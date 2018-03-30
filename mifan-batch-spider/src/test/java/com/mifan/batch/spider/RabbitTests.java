package com.mifan.batch.spider;

import com.mifan.batch.AbstractTests;
import org.junit.Test;
import org.moonframework.core.util.IOUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/11
 */
public class RabbitTests extends AbstractTests {

    // rabbit mq template
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ConfigurableApplicationContext context;     // application context

    @Autowired
    private ConnectionFactory connectionFactory;

    public final static String ROUTE = "spring-boot-test";

    @Test
    public void testAtomicSendAndReceiveWithConversionAndMessagePostProcessorUsingRoutingKey2() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        // Set up a consumer to respond to our producer
        Future<Long> received = executor.submit(() -> {
            Message message = null;
            for (int i = 0; i < 10; i++) {
                message = this.template.receive(ROUTE);
                if (message != null)
                    break;
                Thread.sleep(100L);
            }
            assertNotNull("No message received", message);

            // 模拟耗时任务
            System.out.println("开始耗时任务");
            Thread.sleep(8000);
            System.out.println("结束耗时任务");

            // 发送回复消息
            //Message reply = MessageBuilder.withBody("1a".getBytes()).build();
            //Products product = byteArrayToObject(message.getBody());
            System.out.println(String.format("接收到的消息: %s", new String(message.getBody(), "UTF-8")));

            message.getMessageProperties().getHeaders().put("id", 22L);
            this.template.send(message.getMessageProperties().getReplyTo(), message);
            // return ;
            return 1L;
        });

        RabbitTemplate template = createSendAndReceiveRabbitTemplate(this.connectionFactory);

        // build message
        String msg = "hello ";
        Message message = MessageBuilder.withBody(IOUtils.objectToByteArray(msg)).build();

        // send and receive
        long start = System.currentTimeMillis();
        Message reply = template.sendAndReceive(ROUTE, message);
        System.out.println(String.format("%s ms", System.currentTimeMillis() - start));

        System.out.println("product name : " + msg);
        System.out.println("ID:" + reply.getMessageProperties().getHeaders().get("id"));

        // assertEquals(1L, received.get(100000, TimeUnit.MILLISECONDS).longValue());
        // assertEquals(1L, result.longValue());
        // Message was consumed so nothing left on queue
        // result = (Long) template.receiveAndConvert(ROUTE);
        // assertEquals(null, result);

        System.out.println("结束1");
        TimeUnit.SECONDS.sleep(10);
        System.out.println("结束2");
    }

    @Test
    public void testAtomicSendAndReceiveWithConversionAndMessagePostProcessorUsingRoutingKey() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        // Set up a consumer to respond to our producer
        Future<Long> received = executor.submit(() -> {
            Message message = null;
            for (int i = 0; i < 10; i++) {
                message = this.template.receive(ROUTE);
                if (message != null)
                    break;
                Thread.sleep(100L);
            }
            assertNotNull("No message received", message);

            // 模拟耗时任务
            System.out.println("开始耗时任务");
            Thread.sleep(8000);
            System.out.println("结束耗时任务");

            // 发送回复消息
            //Message reply = MessageBuilder.withBody("1a".getBytes()).build();
            String m = (String) this.template.getMessageConverter().fromMessage(message);
            System.out.println(String.format("接收到的消息: %s", m));
            this.template.convertAndSend(message.getMessageProperties().getReplyTo(), 1L);
            // return ;
            return 1L;
        });
        RabbitTemplate template = createSendAndReceiveRabbitTemplate(this.connectionFactory);
        long start = System.currentTimeMillis();
        Long result = (Long) template.convertSendAndReceive(ROUTE, "message", message -> {
            try {
                byte[] newBody = ("POST " + new String(message.getBody(), "UTF-8").toUpperCase()).getBytes("UTF-8");
                return new Message(newBody, message.getMessageProperties());
            } catch (Exception e) {
                throw new AmqpException("unexpected failure in test", e);
            }
        });
        System.out.println(String.format("%s ms", System.currentTimeMillis() - start));

        assertEquals(1L, received.get(100000, TimeUnit.MILLISECONDS).longValue());
        assertEquals(1L, result.longValue());
        // Message was consumed so nothing left on queue
        result = (Long) template.receiveAndConvert(ROUTE);
        assertEquals(null, result);

        System.out.println("结束1");
        TimeUnit.SECONDS.sleep(10);
        System.out.println("结束2");
    }

    @Test
    public void testRabbit01() throws Exception {
//        products.setName("convertSendAndReceive!!");
//        Object o = rabbitTemplate.convertSendAndReceive(products);
        Message message = template.sendAndReceive(MessageBuilder
                .withBody("ni hao ya".getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                .setReceivedExchange("spring-boot-exchange")
                .setReceivedRoutingKey("spring-boot")
                .build());
        System.out.println(message);

        TimeUnit.SECONDS.sleep(10);

    }

    @Test
    public void testRabbit02() {
        // 发送消息
        System.out.println("Sending message...");
        // template.convertAndSend(SpiderConfig.queueName, "Hello from RabbitMQ!");

        //
        // template.send(SpiderConfig.queueName, MessageBuilder.withBody("hello".getBytes()).build());

        // 等待消息消费者接收消息
        // receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        // context.close();
    }

    protected RabbitTemplate createSendAndReceiveRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setReplyTimeout(10000);
        return template;
    }

}
