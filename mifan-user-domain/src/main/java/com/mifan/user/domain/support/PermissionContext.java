package com.mifan.user.domain.support;



import com.mifan.user.domain.Authorities;
import com.mifan.user.domain.Roles;

import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/22
 */
public class PermissionContext {

    private Long siteId;
    private Long userId;
    private Set<Roles> roles;
    private Set<Authorities> authorities;

    public PermissionContext(Long siteId, Long userId, Set<Roles> roles, Set<Authorities> authorities) {
        this.siteId = siteId;
        this.userId = userId;
        this.roles = roles;
        this.authorities = authorities;
        init();
    }

    public void init() {

    }

    public Long getSiteId() {
        return siteId;
    }

    public Long getUserId() {
        return userId;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public Set<Authorities> getAuthorities() {
        return authorities;
    }
}
