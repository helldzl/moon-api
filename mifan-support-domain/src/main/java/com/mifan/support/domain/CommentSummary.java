package com.mifan.support.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-06-06
 */
public class CommentSummary extends BaseEntity {

    public static final String TABLE_NAME = "comment_summary";

    public static final String CONF_ID = "conf_id";
    public static final String THEME_ID = "theme_id";
    public static final String COMMENT_ID = "comment_id";
    public static final String TYPE = "type";
    public static final String TAG_ID = "tag_id";
    public static final String SUM = "sum";

    private static final long serialVersionUID = -6616155106281094072L;

    private Long confId;
    private Long themeId;
    private Long commentId;
    private Integer type;
    private Long tagId;
    private Integer sum;

    public CommentSummary() {
    }

    public CommentSummary(Long id) {
        super(id);
    }

    /**
     * @return 评论配置标识
     */
    public Long getConfId() {
        return confId;
    }

    /**
     * @param confId 评论配置标识
     */
    public void setConfId(Long confId) {
        this.confId = confId;
    }
    /**
     * @return 主题标识
     */
    public Long getThemeId() {
        return themeId;
    }

    /**
     * @param themeId 主题标识
     */
    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }
    /**
     * @return 评论id
     */
    public Long getCommentId() {
        return commentId;
    }

    /**
     * @param commentId 评论id
     */
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
    /**
     * @return 统计类型，0：点赞统计，1：标签统计
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 统计类型，0：点赞统计，1：标签统计
     */
    public void setType(Integer type) {
        this.type = type;
    }
    /**
     * @return 标签标识
     */
    public Long getTagId() {
        return tagId;
    }

    /**
     * @param tagId 标签标识
     */
    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
    /**
     * @return 
     */
    public Integer getSum() {
        return sum;
    }

    /**
     * @param sum 
     */
    public void setSum(Integer sum) {
        this.sum = sum;
    }

}
