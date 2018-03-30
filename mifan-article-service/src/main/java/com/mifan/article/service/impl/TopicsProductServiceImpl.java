package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsProductDao;
import com.mifan.article.domain.TopicsProduct;
import com.mifan.article.domain.TopicsProductHistory;
import com.mifan.article.domain.support.Currency;
import com.mifan.article.service.TopicsProductService;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class TopicsProductServiceImpl extends AbstractBaseService<TopicsProduct, TopicsProductDao> implements TopicsProductService {

    @Override
    public <S extends TopicsProduct> int save(S entity) {
        int n;
        // save or update
        TopicsProduct one = new TopicsProduct();
        one.setTopicId(entity.getTopicId());
        one = super.findOne(one, Fields.builder().add(BaseEntity.ID).build());
        if (entity.getPriceUnit() != null) {
            entity.setPriceUnit(Currency.from(entity.getPriceUnit()).getCode());
        }
        if (one == null) {
            n = super.save(entity);
        } else {
            entity.setId(one.getId());
            n = super.update(entity);
        }

        // save or update products price by unique key
        if (entity.getPrice() != null) {
            TopicsProductHistory topicsProductHistory = new TopicsProductHistory();
            topicsProductHistory.setTopicId(entity.getTopicId());
            topicsProductHistory.setPrice(entity.getPrice());
            topicsProductHistory.setEffectiveDate(new Date());
            Services.saveOrUpdate(TopicsProductHistory.class, topicsProductHistory);
        }
        return n;
    }

}
