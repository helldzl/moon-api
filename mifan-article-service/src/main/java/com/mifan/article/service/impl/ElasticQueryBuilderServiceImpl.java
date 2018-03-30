package com.mifan.article.service.impl;

import com.mifan.article.dao.ElasticQueryBuilderDao;
import com.mifan.article.domain.ElasticFunctionScore;
import com.mifan.article.domain.ElasticQueryBuilder;
import com.mifan.article.service.ElasticQueryBuilderService;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-25
 */
@Service
public class ElasticQueryBuilderServiceImpl extends AbstractBaseService<ElasticQueryBuilder, ElasticQueryBuilderDao> implements ElasticQueryBuilderService {

    @Override
    public ElasticQueryBuilder findOne(Long id) {
        ElasticQueryBuilder one = super.findOne(id);
        if (one != null) {
            // TODO 改为外键关系, GLOBAL + 自己特定的数据
            List<ElasticFunctionScore> functionScoreList = Services.findAll(ElasticFunctionScore.class, Restrictions.and(
                    Restrictions.eq(ElasticFunctionScore.ENABLED, 1),
                    Restrictions.eq(ElasticFunctionScore.FUNCTION_GLOBAL, 1)), ElasticFunctionScore.DEFAULT_FIELDS);
            one.setFunctionScoreList(functionScoreList);
        }
        return one;
    }

}
