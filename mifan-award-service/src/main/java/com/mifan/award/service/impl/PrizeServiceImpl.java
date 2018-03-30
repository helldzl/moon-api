package com.mifan.award.service.impl;

import com.mifan.award.dao.PrizeDao;
import com.mifan.award.domain.Prize;
import com.mifan.award.service.PrizeService;
import org.moonframework.model.mongodb.service.AbstractBaseService;
import org.springframework.stereotype.Service;

@Service("prizeService")
public class PrizeServiceImpl extends AbstractBaseService<Prize, PrizeDao> implements PrizeService {
}
