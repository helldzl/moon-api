package com.mifan.batch.analyzer.clustering;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.hash.HashFunction;
import com.mifan.batch.analyzer.util.TokenUtils;
import org.apache.lucene.analysis.Analyzer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/6/8
 */
public class FeatureVector {

    /**
     * 分析器
     */
    private Analyzer analyzer;

    /**
     * hash function
     */
    private HashFunction hashFunction;

    /**
     * 选择的特征维度
     */
    private int n;

    /**
     * [可选], 权重计算函数
     */
    private ToDoubleBiFunction<String, Integer> function;

    public FeatureVector(Analyzer analyzer, HashFunction hashFunction) {
        this(analyzer, hashFunction, 64, null);
    }

    public FeatureVector(Analyzer analyzer, HashFunction hashFunction, int n) {
        this(analyzer, hashFunction, n, null);
    }

    public FeatureVector(Analyzer analyzer, HashFunction hashFunction, int n, ToDoubleBiFunction<String, Integer> function) {
        this.analyzer = analyzer;
        this.hashFunction = hashFunction;
        this.n = n;
        this.function = function;
    }

    public long simHash(String text) {
        try {
            Multiset<String> set = HashMultiset.create();
            TokenUtils.token(analyzer, new StringReader(text), set::add);
            return SimHash.hash(set.entrySet()
                    .stream()
                    .map(entry -> new SimHash.Feature(entry.getElement(), hash(entry.getElement()), weight(entry)))
                    .sorted(Comparator.reverseOrder())
                    .limit(n)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long hash(String s) {
        return hashFunction.newHasher().putString(s, Charset.defaultCharset()).hash().asLong();
    }

    private double weight(Multiset.Entry<String> entry) {
        if (function != null) {
            return function.applyAsDouble(entry.getElement(), entry.getCount());
        } else {
            return Math.sqrt(entry.getCount());
        }
    }

}
