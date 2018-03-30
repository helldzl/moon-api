package com.mifan.article.service;

import com.mifan.article.domain.ForumCategories;
import org.moonframework.model.mybatis.service.BaseService;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-10-31
 */
public interface ForumCategoriesService extends BaseService<ForumCategories> {
    /**
     * 根据parentId查询子类别，查询结果的类型如果不是叶子类别则继续查询其子类别，如果是叶子类别则查询其包含的topcis
     * 奇葩接口，哈哈哈
     * @param parentId
     * @param page
     * @param size
     * @return
     */
    /*Page<ForumCategories> findMpCategories(Long parentId,int page,int size);*/
    /**
     * 查询分类，以及它的子集，如果它是叶子分类则查询其包含的topcis，如果不是则查询其子分类
     * @param id
     * @param child 如果true，则查询其子分类的子集
     * @param childTopics 如果true（且child=ture），则查询时带着子集的topics
     * @return
     */
    ForumCategories findOneMp(Long id,boolean child,boolean childTopics);
}
