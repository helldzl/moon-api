package com.mifan.article.service.impl;

import com.mifan.article.service.RedisService;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.model.mybatis.domain.Pages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/12/6
 */
public abstract class AbstractRedisService implements RedisService {

    protected static final Log logger = LogFactory.getLog(AbstractRedisService.class);

    protected RedisTemplate<String, String> template;

    public AbstractRedisService(RedisTemplate<String, String> template) {
        this.template = template;
    }

    /**
     * <p>分页降序查询</p>
     *
     * @param key  key
     * @param page page
     * @param size size
     * @return Page
     */
    public Page<String> page(String key, int page, int size) {
        if (key == null) {
            return new PageImpl<>(Collections.emptyList());
        }
        ZSetOperations<String, String> opsForZSet = template.opsForZSet();

        double min = currentTimeSeconds() - ONE_WEEK_IN_SECONDS;
        double max = Double.MAX_VALUE;
        long offset = ((page < 1 ? 1 : page) - 1) * size;

        Set<String> result = opsForZSet.reverseRangeByScore(key, min, max, offset, size);
        return new PageImpl<>(new ArrayList<>(result), Pages.builder().page(page).size(size).build(), opsForZSet.count(key, min, max));
    }

    /**
     * @param key    key
     * @param value  value
     * @param delta  delta
     * @param period period
     */
    public void increment(String key, String value, double delta, int period) {
        ZSetOperations<String, String> operations = template.opsForZSet();
        Double result = operations.incrementScore(key, value, delta);
        long current = currentTimeSeconds();
        long t;
        if (result == delta)
            // thread safe
        {
            operations.incrementScore(key, value, current);
        } else if (result < (t = current - period))
            // thread unsafe
        {
            operations.add(key, value, t + ONE_DAY_IN_SECONDS + delta);
        }
    }

    public RedisTemplate<String, String> getTemplate() {
        return template;
    }

    protected long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000L;
    }

}
