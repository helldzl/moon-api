package com.mifan.user.domain;

import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author quzile
 * @version 1.0
 * @since 2015-12-11
 */
public class Authorities extends BaseEntity {

    public static final String TABLE_NAME = "authorities";

    public static final String SITE_ID = "site_id";
    public static final String AUTH_TYPE = "auth_type";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String AUTH_GROUP = "auth_group";
    public static final String PRIORITY = "priority";

    private static final long serialVersionUID = 1501125524190497673L;

    @NotNull(groups = ValidationGroups.Post.class, message = "{NotNull.Sites.siteId}")
    private Long siteId;

    @NotNull(groups = ValidationGroups.Post.class, message = "{NotNull.Authorities.authType}")
    @Range(min = 0, max = 3, groups = {
            ValidationGroups.Post.class,
            ValidationGroups.Put.class
    }, message = "{Range.Authorities.authType}")
    private Integer authType;

    @NotNull(groups = ValidationGroups.Post.class, message = "{NotNull.Authorities.name}")
    @Size(min = 2, max = 100, groups = {
            ValidationGroups.Post.class,
            ValidationGroups.Put.class
    }, message = "{Size.Authorities.name}")
    private String name;

    @Size(max = 255, groups = {
            ValidationGroups.Post.class,
            ValidationGroups.Put.class
    }, message = "{Size.Authorities.description}")
    private String description;

    @Size(max = 100, groups = {
            ValidationGroups.Post.class,
            ValidationGroups.Put.class
    }, message = "{Size.Authorities.authGroup}")
    private String authGroup;

    @Range(min = -128, max = 127, groups = {
            ValidationGroups.Post.class,
            ValidationGroups.Put.class
    }, message = "{Range.Authorities.priority}")
    private Integer priority;

    @Valid
    private AuthOperations authOperation;

    private String permission;

    public Authorities() {
    }

    public Authorities(Long id) {
        super(id);
    }

    public void initPermission(AuthorityConfig config) {
        permission = config.value();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Authorities)) {
            return false;
        }

        return (((Authorities) obj).getId().equals(this.getId()));
    }

    @Override
    public int hashCode() {
        if (getId() == null) {
            return super.hashCode();
        }
        return this.getId().hashCode();
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
     * @return 权限类型 0:operation 1:shiro
     */
    public Integer getAuthType() {
        return authType;
    }

    /**
     * @param authType 权限类型 0:operation 1:shiro
     */
    public void setAuthType(Integer authType) {
        this.authType = authType;
    }

    /**
     * @return 权限名称
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 权限名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return 权限描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 权限描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return 权限组
     */
    public String getAuthGroup() {
        return authGroup;
    }

    /**
     * @param authGroup 权限组
     */
    public void setAuthGroup(String authGroup) {
        this.authGroup = authGroup;
    }

    /**
     * @return 权限优先级 -128至127
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority 权限优先级 -128至127
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public AuthOperations getAuthOperation() {
        return authOperation;
    }

    public void setAuthOperation(AuthOperations authOperation) {
        this.authOperation = authOperation;
        initPermission(authOperation);
    }

    public String getPermission() {
        return permission == null ? name : permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
