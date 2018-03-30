package com.mifan.user.service.impl;

import com.mifan.user.dao.UsersGroupsDao;
import com.mifan.user.domain.UsersGroups;
import com.mifan.user.service.UsersGroupsService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/24
 */
@Service
public class UsersGroupsServiceImpl extends AbstractBaseService<UsersGroups, UsersGroupsDao> implements UsersGroupsService {
}
