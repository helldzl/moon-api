package com.mifan.article.dao.impl;

import com.mifan.article.dao.ElasticFunctionScoreDao;
import com.mifan.article.domain.ElasticFunctionScore;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-25
 */
@Repository
public class ElasticFunctionScoreDaoImpl extends AbstractBaseDao<ElasticFunctionScore> implements ElasticFunctionScoreDao {
}
