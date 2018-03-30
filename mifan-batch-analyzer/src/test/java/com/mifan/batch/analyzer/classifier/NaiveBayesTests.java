package com.mifan.batch.analyzer.classifier;

import com.mifan.batch.analyzer.AbstractTests;
import org.junit.Test;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/16
 */
public class NaiveBayesTests extends AbstractTests {

    @Test
    public void testA() throws Exception {
        TestNaiveBayesDriverLogger.main(new String[]{
                "-i",
                "/user/hadoop/topic-2/topic-2-test-vectors",
                "-m",
                "/user/hadoop/topic-2/model",
                "-l",
                "/user/hadoop/topic-2/labelindex",
                "-ow",
                "-o",
                "/user/hadoop/topic-2/topic-2-testing",
                "-c"
        });
        System.out.println();
    }
}
