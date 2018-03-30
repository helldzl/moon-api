package com.mifan.article.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Patch;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
public class Moderation extends BaseEntity {

    public static final String TABLE_NAME = "moderation";

    public static final String POST_ID = "post_id";
    public static final String REMARK = "remark";
    public static final String STATE = "state";

    private static final long serialVersionUID = -1344170348438271L;

    /*@NotNull(groups = {Patch.class}, message = "{NotNull.Moderations.postId}")*/
    private Long postId;
    private String remark;
    @NotNull(groups = {Patch.class}, message = "{NotNull.Moderations.state}")
    @Range(min = 3, max = 4,groups = Patch.class,message="{Error.Moderations.state}")
    private Integer state;//1:草稿箱, 2:待审核, 3:审核通过, 4:审核失败, 5:被覆盖, 9:已删除
    
    private String title;//标题
    
    @Range(min = 0, max = 100,groups = Patch.class,message="{Error.Moderations.riceNum}")
    private Integer riceNum;//奖励的米粒个数

    public Moderation() {
    }

    public Moderation(Long id) {
        super(id);
    }

    /**
     * @return POST ID
     */
    public Long getPostId() {
        return postId;
    }

    /**
     * @param postId POST ID
     */
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    /**
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
    /**
     * @return 状态: {1:草稿箱, 2:待审核, 3:审核通过, 4:审核失败, 5:被覆盖, 9:已删除}
     */
    public Integer getState() {
        return state;
    }

    /**
     * @param state 状态: {1:草稿箱, 2:待审核, 3:审核通过, 4:审核失败, 5:被覆盖, 9:已删除}
     */
    public void setState(Integer state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getRiceNum() {
        return riceNum;
    }

    public void setRiceNum(Integer riceNum) {
        this.riceNum = riceNum;
    }

}
