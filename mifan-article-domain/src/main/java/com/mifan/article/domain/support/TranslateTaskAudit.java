/**
 * 审核翻译任务需要的请求参数
 */
package com.mifan.article.domain.support;

import com.mifan.article.domain.Posts;
import com.mifan.article.domain.support.ValidationGroups.AuditorPatch;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author ZYW
 *
 */
public class TranslateTaskAudit {

    private Long id;
    @NotNull(groups = {AuditorPatch.class}, message = "{NotNull.TranslateTaskAudit.state}")
    @Range(min = 4, max = 6,groups = {AuditorPatch.class},message="{Error.TranslateTaskAudit.state}")
    private Integer state;

    private Long auditor;

    private String auditOpinion;

    private Integer wordsNumCn;

//    @NotNull(groups = {AuditorPatch.class}, message = "{NotNull.TranslateTaskAudit.postId}")
//    private Long postId;

//    @Null(groups = {AuditorPatch.class}, message = "{NotNull.TranslateTaskAudit.posts}")
    @Valid
    private Posts posts;

    
    public TranslateTaskAudit() {
        super();
    }
    public TranslateTaskAudit(Long id) {
        super();
        this.id = id;
    }
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
    public String getAuditOpinion() {
        return auditOpinion;
    }
    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public Posts getPosts() {
        return posts;
    }
    public void setPosts(Posts posts) {
        this.posts = posts;
    }
    public Long getAuditor() {
        return auditor;
    }
    public void setAuditor(Long auditor) {
        this.auditor = auditor;
    }
    public Integer getWordsNumCn() {
        return wordsNumCn;
    }
    public void setWordsNumCn(Integer wordsNumCn) {
        this.wordsNumCn = wordsNumCn;
    }
}
