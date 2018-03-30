package com.mifan.user.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/3
 */
public abstract class AuthorityConfig extends BaseEntity {

    public static final String TABLE_NAME = "authority_config";

    private Long authorityId;

    public AuthorityConfig() {
    }

    public AuthorityConfig(Long id) {
        super(id);
    }

    /**
     * @return FK 权限ID
     */
    public Long getAuthorityId() {
        return authorityId;
    }

    /**
     * @param authorityId FK 权限ID
     */
    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    /**
     * 描述字符串
     *
     * @return
     */
    public abstract String value();

}
