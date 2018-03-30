package com.mifan.batch.analyzer.classifier;

import com.mifan.batch.analyzer.util.TokenUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.StringReader;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/5/26
 */
public class AnalyzerTests {

    public static void main(String[] args) throws Exception {
        String s = "Analyzers are really pretty simple to put together. analytical, KKJUY-dd";
        Analyzer analyzer = new StandardAnalyzer();
        TokenUtils.token(analyzer, new StringReader(s), s1 -> {
            System.out.println(s1);
        });
    }

}
