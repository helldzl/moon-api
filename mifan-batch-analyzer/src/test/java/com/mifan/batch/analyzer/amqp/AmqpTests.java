package com.mifan.batch.analyzer.amqp;

import com.mifan.batch.analyzer.AbstractTests;
import org.junit.Test;
import org.moonframework.amqp.Resource;
import org.moonframework.amqp.autoconfigure.RabbitProperties;
import org.moonframework.core.util.IOUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/21
 */
public class AmqpTests extends AbstractTests {

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

    @Autowired
    private Environment env;


    @Test
    public void testSendAndReceiveClientEvent() {
        Map<String, Object> map = new HashMap<>();
        map.put("event", "1");
        map.put("name", "moon");
        map.put("age", "32");

        // Send Message
        Message message = MessageBuilder.withBody(IOUtils.objectToByteArray(map)).build();
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(UUID.randomUUID().toString());
        template.send(client_event_exchange, client_event_queue, message, correlationData);

        // Receive Message
//        Message receive = template.receive(RabbitProperties.CLIENT_EVENT_QUEUE, 1000);
//        Map<String, Object> result = IOUtils.byteArrayToObject(receive.getBody());
//        System.out.println(result);
    }

    /**
     * <p>发送获取产品训练数据的事件</p>
     *
     * @throws Exception
     */
    @Test
    public void testSendEvent() {
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(UUID.randomUUID().toString());

        Map<String, Object> meta = new HashMap<>();
        meta.put(Resource.META_DATETIME, LocalDateTime.now());
        meta.put(Resource.META_EVENT, "trigger_post_topics");

        Resource resource = new Resource(meta, null);
        template.convertAndSend(client_data_exchange, client_data_queue, resource, correlationData);
    }

    /**
     * <p>发送获取产品全量数据数据的事件</p>
     *
     * @throws Exception
     */
    @Test
    public void testSendEventFull() {
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(UUID.randomUUID().toString());

        Map<String, Object> meta = new HashMap<>();
        meta.put(Resource.META_DATETIME, LocalDateTime.now());
        meta.put(Resource.META_EVENT, "trigger_patch_topics");

        Resource resource = new Resource(meta, null);
        //template.convertAndSend(RabbitProperties.CLIENT_DATA_EXCHANGE, RabbitProperties.CLIENT_DATA_QUEUE, resource, correlationData);
        template.convertAndSend(client_data_exchange, client_data_queue, resource, correlationData);
    }
    @Test
    public void testApplication(){
        System.out.println(env.getProperty("moon.amqp.rabbit.client-event-queue"));
    }

}
