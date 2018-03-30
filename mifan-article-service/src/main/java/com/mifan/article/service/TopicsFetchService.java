package com.mifan.article.service;

import com.mifan.article.domain.TopicsFetch;

import java.util.Map;

import org.moonframework.elasticsearch.SearchResult;
import org.moonframework.model.mybatis.service.BaseService;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public interface TopicsFetchService extends BaseService<TopicsFetch> {
    /**
     * 美频热榜新闻接口
     * @param seedIds 所有的直播新闻来源id
     * @param size
     * @return
     * 本接口2.3.0中不再需要
     */
    @Deprecated
    SearchResult<Map<String, Object>> mpHotNews(String seedIds,int size);
    /**
     * 美频推荐新闻接口，每个来源取出相同个数的新闻，交叉排序
     * @param seedIds 所有爬取到点击量的新闻来源id
     * @param size
     * @return
     */
    SearchResult<Map<String, Object>> mpRecommendNews(String seedIds,int size);
}
