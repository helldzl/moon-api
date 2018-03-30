package com.mifan.batch.spider;

import com.mifan.batch.AbstractTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Created by LiuKai on 2017/11/9.
 */
public class RedisTest extends AbstractTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedisDelete() {
        redisTemplate.hasKey("www.music123.com:products");
        redisTemplate.keys("*123*");
    }
}
