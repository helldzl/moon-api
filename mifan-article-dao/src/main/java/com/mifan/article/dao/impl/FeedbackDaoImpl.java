package com.mifan.article.dao.impl;

import com.mifan.article.dao.FeedbackDao;
import com.mifan.article.domain.Feedback;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
@Repository
public class FeedbackDaoImpl extends AbstractBaseDao<Feedback> implements FeedbackDao {
}
