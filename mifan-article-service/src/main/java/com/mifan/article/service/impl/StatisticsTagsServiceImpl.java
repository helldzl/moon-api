package com.mifan.article.service.impl;

import com.mifan.article.dao.StatisticsTagsDao;
import com.mifan.article.domain.StatisticsTags;
import com.mifan.article.service.StatisticsTagsService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class StatisticsTagsServiceImpl extends AbstractBaseService<StatisticsTags, StatisticsTagsDao> implements StatisticsTagsService {
}
