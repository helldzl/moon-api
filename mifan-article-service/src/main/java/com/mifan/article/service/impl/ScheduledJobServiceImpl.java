package com.mifan.article.service.impl;

import com.mifan.article.dao.ScheduledJobDao;
import com.mifan.article.domain.ScheduledJob;
import com.mifan.article.service.ScheduledJobService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-08-05
 */
@Service
public class ScheduledJobServiceImpl extends AbstractBaseService<ScheduledJob, ScheduledJobDao> implements ScheduledJobService {
}
