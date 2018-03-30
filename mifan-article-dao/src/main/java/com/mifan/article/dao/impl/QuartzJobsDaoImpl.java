package com.mifan.article.dao.impl;

import com.mifan.article.dao.QuartzJobsDao;
import com.mifan.article.domain.QuartzJobs;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-11-02
 */
@Repository
public class QuartzJobsDaoImpl extends AbstractBaseDao<QuartzJobs> implements QuartzJobsDao {
}
