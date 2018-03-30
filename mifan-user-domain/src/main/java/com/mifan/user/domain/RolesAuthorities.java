package com.mifan.user.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author quzile
 * @version 1.0
 * @since 2015-12-04
 */
public class RolesAuthorities extends BaseEntity {

    public static final String TABLE_NAME = "roles_authorities";

    public static final String ROLE_ID = "role_id";
    public static final String AUTHORITY_ID = "authority_id";

    private static final long serialVersionUID = 1737485292210023971L;

    private Long roleId;
    private Long authorityId;

    public RolesAuthorities() {
    }

    public RolesAuthorities(Long id) {
        super(id);
    }

    public RolesAuthorities(Long roleId, Long authorityId) {
        this.roleId = roleId;
        this.authorityId = authorityId;
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

}
