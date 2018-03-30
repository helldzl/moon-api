package com.mifan.article.article;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mifan.article.AbstractTests;
import com.mifan.article.dao.TopicsDao;
import com.mifan.article.domain.Topics;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by LiuKai on 2017/7/6.
 */
public class TitleHashTest extends AbstractTests {
    private static final HashFunction md5 = Hashing.md5();
    @Autowired
    private TopicsDao  topicsDao;

    @Test
    public  void saveTitleHash(){
        Topics topics1= new Topics();
        System.out.println(topicsDao.findAll(topics1).size());
        List<Topics> topicsList =topicsDao.findAll(topics1);

        for (Topics topics:topicsList) {
            topics.setTitleHash(TitleHashTest.asLong(topics.getTitle()));
            topicsDao.saveOrUpdate(topics);
        }
    }

    public static long asLong(String s) {
        return md5.newHasher().putString(s, Charset.defaultCharset()).hash().asLong();
    }
}
