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
public class UsersGroups extends BaseEntity {

    public static final String TABLE_NAME = "users_groups";

    public static final String USER_ID = "user_id";
    public static final String GROUP_ID = "group_id";

    private static final long serialVersionUID = -4417109368660020915L;

    private Long userId;
    private Long groupId;
    
    @NotNull(groups = Post.class, message = "{NotNull.UsersGroups.groupIds}")
    private List<Long> groupIds;

    public UsersGroups() {
    }

    public UsersGroups(Long id) {
        super(id);
    }

    public UsersGroups(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
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

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

}
