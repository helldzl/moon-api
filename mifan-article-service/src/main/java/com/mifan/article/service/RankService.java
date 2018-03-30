/**
 * 排行榜相关
 */
package com.mifan.article.service;

import com.mifan.article.domain.Topics;
import com.mifan.article.domain.support.Rank;
import org.moonframework.web.jsonapi.Data;
import org.springframework.http.ResponseEntity;

import java.util.List;


/**
 * @author ZYW
 */
public interface RankService {
    /**
     * 更新排行榜
     * @param key
     * @param type 0：定时调用，1：手动调用
     */
//    void updateRanking(String key,Integer type);

    /**
     * 获取排行榜及标题（从大到小）
     *
     * @param key
     * @param page
     * @param size
     * @return
     */
    ResponseEntity<Data<List<Topics>>> findPage(String key, int page, int size);

    /**
     * 查询排行列表
     *
     * @param key
     * @param page
     * @param size
     * @return
     */
    ResponseEntity<Data<List<Rank>>> findPageWithScore(String key, int page, int size);
}
