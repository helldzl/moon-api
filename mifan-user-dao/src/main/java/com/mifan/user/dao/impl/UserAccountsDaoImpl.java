package com.mifan.user.dao.impl;

import com.mifan.user.dao.UserAccountsDao;
import com.mifan.user.domain.UserAccounts;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/2
 */
@Repository
public class UserAccountsDaoImpl extends AbstractBaseDao<UserAccounts> implements UserAccountsDao {
}
