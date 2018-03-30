package com.mifan.article.service.amqp;

import org.moonframework.amqp.MessagePublisher;
import org.moonframework.concurrent.util.LockUtils;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>发送需要分析的数据</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/4/21
 */
public class MessageEntityPublisher extends MessagePublisher {

    private Lock lock = new ReentrantLock();

    public MessageEntityPublisher(RabbitTemplate template) {
        super(template);
    }

    /**
     * <p>根据Criterion向队列发送/推送数据集合</p>
     *
     * @param exchange   exchange
     * @param routingKey routingKey
     * @param clazz      clazz
     * @param function   function
     * @param <T>        T
     */
    public <T extends BaseEntity> void send(String exchange, String routingKey, Class<T> clazz, Function<Long, Criterion> function) {
        send(exchange, routingKey, clazz, function, null);
    }

    public <T extends BaseEntity> void send(String exchange, String routingKey, Class<T> clazz, Function<Long, Criterion> function, Consumer<Map<String, Object>> consumer) {
        LockUtils.tryLock(lock, 5, () -> {
            PageRequest pageRequest = Pages.builder().page(1).size(100).sort(Pages.sortBuilder().add(BaseEntity.ID, true).build()).build();
            Long id = 0L;
            Page<T> page;
            while ((page = Services.findAll(clazz, function.apply(id), pageRequest, false)).hasContent()) {
                for (BaseEntity entity : page) {
                    send(exchange, routingKey, entity, entity.getId().toString(), consumer);
                }
                id = page.getContent().get(page.getNumberOfElements() - 1).getId();
            }
            return true;
        });
    }

    public <T extends BaseEntity> void send(String exchange, String routingKey, Class<T> clazz, Supplier<Criterion> supplier, Consumer<Map<String, Object>> consumer) {
        LockUtils.tryLock(lock, 5, () -> {
            PageRequest pageRequest = Pages.builder().page(1).size(100).sort(Pages.sortBuilder().add(BaseEntity.ID, true).build()).build();
            Page<T> page;
            Criterion criterion;
            while ((criterion = supplier.get()) != null && (page = Services.findAll(clazz, criterion, pageRequest, false)).hasContent()) {
                for (BaseEntity entity : page) {
                    send(exchange, routingKey, entity, entity.getId().toString(), consumer);
                }
            }
            return true;
        });
    }

    //

    public <T extends BaseEntity> void send(String exchange, String routingKey, Class<T> clazz, Long id) {
        send(exchange, routingKey, clazz, id, null);
    }

    public <T extends BaseEntity> void send(String exchange, String routingKey, Class<T> clazz, Long id, Consumer<Map<String, Object>> consumer) {
        T entity = Services.findOne(clazz, id);
        if (entity != null) {
            send(exchange, routingKey, entity, entity.getId().toString(), consumer);
        }
    }

    //

    public <T extends BaseEntity> void send(String exchange, String routingKey, T entity) {
        send(exchange, routingKey, entity, entity.getId().toString(), null);
    }

    public <T extends BaseEntity> void send(String exchange, String routingKey, T entity, Consumer<Map<String, Object>> consumer) {
        send(exchange, routingKey, entity, entity.getId().toString(), consumer);
    }

}
