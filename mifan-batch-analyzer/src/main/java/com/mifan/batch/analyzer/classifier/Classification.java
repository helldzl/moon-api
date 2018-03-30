package com.mifan.batch.analyzer.classifier;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.mifan.batch.analyzer.support.Classifiable;
import com.mifan.batch.analyzer.util.TokenUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.util.ToolRunner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.mahout.classifier.naivebayes.*;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.utils.SplitInput;
import org.apache.mahout.vectorizer.SparseVectorsFromSequenceFiles;
import org.apache.mahout.vectorizer.TFIDF;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.apache.hadoop.io.SequenceFile.createWriter;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/14
 */
public class Classification implements Runnable, AutoCloseable {

    private static Log logger = LogFactory.getLog(Classification.class);

    private static final String FILE_DICTIONARY = "/dictionary.file-0";
    private static final String FILE_DF_COUNT = "/df-count/part-r-00000";
    private static final String BASE_PATH = "/user/hadoop/";
    // Model
    private static final String SUFFIX_SEQ = "-seq";
    private static final String SUFFIX_VECTORS = "-vectors";
    private static final String SUFFIX_TRAIN_VECTORS = "-train-vectors";
    private static final String SUFFIX_TEST_VECTORS = "-test-vectors";
    private static final String SUFFIX_TESTING = "-testing";
    private static final String LABEL_INDEX = "labelindex";
    private static final String MODEL = "model";

    private static TFIDF tfidf = new TFIDF();

    /**
     * Hadoop Config
     */
    private Configuration configuration;

    /**
     * Analyzer
     */
    private Analyzer analyzer;

    /**
     * Name of Classification
     */
    private String name;

    /**
     * Model evaluation result
     */
    private Consumer<String> consumer;

    // Work Path
    private final String path;
    private final String seq;
    private final String vectors;
    private final String trainVectors;
    private final String testVectors;
    private final String model;
    private final String labelIndex;
    private final String testing;

    // Map
    private Map<Integer, String> label = Collections.emptyMap();
    private Map<String, Integer> dictionary = Collections.emptyMap();
    private Map<Integer, Long> document = Collections.emptyMap();

    // Classifier
    private AbstractNaiveBayesClassifier classifier;

    // Lock And Queue
    private Lock lock = new ReentrantLock();
    private BlockingQueue<Classifiable> queue = new LinkedBlockingDeque<>(1000);

    private volatile boolean runnable;

    public Classification(Configuration configuration, Analyzer analyzer, String name) {
        this(configuration, analyzer, name, null);
    }

    public Classification(Configuration configuration, Analyzer analyzer, String name, Consumer<String> consumer) {
        //
        this.configuration = configuration;
        this.analyzer = analyzer;
        this.name = name;
        this.consumer = consumer;
        //
        this.path = BASE_PATH + name + "/";
        this.seq = path + name + SUFFIX_SEQ;
        this.vectors = path + name + SUFFIX_VECTORS;
        this.trainVectors = path + name + SUFFIX_TRAIN_VECTORS;
        this.testVectors = path + name + SUFFIX_TEST_VECTORS;
        this.model = path + MODEL;
        this.labelIndex = path + LABEL_INDEX;
        this.testing = path + name + SUFFIX_TESTING;
    }

    /**
     * <p>贝叶斯分类</p>
     *
     * @param classifiable classifiable
     * @return 目标变量
     */
    public Classifiable classify(Classifiable classifiable) {
        try {
            Multiset<String> words = HashMultiset.create();
            // If the word is not in the dictionary, skip it
            TokenUtils.token(analyzer, new StringReader(classifiable.getValue()), word -> {
                if (dictionary.containsKey(word)) {
                    words.add(word);
                }
            });

            // Create a vector (word => TF*IDF weight)
            Vector vector = new RandomAccessSparseVector(10000);
            int numDocs = document.get(-1).intValue();
            for (Multiset.Entry<String> entry : words.entrySet()) {
                Integer index = dictionary.get(entry.getElement());
                double value = tfidf.calculate(entry.getCount(), document.get(index).intValue(), 0, numDocs);
                vector.setQuick(index, value);
            }

            // Classify Result
            String target = label.get(classifier.classifyFull(vector).maxValueIndex());
            classifiable.setLabel(target);
            classifiable.setEnabled(!Classifiable.USELESS.equals(target));
        } catch (Exception e) {
            classifiable.setLabel(Classifiable.USELESS);
            classifiable.setEnabled(false);
            logger.error("error", e);
        }
        return classifiable;
    }

