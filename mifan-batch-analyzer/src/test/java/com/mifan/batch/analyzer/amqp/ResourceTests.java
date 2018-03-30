package com.mifan.batch.analyzer.amqp;

import com.mifan.batch.analyzer.AbstractTests;
import org.junit.Test;
import org.moonframework.amqp.Data;
import org.moonframework.amqp.Resource;
import org.moonframework.amqp.autoconfigure.RabbitProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/24
 */
public class ResourceTests extends AbstractTests {

    @Autowired
    private RabbitTemplate template;
    @Value("${moon.amqp.rabbit.client-data-queue}")
    private  String client_data_queue;
    @Value("${moon.amqp.rabbit.client-data-exchange}")
    private  String client_data_exchange;
    @Value("${moon.amqp.rabbit.client-event-exchange}")
    private  String client_event_exchange;
    @Value("${moon.amqp.rabbit.client-event-queue}")
    private String  client_event_queue;
    @Value("${moon.amqp.rabbit.server-data-queue}")
    private  String server_data_queue;
    @Value("${moon.amqp.rabbit.server-data-exchange}")
    private  String server_data_exchange;

    @Test
    public void testResource() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 11L);
        attributes.put("name", "quzile");

        Map<String, Object> meta = new HashMap<>();
        meta.put("action", "update");

        Data data = new Data("222", "com.mifan.api.Products");
        data.setAttributes(attributes);
        Resource resource = new Resource(meta, data);

        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(UUID.randomUUID().toString());

        template.convertAndSend(server_data_exchange, server_data_queue, resource, correlationData);

        Object o = template.receiveAndConvert(client_data_queue, 1000);
        Resource result = (Resource) o;
        System.out.println(o.getClass());
        System.out.println(result);

    }

    @Test
    public void testReceive() {
        Object o;
        do {
            o = template.receiveAndConvert(server_data_queue, 1000);
            Resource result = (Resource) o;
            System.out.println(result.getData().getId() + " " + result.getData().getType());

        } while (o != null);


    }
}
