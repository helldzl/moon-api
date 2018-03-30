package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsRelatedDao;
import com.mifan.article.domain.TopicsRelated;
import com.mifan.article.service.TopicsRelatedService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class TopicsRelatedServiceImpl extends AbstractBaseService<TopicsRelated, TopicsRelatedDao> implements TopicsRelatedService {
}
