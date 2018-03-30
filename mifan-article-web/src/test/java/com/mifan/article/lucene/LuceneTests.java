package com.mifan.article.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.moonframework.crawler.lucene.WordSegment;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/19
 */
public class LuceneTests {

    public static void main(String[] args) {
        List<Lexeme> 你好啊啊啊啊 = WordSegment.segment("你好啊啊啊啊");
        Analyzer analyzer = new IKAnalyzer();
    }

}
