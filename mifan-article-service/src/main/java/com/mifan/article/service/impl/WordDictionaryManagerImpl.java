package com.mifan.article.service.impl;

import com.mifan.article.dao.WordDictionaryDao;
import com.mifan.article.domain.WordDictionary;
import com.mifan.article.service.WordDictionaryService;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
class WordDictionaryManagerImpl extends AbstractBaseService<WordDictionary, WordDictionaryDao> implements WordDictionaryService {

    @Override
    public <S extends WordDictionary> int save(S entity) {
        WordDictionary dictionary = new WordDictionary();
        dictionary.setWordEn(entity.getWordEn());
        dictionary.setWordEnHash(entity.getWordEnHash());
        if (exists(dictionary)) {
            throw new IllegalArgumentException("已经存在该单词, 不能再次添加");
        }
        return super.save(entity);
    }

    @Override
    public int remove(Long id) {
        return super.remove(id);
    }

    @Override
    public int remove(Long id, Criterion criterion) {
        return super.remove(id, criterion);
    }

    @Override
    public <S extends WordDictionary> int update(S entity) {
        return super.update(entity);
    }

    @Override
    public <S extends WordDictionary> int update(S entity, Criterion criterion) {
        return super.update(entity, criterion);
    }

    @Override
    public Page<WordDictionary> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        return super.findAll(criterion, pageable, fields);
    }

    @Override
    public WordDictionary findOne(WordDictionary entity) {
        return super.findOne(entity);
    }

    /**
     * <p>比较特殊的方法, 只对缓存词典API有用, 其他API不要调用这个方法</p>
     *
     * @return map
     */
    @Override
    public Map<String, String> dictionary() {
        logger.info("词典缓存初始化");
        Pageable pageable = Pages.builder().page(1).size(10000).build();
        List<Field> fields = Fields.builder()
                .add(BaseEntity.ID)
                .add(WordDictionary.WORD_EN)
                .add(WordDictionary.WORD_CN)
                .build();
        Page<WordDictionary> page = Services.findAll(WordDictionary.class, Restrictions.eq(BaseEntity.ENABLED, 1), pageable, fields);

        Map<String, String> result = new HashMap<>(16);
        for (WordDictionary dictionary : page) {
            result.put(dictionary.getWordEn(), dictionary.getWordCn());
            result.put(dictionary.getWordCn(), dictionary.getWordEn());
        }
        return result;
    }

}
