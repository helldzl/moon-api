package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsBrandMappingDao;
import com.mifan.article.domain.TopicsBrandMapping;
import com.mifan.article.service.TopicsBrandMappingService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-08
 */
@Service
public class TopicsBrandMappingServiceImpl extends AbstractBaseService<TopicsBrandMapping, TopicsBrandMappingDao> implements TopicsBrandMappingService {
}
