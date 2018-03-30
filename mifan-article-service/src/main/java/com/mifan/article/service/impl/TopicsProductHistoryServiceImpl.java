package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsProductHistoryDao;
import com.mifan.article.domain.TopicsProductHistory;
import com.mifan.article.service.TopicsProductHistoryService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class TopicsProductHistoryServiceImpl extends AbstractBaseService<TopicsProductHistory, TopicsProductHistoryDao> implements TopicsProductHistoryService {
}
