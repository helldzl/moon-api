package com.mifan.article.service.impl;

import com.mifan.article.dao.ElasticFunctionScoreDao;
import com.mifan.article.domain.ElasticFunctionScore;
import com.mifan.article.service.ElasticFunctionScoreService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-25
 */
@Service
public class ElasticFunctionScoreServiceImpl extends AbstractBaseService<ElasticFunctionScore, ElasticFunctionScoreDao> implements ElasticFunctionScoreService {
}
