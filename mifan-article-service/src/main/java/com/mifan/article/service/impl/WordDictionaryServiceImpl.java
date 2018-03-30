package com.mifan.article.service.impl;

import com.mifan.article.dao.WordDictionaryDao;
import com.mifan.article.domain.WordDictionary;
import com.mifan.article.service.WordDictionaryService;
import com.mifan.article.service.util.EntityUtils;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-10-31
 */
@Service
public class WordDictionaryServiceImpl extends AbstractBaseService<WordDictionary, WordDictionaryDao> implements WordDictionaryService {

    public static final String REDIS_ARTICLE_DICTIONARY = "article:dictionary";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private WordDictionaryManagerImpl wordDictionaryManagerImpl;

    @Override
    public <S extends WordDictionary> int save(S entity) {
        hash(entity);
        int n = wordDictionaryManagerImpl.save(entity);
        if (n > 0) {
            reset();
        }
        return n;
    }

    @Override
    public int remove(Long id) {
        int n = wordDictionaryManagerImpl.remove(id);
        if (n > 0) {
            reset();
        }
        return n;
    }

    @Override
    public int remove(Long id, Criterion criterion) {
        int n = wordDictionaryManagerImpl.remove(id, criterion);
        if (n > 0) {
            reset();
        }
        return n;
    }

    @Override
    public <S extends WordDictionary> int update(S entity) {
        hash(entity);
        int n = wordDictionaryManagerImpl.update(entity);
        if (n > 0) {
            reset();
        }
        return n;
    }

    @Override
    public <S extends WordDictionary> int update(S entity, Criterion criterion) {
        hash(entity);
        int n = wordDictionaryManagerImpl.update(entity, criterion);
        if (n > 0) {
            reset();
        }
        return n;
    }

    @Override
    public Page<WordDictionary> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        if (fields == null) {
            fields = WordDictionary.DEFAULT_FIELDS;
        }
        return wordDictionaryManagerImpl.findAll(criterion, pageable, fields);
    }

    @Override
    public WordDictionary findOne(WordDictionary entity) {
        hash(entity);
        return wordDictionaryManagerImpl.findOne(entity);
    }

    @Override
    public Map<String, String> dictionary() {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        Map<String, String> dictionary = opsForHash.entries(REDIS_ARTICLE_DICTIONARY);
        if (CollectionUtils.isEmpty(dictionary)) {
            dictionary = wordDictionaryManagerImpl.dictionary();
            redisTemplate.opsForHash().putAll(REDIS_ARTICLE_DICTIONARY, dictionary);
        }
        return dictionary;
    }

    private void reset() {
        redisTemplate.delete(REDIS_ARTICLE_DICTIONARY);
    }

    private void hash(WordDictionary entity) {
        String en = entity.getWordEn();
        if (en != null) {
            en = en.trim();
            entity.setWordEn(en);
            entity.setWordEnHash(EntityUtils.asLong(en));
        }

        String cn = entity.getWordCn();
        if (cn != null) {
            cn = entity.getWordCn().trim();
            entity.setWordCn(cn);
            entity.setWordCnHash(EntityUtils.asLong(cn));
        }
    }

}
