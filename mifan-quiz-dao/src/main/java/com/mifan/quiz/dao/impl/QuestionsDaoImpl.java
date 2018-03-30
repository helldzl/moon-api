package com.mifan.quiz.dao.impl;

import com.mifan.quiz.dao.QuestionsDao;
import com.mifan.quiz.domain.Questions;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-12-12
 */
@Repository
public class QuestionsDaoImpl extends AbstractBaseDao<Questions> implements QuestionsDao {
}
