package com.mifan.user.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

import javax.validation.constraints.NotNull;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-03-31
 */
public class UserInvitations extends BaseEntity {

    public static final String TABLE_NAME = "user_invitations";

    public static final String USER_ID = "user_id";
    public static final String TARGET_ID = "target_id";

    private static final long serialVersionUID = 195526442603760065L;

    @NotNull(groups = {Post.class, Patch.class}, message = "{NotNull.UserInvitations.userId}")
    private Long userId;

    @NotNull(groups = {Post.class, Patch.class}, message = "{NotNull.UserInvitations.targetId}")
    private Long targetId;

    // relations
    private UserProfiles target;

    public UserInvitations() {
    }

    public UserInvitations(Long id) {
        super(id);
    }

    /**
     * @return 邀请人
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId 邀请人
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return 被邀请人
     */
    public Long getTargetId() {
        return targetId;
    }

    /**
     * @param targetId 被邀请人
     */
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public UserProfiles getTarget() {
        return target;
    }

    public void setTarget(UserProfiles target) {
        this.target = target;
    }
}
