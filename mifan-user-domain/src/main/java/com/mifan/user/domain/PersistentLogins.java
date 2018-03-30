package com.mifan.user.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

import java.util.Date;

/**
 * @author quzile
 * @version 1.0
 * @since 2015-12-04
 */
public class PersistentLogins extends BaseEntity {

    public static final String TABLE_NAME = "persistent_logins";

    public static final String USERNAME = "username";
    public static final String SERIES = "series";
    public static final String TOKEN = "token";
    public static final String LAST_USED = "last_used";

    private static final long serialVersionUID = -4961765684948005029L;

    private String username;
    private String series;
    private String token;
    private Date lastUsed;

    public PersistentLogins() {
    }

    public PersistentLogins(Long id) {
        super(id);
    }

    /**
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return
     */
    public String getSeries() {
        return series;
    }

    /**
     * @param series
     */
    public void setSeries(String series) {
        this.series = series;
    }

    /**
     * @return
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return
     */
    public Date getLastUsed() {
        return lastUsed;
    }

    /**
     * @param lastUsed
     */
    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

}
