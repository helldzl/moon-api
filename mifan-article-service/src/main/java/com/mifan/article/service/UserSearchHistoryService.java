package com.mifan.article.service;

import com.mifan.article.domain.UserSearchHistory;

import java.util.List;

import org.moonframework.model.mybatis.service.BaseService;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-01-18
 */
public interface UserSearchHistoryService extends BaseService<UserSearchHistory> {
    /**
     * 获取用户搜索记录
     * @param ssid
     * @param categoryId
     * @param forumId
     * @return
     */
    List<String> findHistoryBySsid(String ssid,long categoryId,long forumId);
    /**
     * 删除搜索记录
     * @param ssid
     * @param keyword
     * @return
     */
    int deleteKeyword(String ssid,String keyword);
}
