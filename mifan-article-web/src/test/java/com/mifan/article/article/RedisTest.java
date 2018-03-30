package com.mifan.article.article;

import com.mifan.article.AbstractTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.Set;

/**
 * Created by LiuKai on 2017/12/27.
 */
public class RedisTest extends AbstractTests {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public  void test001(){
        SetOperations<String,String> setOperations = redisTemplate.opsForSet();
        setOperations.add("a","1","2");
        setOperations.add("a","1","2","3");
        System.out.println( "执行成功");
    }
    @Test
    public void test002(){
        SetOperations<String,String> setOperations = redisTemplate.opsForSet();
        setOperations.add("a","1","2","2223","2eff");
        Set set=setOperations.members("a");

        for (Object object :set){
            System.out.println(object.toString());
        }
        System.out.println("执行完毕");
        for (int i=0;i<2;i++)
        System.out.println(setOperations.pop("a"));
    }
}
