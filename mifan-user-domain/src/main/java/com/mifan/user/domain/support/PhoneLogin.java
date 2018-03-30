/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.domain.support.PhoneLogin
 *
 * @description:TODO
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年5月22日 ZYW v0.0.1 create
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
public class PhoneLogin {
    @NotNull(groups = Post.class, message = "{NotNull.Users.username}")
    @Size(min = 2, max = 50, groups = Post.class, message = "{Size.Users.username}")
    @Pattern(regexp = AccountType.REGEXP_USERNAME, groups = Post.class, message = "{Pattern.Users.username}")
    private String username;
    
    @NotNull(groups = Post.class, message = "{NotNull.smsCode}")
    private String smsCode;
    
    private boolean rememberme;

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

    public boolean getRememberme() {
        return rememberme;
    }

    public void setRememberme(boolean rememberme) {
        this.rememberme = rememberme;
    }
    
}
