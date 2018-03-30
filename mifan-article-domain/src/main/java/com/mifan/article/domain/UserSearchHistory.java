package com.mifan.article.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Post;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-01-18
 */
public class UserSearchHistory extends BaseEntity {

    public static final String TABLE_NAME = "user_search_history";

    public static final String SSID = "ssid";
    public static final String CATEGORY_ID = "category_id";
    public static final String FORUM_ID = "forum_id";
    public static final String USER_ID = "user_id";

    private static final long serialVersionUID = 2378574503756199099L;

    @NotNull(groups = {Post.class}, message = "{NotNull.UserSearchHistory.ssid}")
    private String ssid;
    private Long categoryId;
    private Long forumId;
    private Long userId;
    
    @NotBlank(groups = {Post.class}, message = "{NotNull.UserSearchHistory.keyword}")
    private String keyword;//搜索关键字

    public UserSearchHistory() {
    }

    public UserSearchHistory(Long id) {
        super(id);
    }

    /**
     * @return 
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * @param ssid 
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
    /**
     * @return 
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId 
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    /**
     * @return 
     */
    public Long getForumId() {
        return forumId;
    }

    /**
     * @param forumId 
     */
    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }
    /**
     * @return 
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId 
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

}
