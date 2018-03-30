package com.mifan.support.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/2/7
 */
public class RedisTests extends AbstractTests {

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void name() {
        System.out.println(redisTemplate);
        System.out.println(stringRedisTemplate);
    }

}
