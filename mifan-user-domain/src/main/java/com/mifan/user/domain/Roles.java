package com.mifan.user.domain;

import org.moonframework.model.mybatis.annotation.OneToMany;
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
public class Roles extends BaseEntity {

    public static final String TABLE_NAME = "roles";

    public static final String SITE_ID = "site_id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String ENABLED = "enabled";

    private static final long serialVersionUID = -5775621016953674224L;

    @OneToMany(value = "authorities",
            targetEntity = RolesAuthorities.class,
            mappedBy = RolesAuthorities.ROLE_ID,
            mappedTo = RolesAuthorities.AUTHORITY_ID)
    private Long id;

    @NotNull(groups = Post.class, message = "{NotNull.Sites.siteId}")
    private Long siteId;

    @NotNull(groups = Post.class, message = "{NotNull.Roles.name}")
    @Size(min = 2, max = 100, groups = {
            Post.class,
            Patch.class
    }, message = "{Size.Roles.name}")
    private String name;

    @Size(max = 255, groups = {
            Post.class,
            Patch.class
    }, message = "{Size.Roles.description}")
    private String description;

    public Roles() {
    }

    public Roles(Long id) {
        super(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Roles)) {
            return false;
        }

        return (((Roles) obj).getId().equals(this.getId()));
    }

    @Override
    public int hashCode() {
        if (getId() == null) {
            return super.hashCode();
        }
        return this.getId().hashCode();
    }

    /**
     * @return 站点ID
     */
    public Long getSiteId() {
        return siteId;
    }

    /**
     * @param siteId 站点ID
     */
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    /**
     * @return 角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 角色名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return 角色描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 角色描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
