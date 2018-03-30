package com.mifan.award.service.impl;

import com.mifan.award.domain.Category;
import com.mifan.award.dao.CategoryDao;
import com.mifan.award.service.CategoryService;
import org.moonframework.model.mongodb.service.AbstractBaseService;
import org.springframework.stereotype.Service;

@Service("categoryService")
public class CategoryServiceImpl extends AbstractBaseService<Category, CategoryDao> implements CategoryService {
}
