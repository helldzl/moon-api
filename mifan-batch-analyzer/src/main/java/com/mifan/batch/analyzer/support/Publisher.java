package com.mifan.batch.analyzer.support;

import org.moonframework.amqp.Resource;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/16
 */
public interface Publisher {

    void send(String id, String type, Consumer<Map<String, Object>> consumer);

    void send(Resource.Method method, String id, String type, Consumer<Map<String, Object>> consumer);

}
