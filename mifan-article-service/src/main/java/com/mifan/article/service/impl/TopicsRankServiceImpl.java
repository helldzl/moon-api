package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsRankDao;
import com.mifan.article.domain.TopicsRank;
import com.mifan.article.service.TopicsRankService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-08-23
 */
@Service
public class TopicsRankServiceImpl extends AbstractBaseService<TopicsRank, TopicsRankDao> implements TopicsRankService {
}
