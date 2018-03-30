package com.mifan.batch.analyzer.classifier;

import com.mifan.batch.analyzer.AbstractTests;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable;
import org.apache.mahout.vectorizer.SparseVectorsFromSequenceFiles;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/20
 */
public class ClassifierTests extends AbstractTests {

    @Test
    public void testA1() {
        Analyzer analyzer = new StandardAnalyzer();
        Classification c = new Classification(new Configuration(), analyzer, "topic-20171117");
        c.run();
        System.out.println();
    }

    @Test
    @Ignore
    public void name() throws Exception {
        SparseVectorsFromSequenceFiles.main(new String[]{"-i", "/user/hadoop/topic-2/topic-2-seq", "-o", "/user/hadoop/topic-2/topic22222", "-lnorm", "-nv", "-ow", "-wt", "tfidf"});
    }

    /**
     * <p>测试分类</p>
     *
     * @throws Exception
     */
    @Test
    public void testClassify() throws Exception {
        Configuration conf = new Configuration();
        FileSystem hdfs = FileSystem.get(conf);
        hdfs.isDirectory(new Path(""));
    }

    public static Map<String, Integer> readDictionary(Configuration configuration, Path path) {
//        Map<String, Integer> dictionary = new HashMap<>();
//        for (Pair<Text, IntWritable> pair : new SequenceFileIterable<Text, IntWritable>(path, true, configuration))
//            dictionary.put(pair.getFirst().toString(), pair.getSecond().get());
        return map(configuration, path, (BiConsumer<Map<String, Integer>, Pair<Text, IntWritable>>) (map, pair) -> map.put(pair.getFirst().toString(), pair.getSecond().get()));
    }

    public static Map<Integer, Long> readDocumentFrequency(Configuration configuration, Path path) {
//        Map<Integer, Long> map = new HashMap<>();
//        for (Pair<IntWritable, LongWritable> pair : new SequenceFileIterable<IntWritable, LongWritable>(path, true, configuration))
//            map.put(pair.getFirst().get(), pair.getSecond().get());
        return map(configuration, path, (BiConsumer<Map<Integer, Long>, Pair<IntWritable, LongWritable>>) (map, pair) -> map.put(pair.getFirst().get(), pair.getSecond().get()));
    }

    public static <T extends Writable, U extends Writable, K, V> Map<K, V> map(Configuration configuration, Path path, BiConsumer<Map<K, V>, Pair<T, U>> consumer) {
        Map<K, V> map = new HashMap<>();
        for (Pair<T, U> pair : new SequenceFileIterable<T, U>(path, true, configuration))
            consumer.accept(map, pair);
        return map;
    }

    @Test
    public void testClassifier() {
        // 目标变量16个类别
        List<String> catetories = Arrays.asList(
                "Accessories",
                "Cables + Plugs",
                "Cases",
                "DJ Equipment",
                "Drums + Percussion",
                "Effects + Signal Proc.",
                "Guitars and Basses",
                "Keys",
                "Lighting + Stage",
                "Microphones",
                "PA Equipment",
                "Sheet",
                "Software",
                "Studio Equipment",
                "Traditional",
                "Wind Instruments"
        );


        ProductCategoryClassifier classifier = new ProductCategoryClassifier(catetories);
        classifier.accept(get());
    }

    private Products get() {
        Products product = new Products();
        return product;
    }

}