    /**
     * <p>判断模型是否存在</p>
     *
     * @return boolean
     */
    public boolean exists() {
        try (FileSystem fileSystem = FileSystem.get(configuration)) {
            return fileSystem.exists(new Path(model));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>删除模型相关文件</p>
     *
     * @return boolean
     */
    public boolean delete() {
        try (FileSystem fileSystem = FileSystem.get(configuration)) {
            return fileSystem.delete(new Path(path), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>提供原始数据, 当超过n秒内没有提供新的数据时, 分类器将把之前某一连续时间段内的数据作为模型的整体训练数据.</p>
     *
     * @param classifiable 可分类数据
     */
    public void put(Classifiable classifiable) {
        if (classifiable != null) {
            try {
                queue.put(classifiable);
            } catch (InterruptedException e) {
                logger.error("error", e);
            }
        }
    }

    /**
     * <p>可超时的从阻塞队列中获取可分类数据</p>
     *
     * @return Classifiable
     */
    public Classifiable poll() {
        try {
            return queue.poll(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        delete();
    }

    public boolean runnable() {
        if (!runnable) {
            synchronized (this) {
                if (!runnable) {
                    runnable = true;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    logger.info("STEP 1 : Creating sequence files");
                    sequence(configuration, seq, this::poll);

                    // bin/mahout seq2sparse -h
                    // --analyzerName (-a) analyzerName : The class name of the analyzer
                    logger.info("STEP 2 : Converting sequence files to vectors");
                    seq2sparse("-i", seq, "-o", vectors, "-lnorm", "-nv", "-ow", "-wt", "tfidf", "-a", analyzer.getClass().getName());

                    logger.info("STEP 3 : Creating training and holdout set with a random N-M split of the generated vector data set");
                    split("-i", vectors + "/tfidf-vectors", "--trainingOutput", trainVectors, "--testOutput", testVectors, "--randomSelectionPct", "20", "--overwrite", "--sequenceFiles", "-xm", "sequential");

                    logger.info("STEP 4 : Training Naive Bayes model");
                    train("-i", trainVectors, "-o", model, "-li", labelIndex, "-c", "-ow");

                    logger.info("STEP 5 : Testing");
                    String result = test("-i", testVectors, "-m", model, "-l", labelIndex, "-ow", "-o", testing, "-c");

                    logger.info("Train finished");
                    load();

                    consumer.accept(result);
                } catch (Exception e) {
                    logger.error("error", e);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException ignore) {
        } finally {
            runnable = false;
        }
    }

    /**
     * <p>装载模型</p>
     */
    public boolean load() {
        try {
            if (exists()) {
                label = BayesUtils.readLabelIndex(configuration, new Path(labelIndex));
                dictionary = map(configuration, new Path(vectors + FILE_DICTIONARY), (BiConsumer<Map<String, Integer>, Pair<Text, IntWritable>>) (map, pair) -> map.put(pair.getFirst().toString(), pair.getSecond().get()));
                document = map(configuration, new Path(vectors + FILE_DF_COUNT), (BiConsumer<Map<Integer, Long>, Pair<IntWritable, LongWritable>>) (map, pair) -> map.put(pair.getFirst().get(), pair.getSecond().get()));

                // naive bayes model
                NaiveBayesModel bayesModel = NaiveBayesModel.materialize(new Path(model), configuration);
                if (bayesModel.isComplemtary()) {
                    this.classifier = new ComplementaryNaiveBayesClassifier(bayesModel);
                } else {
                    this.classifier = new StandardNaiveBayesClassifier(bayesModel);
                }

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Consumer<String> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    /**
     * <p>STEP 1 : Creating sequence files</p>
     *
     * @param path path
     */
    public static void sequence(Configuration configuration, String path, Supplier<Classifiable> supplier) {
        Classifiable classifiable;
        try (SequenceFile.Writer writer = createWriter(
                configuration,
                SequenceFile.Writer.file(new Path(path)),
                SequenceFile.Writer.keyClass(Text.class),
                SequenceFile.Writer.valueClass(Text.class))) {
            while ((classifiable = supplier.get()) != null) {
                writer.append(new Text(String.format("/%s/%s", classifiable.getLabel(), classifiable.getId())), new Text(classifiable.getValue()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>STEP 2 : Converting sequence files to vectors</p>
     * <p>将sequence file转换成稀疏向量</p>
     * <ul>
     * <li>--input (-i) input : Path to job input directory.</li>
     * <li>--output (-o) output : The directory pathname for output.</li>
     * <li>--overwrite (-ow) : If set, overwrite the output directory.</li>
     * <li>--namedVector (-nv) : (Optional) Whether output vectors should be NamedVectors. If set true else false </li>
     * <li>--logNormalize (-lnorm) : (Optional) Whether output vectors should be logNormalize. If set true else false</li>
     * <li>--weight (-wt) weight : The kind of weight to use. Currently TF or TFIDF. Default: TFIDF</li>
     * </ul>
     *
     * @param args args
     * @throws Exception Exception
     */
    public static void seq2sparse(String... args) throws Exception {
        SparseVectorsFromSequenceFiles.main(args);
    }

    /**
     * <p>STEP 3 : Creating training and holdout set with a random N-M split of the generated vector data set</p>
     * <p>随机切分训练样本到训练数据与测试数据</p>
     *
     * @param args args
     * @throws Exception Exception
     */
    public static void split(String... args) throws Exception {
        SplitInput.main(args);
    }

    /**
     * <p>STEP 4 : Training Naive Bayes model</p>
     *
     * @param args args
     * @throws Exception Exception
     */
    public static void train(String... args) throws Exception {
        TrainNaiveBayesJob.main(args);
    }

    /**
     * <p>STEP 5 : Testing</p>
     * <p>在控制台输出混淆矩阵, 分析分类结果, 进行调优</p>
     * <p>会基于训练数据和测试数据两种数据进行测试</p>
     *
     * @param args args
     * @throws Exception Exception
     */
    public static String test(String... args) throws Exception {
        // TestNaiveBayesDriver.main(args);
        TestNaiveBayesDriverLogger driver = new TestNaiveBayesDriverLogger();
        ToolRunner.run(new Configuration(), driver, args);
        return driver.getResult();
    }

    /**
     * @param configuration configuration
     * @param path          path
     * @param consumer      consumer
     * @param <T>           T
     * @param <U>           U
     * @param <K>           K
     * @param <V>           V
     * @return map
     */
    public static <T extends Writable, U extends Writable, K, V> Map<K, V> map(Configuration configuration, Path path, BiConsumer<Map<K, V>, Pair<T, U>> consumer) {
        Map<K, V> map = new HashMap<>(16);
        for (Pair<T, U> pair : new SequenceFileIterable<T, U>(path, true, configuration)) {
            consumer.accept(map, pair);
        }
        return map;
    }

}
