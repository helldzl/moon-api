package com.mifan.batch.analyzer.classifier;

import com.mifan.batch.analyzer.AbstractTests;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/8
 */
public class RabbitTemplateTests extends AbstractTests {

    @Autowired
    private RabbitTemplate template;

    @Test
    public void testTemplate() {
        Object o = template.receiveAndConvert("server.data.direct.queue4");
        System.out.println(o);
    }
}
