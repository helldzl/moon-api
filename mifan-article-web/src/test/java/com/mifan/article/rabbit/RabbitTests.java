package com.mifan.article.rabbit;

import com.mifan.article.AbstractTests;
import org.junit.Test;
import org.moonframework.core.util.IOUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//import com.mifan.sku.service.amqp.MessageEntityPublisher;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/21
 */
public class RabbitTests extends AbstractTests {

    @Autowired
    private RabbitTemplate template;

    public String exchange = "data.direct.exchange";
    private String routingKey = "data.direct.queue";

//    @Autowired
//    private MessageEntityPublisher publisher;
//

    /**
     * <p>发送分类测试数据</p>
     *
     * @throws Exception
     */
//    @Test
//    public void sendTestData() throws Exception {
////        Restrictions.and(
////                Restrictions.gt(BaseEntity.ID, id),
////                Restrictions.le(BaseEntity.ID, 2000));
//        publisher.send(
//                RabbitProperties.SERVER_DATA_EXCHANGE,
//                RabbitProperties.SERVER_DATA_QUEUE,
//                Products.class,
//                id -> Restrictions.gt(BaseEntity.ID, id),
//                meta -> {
//                    meta.put(Resource.META_METHOD, Resource.Method.PATCH);
//                    meta.put("target_classification", "com.mifan.sku.domain.ProductClassification");
//                    meta.put("target_cluster", "com.mifan.sku.domain.ProductCluster");
//                });
//    }
//
//    @Test
//    public void testAmqp() throws Exception {
//        publisher.send(
//                RabbitProperties.SERVER_DATA_EXCHANGE,
//                RabbitProperties.SERVER_DATA_QUEUE,
//                Products.class, id -> Restrictions.and(Restrictions.eq(Products.SEED_ID, 11), Restrictions.gt(BaseEntity.ID, id)),
//                meta -> meta.put(Resource.META_METHOD, Resource.Method.POST));
//    }

    @Test
    public void testSend() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "1");
        map.put("name", "moon");
        map.put("age", "32");

        // Message
        Message message = MessageBuilder
                .withBody(IOUtils.objectToByteArray(map))
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                // .setReceivedExchange(exchange)
                // .setReceivedRoutingKey(routingKey)
                .build();

        // Correlation Data
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId("1");
        template.send(exchange, routingKey, message, correlationData);

        System.out.println("success");
        TimeUnit.SECONDS.sleep(100000);
    }

    @Test
    public void testReceive() {
        // Message receive = ;
        // String s = new String(receive.getBody(), "UTF-8");
        Map<String, Object> map = IOUtils.byteArrayToObject(template.receive("analyzer.queue").getBody());
        System.out.println(map);
    }
}
