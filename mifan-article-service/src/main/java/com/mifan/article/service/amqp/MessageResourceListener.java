package com.mifan.article.service.amqp;

import org.moonframework.amqp.AbstractResourceMessageListener;
import org.moonframework.amqp.Data;
import org.moonframework.amqp.Resource;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.function.BiConsumer;

import static org.moonframework.amqp.Resource.Method;

/**
 * <p>消息监听器, 用于接收消息, 监听MQ队列投递过来的消息, 并将之转换为BaseEntity</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/4/24
 */
public class MessageResourceListener extends AbstractResourceMessageListener {

    public MessageResourceListener(RabbitTemplate template, String queueName) {
        super(template, queueName);
        put(Method.POST, resource -> invoke(resource, Services::save));
        put(Method.PATCH, resource -> invoke(resource, Services::update));
    }

    @SuppressWarnings("unchecked")
    private void invoke(Resource resource, BiConsumer<Class<BaseEntity>, BaseEntity> consumer) {
        try {
            Data data = resource.getData();
            Object target = Class.forName(data.getType()).newInstance();
            if (target instanceof BaseEntity) {
                // data bind
                BeanWrapper wrapper = new BeanWrapperImpl(target);
                wrapper.setPropertyValues(data.getAttributes());
                // accept
                consumer.accept((Class<BaseEntity>) target.getClass(), (BaseEntity) target);
            }
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
