package com.mifan.article.dao.impl;

import com.mifan.article.dao.UserSearchHistoryDao;
import com.mifan.article.domain.UserSearchHistory;

import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-01-18
 */
@Repository
public class UserSearchHistoryDaoImpl extends AbstractBaseDao<UserSearchHistory> implements UserSearchHistoryDao {
}
