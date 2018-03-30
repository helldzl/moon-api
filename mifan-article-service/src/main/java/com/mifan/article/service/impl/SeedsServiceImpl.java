package com.mifan.article.service.impl;

import com.mifan.article.dao.SeedsDao;
import com.mifan.article.domain.Seeds;
import com.mifan.article.service.SeedsService;
import org.moonframework.core.util.ObjectMapperFactory;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class SeedsServiceImpl extends AbstractBaseService<Seeds, SeedsDao> implements SeedsService {

    private static final String REDIS_ARTICLE_SEEDS = "article:seeds:%s";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SeedsManagerImpl seedsManagerImpl;

    @Override
    public Seeds findOne(Long id) {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        String key = String.format(REDIS_ARTICLE_SEEDS, id);
        String value = opsForValue.get(key);
        if (value != null) {
            return ObjectMapperFactory.readValue(value, Seeds.class);
        }

        Seeds seed = seedsManagerImpl.findOne(id);
        if (seed != null) {
            opsForValue.set(key, ObjectMapperFactory.writeValueAsString(seed));
        }
        return seed;
    }

    @Override
    public int remove(Long id) {
        int n = seedsManagerImpl.remove(id);
        if (n > 0) {
            clear(id);
        }
        return n;
    }

    @Override
    public int remove(Long id, Criterion criterion) {
        int n = seedsManagerImpl.remove(id, criterion);
        if (n > 0) {
            clear(id);
        }
        return n;
    }

    @Override
    public <S extends Seeds> int update(S entity) {
        int n = seedsManagerImpl.update(entity);
        if (n > 0) {
            clear(entity.getId());
        }
        return n;
    }

    @Override
    public <S extends Seeds> int update(S entity, Criterion criterion) {
        int n = seedsManagerImpl.update(entity, criterion);
        if (n > 0) {
            clear(entity.getId());
        }
        return n;
    }

    @Override
    public Seeds queryForObject(Long id, Iterable<? extends Field> fields) {
        return seedsManagerImpl.queryForObject(id, fields);
    }

    @Override
    public Page<Seeds> getQuestionTemplate(int page, int size) {
        return seedsManagerImpl.getQuestionTemplate(page, size);
    }

    @Override
    public Page<Seeds> getNotWholeTemplate(int page, int size) {
        return seedsManagerImpl.getNotWholeTemplate(page, size);
    }

    private void clear(Long id) {
        redisTemplate.delete(String.format(REDIS_ARTICLE_SEEDS, id));
    }

}
