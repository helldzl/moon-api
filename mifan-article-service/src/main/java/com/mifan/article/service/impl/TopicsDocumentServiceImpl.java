package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsDocumentDao;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.TopicsDocument;
import com.mifan.article.service.TopicsDocumentService;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class TopicsDocumentServiceImpl extends AbstractBaseService<TopicsDocument, TopicsDocumentDao> implements TopicsDocumentService {

    @Override
    public TopicsDocument findOne(TopicsDocument entity) {
        return findOne(entity, null);
    }

    @Override
    public TopicsDocument findOne(TopicsDocument entity, Iterable<? extends Field> fields) {
        TopicsDocument document = super.findOne(entity, fields == null ? TopicsDocument.DEFAULT_FIELDS : fields);
        if (document != null) {
            document.setBrands(Posts.fromString(document.getBrand()));
            document.setBrand(null);
        }
        return document;
    }

    @Override
    public <S extends TopicsDocument> int save(S entity) {
        int n;
        //  save or update
        TopicsDocument one = new TopicsDocument();
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
