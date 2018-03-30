package com.mifan.article.domain;

import com.mifan.article.domain.support.ValidationGroups.AdminPost;
import com.mifan.article.domain.support.ValidationGroups.TranslateTaskPatch;
import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Patch;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-11-02
 */
public class TranslateTask extends BaseEntity {

    public static final String ROLE_TRANSLATOR = "translator";
    public static final String ROLE_AUDITOR = "auditor";
    
    public static final String TABLE_NAME = "translate_task";

    public static final String TOPIC_ID = "topic_id";
    public static final String POST_ID = "post_id";
    public static final String STATE = "state";
    public static final String WORDS_NUM = "words_num";
    public static final String WORDS_NUM_CN = "words_num_cn";
    public static final String BONUS = "bonus";
    public static final String TRANSLATOR = "translator";
    public static final String AUDITOR = "auditor";
    public static final String AUDIT_OPINION = "audit_opinion";

    private static final long serialVersionUID = 2562966697733382198L;

    @NotNull(groups = {AdminPost.class}, message = "{NotNull.TranslateTask.topicId}")//,TranslateTaskPatch.class
    @Null(groups = {Patch.class}, message = "{MustNull.TranslateTask.topicId}")
    private Long topicId;
    
    @Null(groups = {AdminPost.class,Patch.class}, message = "{MustNull.TranslateTask.postId}")
    private Long postId;
    
    @Null(groups = {AdminPost.class}, message = "{MustNull.TranslateTask.state}")
    @NotNull(groups = {TranslateTaskPatch.class}, message = "{NotNull.TranslateTask.state}")
    @Range(min = 2, max = 3,groups = {TranslateTaskPatch.class},message="{Error.Posts.state}")
    private Integer state;
    
    @Null(groups = {TranslateTaskPatch.class}, message = "{MustNull.TranslateTask.wordsNum}")
    private Integer wordsNum;
    
    @Null(groups = {TranslateTaskPatch.class}, message = "{MustNull.TranslateTask.wordsNum}")
    private Integer wordsNumCn;
    
    @Null(groups = {TranslateTaskPatch.class}, message = "{MustNull.TranslateTask.bonus}")
    private BigDecimal bonus;
    
    @Null(groups = {AdminPost.class,Patch.class}, message = "{MustNull.TranslateTask.translator}")
    private Long translator;
    
    @Null(groups = {AdminPost.class,TranslateTaskPatch.class,Patch.class}, message = "{MustNull.TranslateTask.auditor}")
    private Long auditor;
    
    @Null(groups = {AdminPost.class,TranslateTaskPatch.class,Patch.class}, message = "{MustNull.TranslateTask.auditOpinion}")
    private String auditOpinion;

//    @NotNull(groups = {AdminPost.class}, message = "{NotNull.TranslateTask.post}")
    @Null(groups = {Patch.class}, message = "{NotNull.TranslateTask.post}")
    @Valid
    private Posts post;
    
    private String title;//文章标题
    private String topicType;//文章类型
    
    private String translatorAccount;//翻译人员帐号
    private String auditorAccount;//审核人员帐号
    private String modifierAccount;//修改人帐号
    private String creatorAccount;//创建人帐号

    public TranslateTask() {
    }

    public TranslateTask(Long id) {
        super(id);
    }

    /**
     * @return 
     */
    public Long getTopicId() {
        return topicId;
    }

    /**
     * @param topicId 
     */
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
    /**
     * @return 翻译文档关联id
     */
    public Long getPostId() {
        return postId;
    }

    /**
     * @param postId 翻译文档关联id
     */
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    /**
     * @return 状态：1：待领取，2：未提交，3：待审核，4：审核中，5：审核失败，6：审核成功，7：已支付
     */
    public Integer getState() {
        return state;
    }

    /**
     * @param state 状态：1：待领取，2：未提交，3：待审核，4：审核中，5：审核失败，6：审核成功，7：已支付
     */
    public void setState(Integer state) {
        this.state = state;
    }
    /**
     * @return 单词数
     */
    public Integer getWordsNum() {
        return wordsNum;
    }

    /**
     * @param wordsNum 单词数
     */
    public void setWordsNum(Integer wordsNum) {
        this.wordsNum = wordsNum;
    }
    /**
     * @return 中文字数
     */
    public Integer getWordsNumCn() {
        return wordsNumCn;
    }

    /**
     * @param wordsNumCn 中文字数
     */
    public void setWordsNumCn(Integer wordsNumCn) {
        this.wordsNumCn = wordsNumCn;
    }
    /**
     * @return 支付金额
     */
    public BigDecimal getBonus() {
        return bonus;
    }

    /**
     * @param bonus 支付金额
     */
    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }
    /**
     * @return 翻译人
     */
    public Long getTranslator() {
        return translator;
    }

    /**
     * @param translator 翻译人
     */
    public void setTranslator(Long translator) {
        this.translator = translator;
    }
    /**
     * @return 
     */
    public Long getAuditor() {
        return auditor;
    }

    /**
     * @param auditor 
     */
    public void setAuditor(Long auditor) {
        this.auditor = auditor;
    }
    /**
     * @return 审核员反馈
     */
    public String getAuditOpinion() {
        return auditOpinion;
    }

    /**
     * @param auditOpinion 审核员反馈
     */
    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public Posts getPost() {
        return post;
    }

    public void setPost(Posts post) {
        this.post = post;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public String getTranslatorAccount() {
        return translatorAccount;
    }

    public void setTranslatorAccount(String translatorAccount) {
        this.translatorAccount = translatorAccount;
    }

    public String getAuditorAccount() {
        return auditorAccount;
    }

    public void setAuditorAccount(String auditorAccount) {
        this.auditorAccount = auditorAccount;
    }

    public String getModifierAccount() {
        return modifierAccount;
    }

    public void setModifierAccount(String modifierAccount) {
        this.modifierAccount = modifierAccount;
    }

    public String getCreatorAccount() {
        return creatorAccount;
    }

    public void setCreatorAccount(String creatorAccount) {
        this.creatorAccount = creatorAccount;
    }
    
}
