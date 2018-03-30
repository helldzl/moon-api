package com.mifan.batch.spider;


import com.mifan.batch.spider.listener.MessageResourceListener;
import org.moonframework.core.amqp.Message;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/25
 */
@Configuration
public class RabbitConfig {
    @Autowired
    private RabbitTemplate template;
    @Value("${moon.amqp.rabbit.vpn-seeds-data-queue}")
    private  String vpn_seeds_data_queue;
    @Value("${moon.amqp.rabbit.vpn-seeds-exchange}")
    private  String vpn_direct_data_exchange;
    @Value("${moon.amqp.rabbit.seeds-data-queue}")
    private  String seeds_direct_data_queue;
    @Value("${moon.amqp.rabbit.seeds-exchange}")
    private  String seeds_direct_data_exchange;
    @Value("${spider-seeds-queue}")
    private  String spider_seeds_queue;
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
    @Bean
    public MessageResourceListener messageResourceListener() {
        // 监听服务端发送消息的队列, 一般是待分析的数据
        return new MessageResourceListener(template, spider_seeds_queue);
    }
}
