package com.mifan.user.service.impl;

import com.mifan.user.dao.UserAccountsDao;
import com.mifan.user.domain.UserAccounts;
import com.mifan.user.service.UserAccountsService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/1/13
 */
@Service
public class UserAccountsServiceImpl extends AbstractBaseService<UserAccounts, UserAccountsDao> implements UserAccountsService {
}
