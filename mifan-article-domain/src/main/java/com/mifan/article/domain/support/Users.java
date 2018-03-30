package com.mifan.article.domain.support;

import com.fasterxml.jackson.annotation.JsonView;
import com.mifan.article.domain.*;
import org.hibernate.validator.constraints.Range;
import org.moonframework.core.json.ResponseView;
import org.moonframework.core.json.View;
import org.moonframework.model.mybatis.annotation.OneToMany;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/5
 */
@GroupSequence({ValidationGroups.Post.class, ValidationGroups.Put.class, Users.class})
public class Users extends BaseEntity {

    public static final String TABLE_NAME = "users";

    public interface WithoutPasswordView extends ResponseView {
    }

    public interface WithPasswordView extends WithoutPasswordView {
    }

    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PWD_TYPE = "pwd_type";
    public static final String REG_FROM = "reg_from";
    public static final String LOGIN_TIMES = "login_times";
    public static final String ENABLED = "enabled";

    private static final long serialVersionUID = 3880847884448565378L;

    @OneToMany(value = "topics:hide",
            targetEntity = UsersTopicsHide.class,
            mappedBy = UsersTopicsHide.USER_ID,
            mappedTo = UsersTopicsHide.TOPIC_ID)
    @OneToMany(value = "topics:like",
            targetEntity = UsersTopicsLike.class,
            mappedBy = UsersTopicsLike.USER_ID,
            mappedTo = UsersTopicsLike.TOPIC_ID)
    @OneToMany(value = "topics:report",
            targetEntity = UsersTopicsReport.class,
            mappedBy = UsersTopicsReport.USER_ID,
            mappedTo = UsersTopicsReport.TOPIC_ID)
    @OneToMany(value = "topics:favorite",
            targetEntity = UsersTopicsFavorite.class,
            mappedBy = UsersTopicsFavorite.USER_ID,
            mappedTo = UsersTopicsFavorite.TOPIC_ID)
    @OneToMany(value = "folders:compare",
            targetEntity = UsersTopicsCompare.class,
            mappedBy = UsersTopicsCompare.USER_ID,
            mappedTo = UsersTopicsCompare.FOLDER_ID)
    @OneToMany(value = "channels:watch",
            targetEntity = UsersChannelsWatch.class,
            mappedBy = UsersChannelsWatch.USER_ID,
            mappedTo = UsersChannelsWatch.CHANNEL_ID)
    private Long id;

    @NotNull(groups = ValidationGroups.Post.class, message = "{NotNull.Users.username}")
    @Size(min = 2, max = 50, groups = ValidationGroups.Post.class, message = "{Size.Users.username}")
    @Null(groups = ValidationGroups.Patch.class, message = "{Null.Users.username}")
    private String username;

    @NotNull(groups = ValidationGroups.Post.class, message = "{NotNull.Users.password}")
    @Size(min = 6, max = 32, groups = {ValidationGroups.Post.class, ValidationGroups.Patch.class}, message = "{Size.Users.password}")
    @Pattern(regexp = "^\\w+$", groups = {ValidationGroups.Post.class, ValidationGroups.Patch.class}, message = "{Pattern.Users.password}")
    private String password;

    @Size(min = 6, max = 32, groups = {ValidationGroups.Patch.class}, message = "{Size.Users.oldPassword}")
    @Pattern(regexp = "^\\w+$", groups = {ValidationGroups.Patch.class}, message = "{Pattern.Users.oldPassword}")
    private String oldPassword;

    @Range(min = 0, max = 255, message = "{Range.Users.regFrom}")
    private Integer regFrom;

    @Min(value = 0)
    @Null(groups = ValidationGroups.Patch.class, message = "{Null.Users.loginTimes}")
    private Integer loginTimes;

    private Integer pwdType;//密码类型

    /*@NotNull(groups = Post.class, message = "{NotNull.smsCode}")*/
    private String smsCode;//短信验证码

    private Long inviter;//注册邀请人

    @Valid
    private UserProfiles userProfile; // 简介

    public Users() {
    }

    public Users(Long id) {
        this.id = id;
    }

    @Override
    @JsonView(View.class)
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @JsonView(WithoutPasswordView.class)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return 用户密码
     */
    @JsonView(WithPasswordView.class)
    public String getPassword() {
        return password;
    }

    /**
     * @param password 用户密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return
     */
    @JsonView(WithPasswordView.class)
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * @param oldPassword
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * @return 注册来源
     */
    @JsonView(WithoutPasswordView.class)
    public Integer getRegFrom() {
        return regFrom;
    }

    /**
     * @param regFrom 注册来源
     */
    public void setRegFrom(Integer regFrom) {
        this.regFrom = regFrom;
    }

    /**
     * @return 登陆次数
     */
    @JsonView(WithoutPasswordView.class)
    public Integer getLoginTimes() {
        return loginTimes;
    }

    /**
     * @param loginTimes 登陆次数
     */
    public void setLoginTimes(Integer loginTimes) {
        this.loginTimes = loginTimes;
    }

    @JsonView(WithoutPasswordView.class)
    public UserProfiles getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfiles userProfile) {
        this.userProfile = userProfile;
    }

    /**
     * @return 密码类型，0：正常，1：无密码登录（手机登录）
     */
    public Integer getPwdType() {
        return pwdType;
    }

    /**
     * @param pwdType 密码类型，0：正常，1：无密码登录（手机登录）
     */
    public void setPwdType(Integer pwdType) {
        this.pwdType = pwdType;
    }

    /**
     * @return 注册时短信验证码
     */
    public String getSmsCode() {
        return smsCode;
    }

    /**
     * @param smsCode 注册时短信验证码
     */
    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    /**
     * @return 注册邀请人
     */
    public Long getInviter() {
        return inviter;
    }

    /**
     * @param inviter 注册邀请人
     */
    public void setInviter(Long inviter) {
        this.inviter = inviter;
    }

}
