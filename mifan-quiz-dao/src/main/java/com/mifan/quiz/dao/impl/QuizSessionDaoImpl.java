package com.mifan.quiz.dao.impl;

import com.mifan.quiz.dao.QuizSessionDao;
import com.mifan.quiz.domain.QuizSession;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-12-12
 */
@Repository
public class QuizSessionDaoImpl extends AbstractBaseDao<QuizSession> implements QuizSessionDao {
}
