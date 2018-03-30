package com.mifan.reward.service.impl;

import com.mifan.reward.dao.PrizeInfoDao;
import com.mifan.reward.domain.PrizeInfo;
import com.mifan.reward.service.PrizeInfoService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author DXZ
 * @version 1.0
 * @since 2017-08-31
 */
@Service
public class PrizeInfoServiceImpl extends AbstractBaseService<PrizeInfo, PrizeInfoDao> implements PrizeInfoService {
}
