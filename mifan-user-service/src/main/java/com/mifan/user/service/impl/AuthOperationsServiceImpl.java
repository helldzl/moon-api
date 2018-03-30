package com.mifan.user.service.impl;

import com.mifan.user.dao.AuthOperationsDao;
import com.mifan.user.domain.AuthOperations;
import com.mifan.user.service.AuthOperationsService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/5/3
 */
@Service
public class AuthOperationsServiceImpl extends AbstractBaseService<AuthOperations, AuthOperationsDao> implements AuthOperationsService {
}
