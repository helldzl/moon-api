package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-02-02
 */
public class SearchKeyword extends BaseEntity {

    public static final String TABLE_NAME = "search_keyword";

    public static final String SEARCH_ID = "search_id";
    public static final String KEYWORD = "keyword";
    public static final String NUM = "num";

    private static final long serialVersionUID = -9008468290644487522L;

    private Long searchId;
    private String keyword;
    private Integer num;

    public SearchKeyword() {
    }

    public SearchKeyword(Long id) {
        super(id);
    }

    /**
     * @return user_search_history FK
     */
    public Long getSearchId() {
        return searchId;
    }

    /**
     * @param searchId user_search_history FK
     */
    public void setSearchId(Long searchId) {
        this.searchId = searchId;
    }
    /**
     * @return 
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @param keyword 
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    /**
     * @return 次数
     */
    public Integer getNum() {
        return num;
    }

    /**
     * @param num 次数
     */
    public void setNum(Integer num) {
        this.num = num;
    }

}
