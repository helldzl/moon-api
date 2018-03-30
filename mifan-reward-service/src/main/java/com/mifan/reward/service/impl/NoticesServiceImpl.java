package com.mifan.reward.service.impl;

import com.mifan.reward.dao.NoticesDao;
import com.mifan.reward.domain.Notices;
import com.mifan.reward.service.NoticesService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author DXZ
 * @version 1.0
 * @since 2017-08-31
 */
@Service
public class NoticesServiceImpl extends AbstractBaseService<Notices, NoticesDao> implements NoticesService {
}
