package com.mifan.user.domain;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2015-12-04
 */
public class UsersRoles extends BaseEntity {

    public static final String TABLE_NAME = "users_roles";

    public static final String USER_ID = "user_id";
    public static final String ROLE_ID = "role_id";

    private static final long serialVersionUID = 8154250137273282790L;

    private Long userId;
    private Long roleId;

    @NotNull(groups = Post.class, message = "{NotNull.UsersRoles.roleIds}")
    private List<Long> roleIds;
    
    public UsersRoles() {
    }

    public UsersRoles(Long id) {
        super(id);
    }

    public UsersRoles(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    /**
     * @return FK 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId FK 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return FK 角色ID
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * @param roleId FK 角色ID
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

}
