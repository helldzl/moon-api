package com.mifan.batch.analyzer.prop;

import org.moonframework.core.util.PropertiesUtils;

import java.util.Properties;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/5/26
 */
public class ReadPropertiesTests {

    public static void main(String[] args) {
        Properties prop = PropertiesUtils.load(ReadPropertiesTests.class, "classification/dictionary.properties");
        System.out.println(prop.size());
    }

}
