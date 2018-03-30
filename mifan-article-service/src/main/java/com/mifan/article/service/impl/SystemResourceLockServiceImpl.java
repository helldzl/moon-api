package com.mifan.article.service.impl;

import com.mifan.article.dao.SystemResourceLockDao;
import com.mifan.article.domain.SystemResourceLock;
import com.mifan.article.service.SystemResourceLockService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2018-01-18
 */
@Service
public class SystemResourceLockServiceImpl extends AbstractBaseService<SystemResourceLock, SystemResourceLockDao> implements SystemResourceLockService {
}
