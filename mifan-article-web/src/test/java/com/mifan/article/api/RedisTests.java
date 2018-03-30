package com.mifan.article.api;

import com.mifan.article.AbstractTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/10
 */
public class RedisTests extends AbstractTests {

    @Autowired
    private StringRedisTemplate template;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void testCache() {
        System.out.println(template);
    }
}
