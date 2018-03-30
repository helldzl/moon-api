package com.mifan.user.service.impl;

import com.mifan.user.domain.Users;
import com.mifan.user.domain.support.SecureUser;
import com.mifan.user.service.UsersService;
import org.moonframework.security.authentication.PermissionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/11/17
 */
// @Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersService usersService;

    // @Value("${moon.security.cas.filter.appKey}")
    private String appKey;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersService.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username Not Found");
        }

        PermissionControl control = usersService.doPermission(usersService.findPermissions(appKey, user.getId(), null));
        // 扩展User, 增加存放userId的属性, 然后在SavedRequestAwareAuthenticationSuccessHandler中增加登录成功后的用户信息更新操作.
        SecureUser secureUser = new SecureUser(username, user.getPassword(), user.getEnabled() == 1, true, true, true, getGrantedAuthorities(control));
        secureUser.setUserId(user.getId());
        return secureUser;
    }

    private List<GrantedAuthority> getGrantedAuthorities(PermissionControl control) {
        if (control == null) {
            return Collections.emptyList();
        }
        Set<String> privileges = new HashSet<>();
        privileges.addAll(control.getRoles());
        privileges.addAll(control.getAuthorities());

        List<GrantedAuthority> authorities = new ArrayList<>();
        privileges.forEach(privilege -> authorities.add(new SimpleGrantedAuthority(privilege)));
        return authorities;
    }

}
