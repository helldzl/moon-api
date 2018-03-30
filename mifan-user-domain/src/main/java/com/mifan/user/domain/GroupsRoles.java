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
public class GroupsRoles extends BaseEntity {

    public static final String TABLE_NAME = "groups_roles";

    public static final String GROUP_ID = "group_id";
    public static final String ROLE_ID = "role_id";

    private static final long serialVersionUID = 4898345357831216114L;

    private Long groupId;
    private Long roleId;
    @NotNull(groups = Post.class, message = "{NotNull.GroupsRoles.roleIds}")
    private List<Long> roleIds;
    public GroupsRoles() {
    }

    public GroupsRoles(Long id) {
        super(id);
    }

    public GroupsRoles(Long groupId, Long roleId) {
        this.groupId = groupId;
        this.roleId = roleId;
    }

    /**
     * @return FK 用户组ID
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * @param groupId FK 用户组ID
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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
