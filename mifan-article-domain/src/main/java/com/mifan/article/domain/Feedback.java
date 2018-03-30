package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
public class Feedback extends BaseEntity {

    // TODO 与AccountType中的正则冗余了
    public static final String REGEXP_MOBILE = "(^1(3[0-9]|4[579]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$)";
    public static final String REGEXP_EMAIL = "(^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$)";

    public static final String TABLE_NAME = "feedback";

    public static final String USERNAME = "username";
    public static final String MOBILE = "mobile";
    public static final String EMAIL = "email";
    public static final String SUGGESTION = "suggestion";

    private static final long serialVersionUID = 6211711015126801903L;

    @NotNull(groups = Post.class, message = "{NotNull.Feedback.username}")
    @Size(min = 1, max = 100, groups = {Post.class, Patch.class}, message = "{Size.Feedback.username}")
    private String username;

    @NotNull(groups = Post.class, message = "{NotNull.Feedback.mobile}")
    @Pattern(regexp = REGEXP_MOBILE, groups = {Post.class, Patch.class}, message = "{Pattern.Feedback.mobile}")
    private String mobile;

    @NotNull(groups = Post.class, message = "{NotNull.Feedback.email}")
    @Size(min = 2, max = 255, groups = {Post.class, Patch.class}, message = "{Size.Feedback.email}")
    @Pattern(regexp = REGEXP_EMAIL, groups = {Post.class, Patch.class}, message = "{Pattern.Feedback.email}")
    private String email;

    @NotNull(groups = Post.class, message = "{NotNull.Feedback.suggestion}")
    @Size(min = 1, max = 2048, groups = {Post.class, Patch.class}, message = "{Size.Feedback.suggestion}")
    private String suggestion;

    public Feedback() {
    }

    public Feedback(Long id) {
        super(id);
    }

    /**
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return 手机
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile 手机
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return 建议
     */
    public String getSuggestion() {
        return suggestion;
    }

    /**
     * @param suggestion 建议
     */
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

}
