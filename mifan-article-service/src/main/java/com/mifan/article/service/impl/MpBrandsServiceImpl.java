package com.mifan.article.service.impl;

import com.mifan.article.dao.MpBrandsDao;
import com.mifan.article.domain.MpBrands;
import com.mifan.article.service.MpBrandsService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
@Service
public class MpBrandsServiceImpl extends AbstractBaseService<MpBrands, MpBrandsDao> implements MpBrandsService {
}
