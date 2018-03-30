package com.mifan.reward.service.impl;

import com.mifan.reward.dao.CodesDao;
import com.mifan.reward.domain.Codes;
import com.mifan.reward.service.CodesService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author DXZ
 * @version 1.0
 * @since 2017-08-31
 */
@Service
public class CodesServiceImpl extends AbstractBaseService<Codes, CodesDao> implements CodesService {
}
