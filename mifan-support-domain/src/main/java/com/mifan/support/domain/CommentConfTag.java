package com.mifan.support.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-05-22
 */
public class CommentConfTag extends BaseEntity {

    public static final String TABLE_NAME = "comment_conf_tag";

    public static final String CONF_ID = "conf_id";
    public static final String TAG_ID = "tag_id";

    private static final long serialVersionUID = -8346583921052197489L;

    private Long confId;
    private Long tagId;

    public CommentConfTag() {
    }

    public CommentConfTag(Long id) {
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
