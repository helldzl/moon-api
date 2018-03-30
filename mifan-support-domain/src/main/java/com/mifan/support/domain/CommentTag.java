package com.mifan.support.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-05-22
 */
public class CommentTag extends BaseEntity {

    public static final String TABLE_NAME = "comment_tag";

    public static final String COMMENT_ID = "comment_id";
    public static final String TAG_ID = "tag_id";

    private static final long serialVersionUID = 6295150097222033707L;

    private Long commentId;
    private Long tagId;

    public CommentTag() {
    }

    public CommentTag(Long id) {
        super(id);
    }

    /**
     * @return 评论标识
     */
    public Long getCommentId() {
        return commentId;
    }

    /**
     * @param commentId 评论标识
     */
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
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

}
