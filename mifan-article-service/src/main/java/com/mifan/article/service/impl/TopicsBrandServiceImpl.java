package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsBrandDao;
import com.mifan.article.domain.TopicsBrand;
import com.mifan.article.service.TopicsBrandService;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class TopicsBrandServiceImpl extends AbstractBaseService<TopicsBrand, TopicsBrandDao> implements TopicsBrandService {

    @Override
    public <S extends TopicsBrand> int save(S entity) {
        int n;
        // save or update
        TopicsBrand one = new TopicsBrand();
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
