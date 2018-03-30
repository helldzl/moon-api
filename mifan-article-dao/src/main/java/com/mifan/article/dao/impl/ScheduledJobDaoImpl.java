package com.mifan.article.dao.impl;

import com.mifan.article.dao.ScheduledJobDao;
import com.mifan.article.domain.ScheduledJob;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-08-05
 */
@Repository
public class ScheduledJobDaoImpl extends AbstractBaseDao<ScheduledJob> implements ScheduledJobDao {
}
