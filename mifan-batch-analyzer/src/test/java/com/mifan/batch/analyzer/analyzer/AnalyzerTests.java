package com.mifan.batch.analyzer.analyzer;

import com.mifan.batch.analyzer.AbstractTests;
import com.mifan.batch.analyzer.util.TokenUtils;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.StringReader;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/22
 */
public class AnalyzerTests extends AbstractTests {

    @Test
    public void testAnalyzer() throws Exception {
        IKAnalyzer analyzer = new IKAnalyzer(true);
        String text = "曲子乐是好人";
        TokenUtils.token(analyzer, new StringReader(text), System.out::println);
    }

}
