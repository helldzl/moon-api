package com.mifan.user.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author ZYW
 * @version 1.0
 * @since 2016-09-26
 */
public class UserAliases extends BaseEntity {

    public static final String TABLE_NAME = "user_aliases";

    public static final String USER_ID = "user_id";
    public static final String ALIAS_USER_ID = "alias_user_id";

    private static final long serialVersionUID = 1376435402749792055L;

    private Long userId;
    private Long aliasUserId;

    public UserAliases() {
    }

    public UserAliases(Long id) {
        super(id);
    }

    /**
     * @return 普通主账号FK
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId 普通主账号FK
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    /**
     * @return 第三方登录主账号FK
     */
    public Long getAliasUserId() {
        return aliasUserId;
    }

    /**
     * @param aliasUserId 第三方登录主账号FK
     */
    public void setAliasUserId(Long aliasUserId) {
        this.aliasUserId = aliasUserId;
    }

}
