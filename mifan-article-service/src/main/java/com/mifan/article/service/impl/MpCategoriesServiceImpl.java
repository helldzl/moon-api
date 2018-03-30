package com.mifan.article.service.impl;

import com.mifan.article.dao.MpCategoriesDao;
import com.mifan.article.domain.MpBrandCategories;
import com.mifan.article.domain.MpCategories;
import com.mifan.article.service.MpCategoriesService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.stereotype.Service;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
@Service
public class MpCategoriesServiceImpl extends AbstractBaseService<MpCategories, MpCategoriesDao> implements MpCategoriesService {
    @Override
    public List<MpCategories> findByBrandId(Long brandId){
        MpBrandCategories find = new MpBrandCategories();
        find.setBrandId(brandId);
        List<MpBrandCategories> brandCategories = Services.findAll(MpBrandCategories.class, find);
        Set<Long> categoryIds = brandCategories.stream().map(BaseEntity::getId).collect(Collectors.toSet());
        List<MpCategories> categories = this.findAll(categoryIds);
        for(MpCategories mc : categories) {
            List<MpCategories> children = baseDao.findByParentIdBrandId(mc.getId(), brandId);
            mc.setChildren(children);
        }
        return categories;
    }
}
