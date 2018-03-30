package com.mifan.batch.analyzer.classifier;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.mifan.batch.analyzer.util.TokenUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.vectorizer.encoders.Dictionary;
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder;
import org.apache.mahout.vectorizer.encoders.StaticWordValueEncoder;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.function.Consumer;

/**
 * <p>产品类别分类器</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2016/11/8
 */
public class ProductCategoryClassifier implements Consumer<Products> {

    protected static Log logger = LogFactory.getLog(ProductCategoryClassifier.class);

    private static final HtmlCompressor compressor = new HtmlCompressor();

    static {
        // default replace all multiple whitespace characters with single spaces.
        // Then compress remove all inter-tag whitespace characters <code>&amp;nbsp;</code>
        compressor.setRemoveIntertagSpaces(true);
    }

    public static String removeHtml(String str) {
        if (str == null)
            return null;
        return compressor.compress(Jsoup.clean(str, Whitelist.none()));
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ObjectMapper getInstance() {
        return objectMapper;
    }

    public static <T> T readValue(String value, Class<T> clazz) throws IOException {
        return objectMapper.readValue(value, clazz);
    }

    public static String writeValueAsString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    private static final int FEATURES = 10000;
    private static Analyzer analyzer = new StandardAnalyzer();

    /**
     * 训练词典
     */
    private Map<String, Set<Integer>> traceDictionary = new TreeMap<>();

    /**
     * 向量编码器
     */
    private Map<String, FeatureVectorEncoder> vectorEncoderMap = new HashMap<>();

    /**
     * 学习算法:在线逻辑回归算法
     */
    private final OnlineLogisticRegression learningAlgorithm;

    /**
     * 将目标变量转成整型值
     */
    private final Dictionary dictionary;

    //

    private double averageLL = 0.0;         // 平均对数似然
    private double averageCorrect = 0.0;    // 正确率 正确平均百分比
    private double averageLineCount = 0.0;
    private int k = 0;
    private double step = 0.0;
    private int[] bumps = new int[]{1, 2, 5};
    private double lineCount = 0;

    //
    public ProductCategoryClassifier(Collection<String> categories) {
        this.dictionary = new Dictionary();
        categories.forEach(category -> dictionary.intern(category));
        this.learningAlgorithm = new OnlineLogisticRegression(dictionary.size(), FEATURES, new L1())
                .alpha(1)               // 学习率
                .stepOffset(1000)       // 衰减方式
                .decayExponent(0.9)     // 衰减率
                .lambda(3.0e-5)         // 正则化权重
                .learningRate(20);      // 初始学习率

        // 文本编码器
        FeatureVectorEncoder textEncoder = new StaticWordValueEncoder("body");
        textEncoder.setProbes(2);
        textEncoder.setTraceDictionary(traceDictionary);

        vectorEncoderMap.put("textEncoder", textEncoder);
    }

    /**
     * <p>读取数据并进行词条化处理</p>
     *
     * @param product product
     */
    @Override
    public void accept(Products product) {
        // 目标类型为空的数据不参与训练
        String category = product.getCategory();
        if (category == null)
            return;

        // 识别目标类型
        String group = category.substring(0, category.indexOf(","));
        int actual = dictionary.intern(group);
        Multiset<String> words = ConcurrentHashMultiset.create();

        count(words, product.getName());                        // 标题
        count(words, product.getDescription());                 // 描述
        count(words, removeHtml(product.getContent()));         // 文本
        toList(product.getFeature()).forEach(map ->             // 特性
                map.forEach((name, value) -> {
                    count(words, name);
                    count(words, removeHtml(value.toString()));
                }));

        // 将所有特征收集到一个特征向量中, 供分类器的学习算法使用
        Vector v = new RandomAccessSparseVector(FEATURES);
        addToVector(v, words);
        train(actual, v, group, product.getId());
    }


    /**
     * <p>数据的向量化</p>
     */
    protected void addToVector(Vector v, Multiset<String> words) {
        // 学习算法利用该特征作为阈值, 如果没有这样一个bias, 有些问题就没法用Logistic回归解决
        // bias.addToVector((String) null, .8, v);
        //addToVector("", null, .8, v);

        // 行数同时编码为原始形式和对数形式, 除以30以便加快学习
        //lines.addToVector((String) null, lineCount / 30, v);
        //logLines.addToVector((String) null, Math.log(lineCount + 1), v);

        //addToVector("", null, Math.log(lineCount + 1), v);
        // 出现频率的对数
        words.forEach(word -> addToVector("textEncoder", word, Math.log(1 + words.count(word)), v));
    }

    protected void train(int actual, Vector v, String group, long id) {
        // 7. 评估当前进度
        double mu = Math.min(k + 1, 200);
        // 对分类器准确度的衡量指标:对数似然
        double ll = learningAlgorithm.logLikelihood(actual, v);
        if (!Double.isNaN(ll)) {
            averageLL = averageLL + (ll - averageLL) / mu;
        }

        Vector p = new DenseVector(16);
        learningAlgorithm.classifyFull(p, v);
        int estimated = p.maxValueIndex(); // 使用比例最高的新闻组确定评估值estimated

        int correct = (estimated == actual ? 1 : 0);
        averageCorrect = averageCorrect + (correct - averageCorrect) / mu; // 正确评分百分比

        // 8. 用编码数据训练SGD模型
        // Updates the model using a particular target variable value and a feature vector.
        // 数据训练
        learningAlgorithm.train(actual, v);
        k++;
        int bump = bumps[(int) Math.floor(step) % bumps.length];
        int scale = (int) Math.pow(10, Math.floor(step / bumps.length));
        if (k % (bump * scale) == 0) {
            step += 0.1;
            System.out.printf("%10d %10.3f %10.3f %10.2f %s %s %s\n",
                    k, ll, averageLL, averageCorrect * 100, group,
                    dictionary.values().get(estimated), id);
        }
        learningAlgorithm.close();
    }

    /**
     * <p>计算文本类型中单词的数量</p>
     *
     * @param words words
     * @param text  text
     */
    private void count(Collection<String> words, String text) {
        try {
            if (text != null)
                TokenUtils.token(analyzer, words, new StringReader(text));
        } catch (IOException ignore) {
        }
    }

    private void addToVector(String name, String originalForm, double weight, Vector data) {
        FeatureVectorEncoder featureVectorEncoder = vectorEncoderMap.get(name);
        if (featureVectorEncoder == null)
            throw new IllegalArgumentException();
        featureVectorEncoder.addToVector(originalForm, weight, data);
    }

    private List<Map<String, Object>> toList(String json) {
        try {
            List<Map<String, Object>> features;
            if (json != null)
                features = readValue(json, List.class);
            else
                throw new IllegalStateException();
            return features;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

}
