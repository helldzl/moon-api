package com.mifan.batch.spider.filter;

import org.moonframework.crawler.filter.LinkFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.net.URI;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/30
 */
public class RedisFilterAdapter implements LinkFilter {

    private SetOperations<String, String> setOps;

    public RedisFilterAdapter(StringRedisTemplate template) {
        this.setOps = template.opsForSet();
    }

    @Override
    public boolean filter(String type, String url) {
        try {
            URI uri = URI.create(url);
            Long result = setOps.add(uri.getHost() + ":" + type, url);
            return result != 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean exist(String type, String url) {
        URI uri = URI.create(url);
        //true为存在    false为不存在
        return setOps.isMember(uri.getHost() + ":" + type, url);
    }

}
