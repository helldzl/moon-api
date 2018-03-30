package com.mifan.article.service.model;

import org.hibernate.validator.constraints.NotBlank;
import org.moonframework.validation.ValidationGroups.Post;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by LiuKai on 2017/3/3.
 */
public class AuditModel implements Serializable{
    private static final long serialVersionUID = 1L;
    @NotNull(groups = Post.class, message = "{NotNull.AuditModel.id}")
    private Long id;
    @NotNull(groups = Post.class, message = "{NotNull.AuditModel.state}")
    private Integer state;
    @NotBlank(groups = Post.class, message = "{NotNull.AuditModel.auditCommit}")
    private String auditCommit;
    @NotNull(groups = Post.class, message = "{NotNull.AuditModel.auditId}")
    private Long auditId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getAuditCommit() {
        return auditCommit;
    }

    public void setAuditCommit(String auditCommit) {
        this.auditCommit = auditCommit;
    }

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }
    
}
