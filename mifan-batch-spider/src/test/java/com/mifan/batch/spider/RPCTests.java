package com.mifan.batch.spider;

import com.mifan.batch.AbstractTests;
import org.junit.Test;
import org.moonframework.core.amqp.Message;
import org.moonframework.core.util.BeanUtils;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/12
 */
public class RPCTests extends AbstractTests {
    // @Autowired
    // @Qualifier("clientTemplate")
    // private RabbitTemplate template;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private Message message;

    @Test
    public void testRPC() {
        Map<String, String> meta = new HashMap<>();
        meta.put("name", "quzile");
        Map<String, Object> result = message.sendAndReceive(meta, null, null);
        System.out.println(result);
    }

    @Test
    public void testRPCSeeds() {
        Map<String, String> meta = new HashMap<>(2);
        meta.put("id","16" );
        meta.put("className", "com.mifan.article.domain.Seeds");
        Map<String, Object> result = message.sendAndReceive(meta,null, null);
        System.out.println(result);
    }


    @Test
    public void test1() {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setRoutingKey(RemoteConfig.QUEUE_NAME);
//        template.setExchange(RemoteConfig.EXCHANGE_NAME);
//        template.setReplyTimeout(10000);
//
//        template.convertAndSend(RemoteConfig.QUEUE_NAME, "hello");
//        Object o = template.receiveAndConvert(RemoteConfig.QUEUE_NAME, 2000);
//        System.out.println(o);

//        try {
//            Long result = service.download("quzile");
//            System.out.println("result : " + result);
//        } catch (Exception e) {
//            Long result = service.download("quzile");
//            System.out.println("result : " + result);
//        }
    }
}
