package com.mifan.reward.service.impl;

import com.mifan.reward.dao.CategoriesDao;
import com.mifan.reward.domain.Categories;
import com.mifan.reward.service.CategoriesService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author DXZ
 * @version 1.0
 * @since 2017-08-31
 */
@Service
public class CategoriesServiceImpl extends AbstractBaseService<Categories, CategoriesDao> implements CategoriesService {
}
