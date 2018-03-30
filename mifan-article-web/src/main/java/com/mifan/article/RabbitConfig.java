package com.mifan.article;

import com.mifan.article.service.amqp.MessageEntityPublisher;
import com.mifan.article.service.amqp.MessageImpl;
import com.mifan.article.service.amqp.MessageResourceListener;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.amqp.autoconfigure.RabbitProperties;
import org.moonframework.core.amqp.Message;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Server Side</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/4/14
 */
@Configuration
public class RabbitConfig implements InitializingBean {

    protected static Log logger = LogFactory.getLog(RabbitConfig.class);

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private RabbitProperties rabbitProperties;

    @Value("${moon.amqp.rabbit.analyzer-exchange}")
    private String analyzerExchange;
    @Value("${moon.amqp.rabbit.analyzer-queue}")
    private String analyzerQueue;

    @Value("${moon.amqp.rabbit.vpn-seeds-data-queue}")
    private String vpn_seeds_data_queue;
    @Value("${moon.amqp.rabbit.vpn-seeds-exchange}")
    private String vpn_direct_data_exchange;
    @Value("${moon.amqp.rabbit.seeds-data-queue}")
    private String seeds_direct_data_queue;
    @Value("${moon.amqp.rabbit.seeds-exchange}")
    private String seeds_direct_data_exchange;

    //==========队列================//

    @Bean
    public Queue vpnQueue() {
        return new Queue(vpn_seeds_data_queue);
    }

    @Bean
    public Queue notVpnQueue() {
        return new Queue(seeds_direct_data_queue);
    }

    //=========Exchange============//

    @Bean
    public DirectExchange exchangeVpn() {
        return new DirectExchange(vpn_direct_data_exchange);
    }

    @Bean
    public DirectExchange exchangeNotVpn() {
        return new DirectExchange(seeds_direct_data_exchange);
    }

    //=============banding===============//

    @Bean
    Binding bindingExchangeMessages(Queue vpnQueue, DirectExchange exchangeVpn) {
        return BindingBuilder.bind(vpnQueue).to(exchangeVpn).with(vpn_seeds_data_queue);
    }

    @Bean
    Binding bindingNotVpnExchangeMessages(Queue notVpnQueue, DirectExchange exchangeNotVpn) {
        return BindingBuilder.bind(notVpnQueue).to(exchangeNotVpn).with(seeds_direct_data_queue);
    }

    @Override
    public void afterPropertiesSet() {
        template.setConfirmCallback((correlationData, ack, cause) -> {
            String id = correlationData == null ? null : correlationData.getId();
            if (ack) {
                logger.info("Publisher Confirms Successful, ID : " + id);
            } else {
                logger.info("Publisher Confirms Error, ID : " + id);
            }
        });
    }

    @Bean
    public Message message() {
        return new MessageImpl();
    }

    @Bean
    public Queue analyzerDataQueue() {
        return new Queue(analyzerQueue);
    }

    @Bean
    public DirectExchange analyzerDataExchange() {
        return new DirectExchange(analyzerExchange);
    }

    @Bean
    public Binding analyzerDataBinding(
            @Qualifier("analyzerDataQueue") Queue queue,
            @Qualifier("analyzerDataExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(analyzerQueue);
    }

    @Bean
    public MessageEntityPublisher messageEntityPublisher() {
        return new MessageEntityPublisher(template);
    }

    /**
     * <p>消息监听器, 监听客户端发送的消息</p>
     * <p>注册监听事件: 事件 -> 行为, 命名规则: trigger_{method}_{entity}</p>
     *
     * @return MessageResourceListener
     */
    @Bean
    public MessageResourceListener messageResourceListener() {
        return new MessageResourceListener(template, rabbitProperties.getClientDataQueue());
    }

}
