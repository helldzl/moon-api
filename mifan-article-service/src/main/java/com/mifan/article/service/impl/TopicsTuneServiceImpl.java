package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsTuneDao;
import com.mifan.article.domain.TopicsTune;
import com.mifan.article.service.TopicsTuneService;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-01-05
 */
@Service
public class TopicsTuneServiceImpl extends AbstractBaseService<TopicsTune, TopicsTuneDao> implements TopicsTuneService {

    @Override
    public <S extends TopicsTune> int save(S entity) {
        int n;
        //  save or update
        TopicsTune one = new TopicsTune();
        one.setTopicId(entity.getTopicId());
        one = super.findOne(one, Fields.builder().add(BaseEntity.ID).build());
        if (one == null) {
            n = super.save(entity);
        } else {
            entity.setId(one.getId());
            n = super.update(entity);
        }
        return n;
    }
}
