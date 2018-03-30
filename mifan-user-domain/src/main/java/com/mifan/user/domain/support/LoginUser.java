/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.domain.support.LoginUser
 *
 * @description:登录参数
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月12日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.user.domain.support;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.moonframework.validation.ValidationGroups.Post;

import com.mifan.user.type.AccountType;

/**
 * @author ZYW
 *
 */
public class LoginUser {
    
    @NotNull(groups = Post.class, message = "{NotNull.Users.username}")
    @Size(min = 2, max = 50, groups = Post.class, message = "{Size.Users.username}")
    @Pattern(regexp = AccountType.REGEXP_USERNAME, groups = Post.class, message = "{Pattern.Users.username}")
    private String username;
    
    @NotNull(groups = Post.class, message = "{NotNull.Users.password}")
    @Size(min = 6, max = 32, groups = {Post.class}, message = "{Size.Users.password}")
    @Pattern(regexp = "^\\w+$", groups = {Post.class}, message = "{Pattern.Users.password}")
    private String password;
    
    private String verifyCode;
    
    private boolean rememberme;
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getVerifyCode() {
        return verifyCode;
    }
    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
    public boolean isRememberme() {
        return rememberme;
    }
    public void setRememberme(boolean rememberme) {
        this.rememberme = rememberme;
    }
    
}
