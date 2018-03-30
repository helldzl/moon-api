package com.mifan.batch.spider;

import com.mifan.batch.AbstractTests;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/17
 */
public class RabbitFileTests extends AbstractTests {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    public void testFileMsg() throws Exception {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange("spring-boot-exchange");
        template.setRoutingKey("spring-boot");

        try (FileInputStream fileInputStream = new FileInputStream(new File("C:\\Quzile\\Wallpapers\\wall015-1366x768.jpg"))) {
            byte[] b = new byte[fileInputStream.available()];
            fileInputStream.read(b);

            template.send(MessageBuilder
                    .withBody(b)
                    .setContentType(MessageProperties.CONTENT_TYPE_BYTES)
                    //.setReceivedExchange("spring-boot-exchange")
                    //.setReceivedRoutingKey("spring-boot")
                    .build());

            System.out.println();
        }
    }

    @Test
    public void testFile() throws Exception {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange("spring-boot-exchange");
        template.setQueue("spring-boot");
        template.setRoutingKey("spring-boot");

        //Object o = template.receiveAndConvert("spring-boot");

        Message receive = template.receive();

        InputStream uploadInputStream = new ByteArrayInputStream(receive.getBody());
        BufferedImage bufferedImage = ImageIO.read(uploadInputStream);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        System.out.println();

    }
}
