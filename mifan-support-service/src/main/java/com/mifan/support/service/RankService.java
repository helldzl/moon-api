/**
 * 排行榜相关
 */
package com.mifan.support.service;

import org.moonframework.model.mybatis.service.BaseService;
import org.springframework.data.domain.Page;

import com.mifan.support.domain.EventLog;
import com.mifan.support.domain.support.Rank;
import com.mifan.support.domain.support.Topics;

/**
 * @author ZYW
 *
 */
public interface RankService extends BaseService<EventLog>{
    /**
     * 更新排行榜
     * @param key
     * @param type 0：定时调用，1：手动调用
     */
    void updateRanking(String key,Integer type);
    /**
     * 获取排行榜（从大到小）
     * @param key
     * @param page
     * @param size
     * @return
     */
    Page<Long> getRank(String key,int page,int size);
    /**
     * 获取排行榜及标题（从大到小）
     * @param key
     * @param page
     * @param size
     * @return
     */
    Page<Topics> findPage(String key,int page,int size);
    /**
     * 删除排行中的某个topic
     * @param key
     * @param topicId
     * @return
     */
    int remove(String key,Long topicId);
    /**
     * 获取排行榜及分数及标题（从大到小）
     * @param key
     * @param page
     * @param size
     * @return
     */
    Page<Rank> findPageWithScore(String key,int page,int size);
}
