package com.mifan.batch.spider.storage;

import org.moonframework.crawler.storage.PersistenceAdapter;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/16
 */
public class DataPersistence extends PersistenceAdapter {

    private StringRedisTemplate template;

    public DataPersistence(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    protected boolean exists(String type, String identity) {
        // TODO 可选 线下缓存
        return false;
    }

    @Override
    protected void visited(String name, String type, String url) {
        // TODO 可选 线下缓存
    }

}
