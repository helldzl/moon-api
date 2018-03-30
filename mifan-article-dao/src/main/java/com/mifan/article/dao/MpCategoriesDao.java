package com.mifan.article.dao;

import com.mifan.article.domain.MpCategories;

import java.util.List;

import org.moonframework.model.mybatis.repository.BaseDao;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
public interface MpCategoriesDao extends BaseDao<MpCategories> {
    List<MpCategories> findByParentIdBrandId(Long parentId,Long brandId);
}
