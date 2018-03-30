package com.mifan.quiz.dao.impl;

import com.mifan.quiz.dao.QuizsDao;
import com.mifan.quiz.domain.Quizs;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-12-12
 */
@Repository
public class QuizsDaoImpl extends AbstractBaseDao<Quizs> implements QuizsDao {
}
