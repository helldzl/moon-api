package com.mifan.batch.spider.listener;


import com.mifan.batch.spider.service.FetcherService;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.amqp.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/25
 */
public class MessageResourceListener extends MessageListener<Resource> implements ApplicationContextAware {
    @Autowired
    private FetcherService fetcherService;

    protected static final Log logger = LogFactory.getLog(MessageResourceListener.class);
    private ApplicationContext applicationContext;

    public MessageResourceListener(RabbitTemplate template, String queueName) {
        super(template, queueName);
    }

    @Override
    protected void accept(Resource resource) {
        if (resource.getData() != null) {
            fetcherService.fetch(resource.getData());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
