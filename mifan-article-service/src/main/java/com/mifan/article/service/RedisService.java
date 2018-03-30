package com.mifan.article.service;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/12/6
 */
public interface RedisService {

    int ONE_DAY_IN_SECONDS = 86400;

    int ONE_WEEK_IN_SECONDS = 7 * ONE_DAY_IN_SECONDS;

    int TWO_WEEK_IN_SECONDS = 14 * ONE_DAY_IN_SECONDS;

    int ONE_MONTH_IN_SECONDS = 30 * ONE_DAY_IN_SECONDS;

    /**
     * <p>Redis provides support for transactions through the multi, exec, and discard commands.</p>
     *
     * @param template template
     * @param function function
     * @param <K>      K
     * @param <V>      V
     * @return result
     */
    static <K, V> List<Object> execute(RedisTemplate<K, V> template, Function<RedisOperations, List<Object>> function) {
        return template.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                return function.apply(operations);
            }
        });
    }

    /**
     * @param template template
     * @param consumer consumer
     * @param <K>      K
     * @param <V>      V
     * @return result
     */
    @SuppressWarnings("unchecked")
    static <K, V> List<Object> execute(RedisTemplate<K, V> template, Consumer<RedisOperations> consumer) {
        return template.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                // start transaction
                operations.multi();
                consumer.accept(operations);
                // execute transaction
                return operations.exec();
            }
        });
    }

    /**
     * <p>execute until success or timeout</p>
     *
     * @param template template
     * @param function function
     * @param timeout  timeout
     * @param unit     time unit
     * @param <K>      K
     * @param <V>      V
     * @return result
     */
    static <K, V> List<Object> execute(RedisTemplate<K, V> template, Function<RedisOperations, List<Object>> function, long timeout, TimeUnit unit) {
        List<Object> result;
        long millis = System.currentTimeMillis() + unit.toMillis(timeout);
        do {
            result = execute(template, function);
        } while (result == null && System.currentTimeMillis() < millis);
        return result;
    }

}
