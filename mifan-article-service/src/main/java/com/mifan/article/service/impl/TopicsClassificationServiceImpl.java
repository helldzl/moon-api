package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsClassificationDao;
import com.mifan.article.domain.TopicsClassification;
import com.mifan.article.service.TopicsClassificationService;
import com.mifan.article.service.WordDictionaryService;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.mifan.article.domain.support.Multilingual.Language;
import static com.mifan.article.domain.support.Multilingual.getList;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class TopicsClassificationServiceImpl extends AbstractBaseService<TopicsClassification, TopicsClassificationDao> implements TopicsClassificationService {

    @Autowired
    @Qualifier("wordDictionaryServiceImpl")
    private WordDictionaryService wordDictionaryService;

    /**
     * <p>从词典中自动翻译类别</p>
     *
     * @param entity entity
     * @param <S>    S
     * @return int
     */
    @Override
    public <S extends TopicsClassification> int update(S entity) {
        String targetVariable = entity.getTargetVariable();
        if (targetVariable != null) {
            List<Map<String, String>> list = getList(targetVariable);
            if (!list.isEmpty()) {
                for (Map<String, String> map : list) {
                    Language.translate(wordDictionaryService.dictionary(), map);
                }
                entity.setTargetVariable(BeanUtils.writeValueAsString(list));
            }
        }
        return super.update(entity);
    }
}
