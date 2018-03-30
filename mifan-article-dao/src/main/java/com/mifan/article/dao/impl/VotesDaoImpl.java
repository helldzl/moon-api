package com.mifan.article.dao.impl;

import com.mifan.article.dao.VotesDao;
import com.mifan.article.domain.Votes;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-05
 */
@Repository
public class VotesDaoImpl extends AbstractBaseDao<Votes> implements VotesDao {
}
