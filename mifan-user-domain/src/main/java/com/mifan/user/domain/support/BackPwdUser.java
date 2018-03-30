/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.domain.support.BackPwdUser
 *
 * @description:找回密码参数
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月17日 ZYW v0.0.1 create
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
public class BackPwdUser {
    
    @NotNull(groups = Post.class, message = "{NotNull.Users.username}")
    @Size(min = 2, max = 50, groups = Post.class, message = "{Size.Users.username}")
    @Pattern(regexp = AccountType.REGEXP_USERNAME, groups = Post.class, message = "{Pattern.Users.username}")
    private String username;
    
    @NotNull(groups = Post.class, message = "{NotNull.smsCode}")
    private String smsCode;
    
    @NotNull(groups = Post.class, message = "{NotNull.Users.password}")
    @Size(min = 6, max = 32, groups = {Post.class}, message = "{Size.Users.password}")
    @Pattern(regexp = "^\\w+$", groups = {Post.class}, message = "{Pattern.Users.password}")
    private String password;
    
    @NotNull(groups = Post.class, message = "{NotNull.Users.password}")
    @Size(min = 6, max = 32, groups = {Post.class}, message = "{Size.Users.password}")
    @Pattern(regexp = "^\\w+$", groups = {Post.class}, message = "{Pattern.Users.password}")
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
}
