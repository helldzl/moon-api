package com.mifan.article.service;

import com.mifan.article.domain.MpCategories;

import java.util.List;

import org.moonframework.model.mybatis.service.BaseService;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
public interface MpCategoriesService extends BaseService<MpCategories> {
    /**
     * 根据品牌查询分类，包括二级分类
     * @param brandId
     * @return
     */
    List<MpCategories> findByBrandId(Long brandId);
}
