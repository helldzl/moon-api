package com.mifan.article.dao.impl;

import com.mifan.article.dao.UsersTopicsLikeDao;
import com.mifan.article.domain.UsersTopicsLike;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Repository
public class UsersTopicsLikeDaoImpl extends AbstractBaseDao<UsersTopicsLike> implements UsersTopicsLikeDao {
}
