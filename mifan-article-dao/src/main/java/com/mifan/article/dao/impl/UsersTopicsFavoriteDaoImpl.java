package com.mifan.article.dao.impl;

import com.mifan.article.dao.UsersTopicsFavoriteDao;
import com.mifan.article.domain.UsersTopicsFavorite;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
@Repository
public class UsersTopicsFavoriteDaoImpl extends AbstractBaseDao<UsersTopicsFavorite> implements UsersTopicsFavoriteDao {
}
