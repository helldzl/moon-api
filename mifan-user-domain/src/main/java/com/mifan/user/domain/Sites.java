package com.mifan.user.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Patch;
import org.moonframework.validation.ValidationGroups.Post;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author quzile
 * @version 1.0
 * @since 2015-12-04
 */
public class Sites extends BaseEntity {

    public static final String TABLE_NAME = "sites";

    public static final String NAME = "name";
    public static final String DOMAIN = "domain";
    public static final String APP_KEY = "app_key";
    public static final String APP_SECRET = "app_secret";
    public static final String DESCRIPTION = "description";
    public static final String ENABLED = "enabled";

    private static final long serialVersionUID = 5907809164680112028L;

    @NotNull(groups = Post.class, message = "{NotNull.Sites.name}")
    @Size(min = 2, max = 100, groups = {
            Post.class,
            Patch.class
    }, message = "{Size.Sites.name}")
    private String name;

    @Size(max = 200, groups = {
            Post.class,
            Patch.class
    }, message = "{Size.Sites.domain}")
    private String domain;

    @Size(max = 36, groups = {
            Post.class,
            Patch.class
    }, message = "{Size.Sites.appKey}")
    private String appKey;

    @Size(max = 36, groups = {
            Post.class,
            Patch.class
    }, message = "{Size.Sites.appSecret}")
    private String appSecret;

    @Size(max = 255, groups = {
            Post.class,
            Patch.class
    }, message = "{Size.Sites.description}")
    private String description;

    public Sites() {
    }

    public Sites(Long id) {
        super(id);
    }

    /**
     * @return 站点名称
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 站点名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return 站点域名
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain 站点域名
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @return 应用KEY
     */
    public String getAppKey() {
        return appKey;
    }

    /**
     * @param appKey 应用KEY
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    /**
     * @return 应用安全码
     */
    public String getAppSecret() {
        return appSecret;
    }

    /**
     * @param appSecret 应用安全码
     */
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    /**
     * @return 站点描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 站点描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
