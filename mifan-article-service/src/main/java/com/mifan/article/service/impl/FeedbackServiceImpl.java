package com.mifan.article.service.impl;

import com.mifan.article.dao.FeedbackDao;
import com.mifan.article.domain.Feedback;
import com.mifan.article.service.FeedbackService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
@Service
public class FeedbackServiceImpl extends AbstractBaseService<Feedback, FeedbackDao> implements FeedbackService {
}
