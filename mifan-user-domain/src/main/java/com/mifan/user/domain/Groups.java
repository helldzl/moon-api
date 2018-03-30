package com.mifan.user.domain;

import org.moonframework.model.mybatis.annotation.OneToMany;
import org.moonframework.model.mybatis.domain.BaseEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2015-12-04
 */
public class Groups extends BaseEntity {

    public static final String TABLE_NAME = "groups";

    public static final String SITE_ID = "site_id";
    public static final String PARENT_ID = "parent_id";
    public static final String GROUP_NAME = "group_name";
    public static final String DESCRIPTION = "description";
    public static final String ENABLED = "enabled";

    private static final long serialVersionUID = -3641043420308543957L;

    @OneToMany(value = "roles",
            targetEntity = GroupsRoles.class,
            mappedBy = GroupsRoles.GROUP_ID,
            mappedTo = GroupsRoles.ROLE_ID)
    private Long id;

    @NotNull(groups = Post.class, message = "{NotNull.Sites.siteId}")
    private Long siteId;

    private Long parentId;

    @NotNull(groups = Post.class, message = "{NotNull.Groups.groupName}")
    @Size(min = 2, max = 100, groups = {
            Post.class,
            Patch.class
    }, message = "{Size.Groups.groupName}")
    private String groupName;

    @Size(max = 255, groups = {
            Post.class,
            Patch.class
    }, message = "{Size.Groups.description}")
    private String description;

    public Groups() {
    }

    public Groups(Long id) {
        super(id);
    }

    /**
     * @return FK 站点ID
     */
    public Long getSiteId() {
        return siteId;
    }

    /**
     * @param siteId FK 站点ID
     */
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    /**
     * @return PID
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId PID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return 用户组名称
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName 用户组名称
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
