package com.mifan.article.dao;

import com.mifan.article.domain.SpiderStatistics;
import com.mifan.article.domain.Topics;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.repository.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public interface TopicsDao extends BaseDao<Topics> {
    
    /**
     * 查询搜索热词 根据浏览量
     * 类别(麦克风)
     * @param product 是否是产品中心1：是，0：否
     * @param minReviews 最小浏览量
     * @param size 要查询的个数
     * @param mydate 限定时间
     * @return
     */
    List<Topics> findHotKeywordForMicrophonesByReviews(int product,int minReviews,int size,String mydate);
    /**
     * 查询搜索热词 根据图片个数>4及时间最新
     * 类别(麦克风)
     * @param product 是否是产品中心1：是，0：否
     * @param size 要查询的个数
     * @return
     */
    List<Topics> findHotKeywordForMicrophonesByQuality(int product,int size);
    /**
     * 查询搜索热词 根据浏览量
     * 类别（非麦克风、附件、架子/包、电缆/插头）
     * @param product 是否是产品中心1：是，0：否
     * @param minReviews 最小浏览量
     * @param size 要查询的个数
     * @param mydate 限定时间
     * @return
     */
    List<Topics> findHotKeywordForOthersByReviews(int product,int minReviews,int size,String mydate);
    /**
     * 查询搜索热词 根据图片个数>4及时间最新
     * 类别（非麦克风、附件、架子/包、电缆/插头）
     * @param product 是否是产品中心1：是，0：否
     * @param size 要查询的个数
     * @return
     */
    List<Topics> findHotKeywordForOthersByQuality(int product, int size);
    
    Page<SpiderStatistics> findSpiderStatisticsByTime (String priortime, String latertime, Pageable pageable,Iterable<? extends Field> fields);
}
