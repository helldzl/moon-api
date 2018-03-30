package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsMpDao;
import com.mifan.article.domain.TopicsMp;
import com.mifan.article.service.TopicsMpService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
@Service
public class TopicsMpServiceImpl extends AbstractBaseService<TopicsMp, TopicsMpDao> implements TopicsMpService {
}
