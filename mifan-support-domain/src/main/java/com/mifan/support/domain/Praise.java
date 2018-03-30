package com.mifan.support.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Post;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-05-22
 */
public class Praise extends BaseEntity implements Cloneable{

    public static final String TABLE_NAME = "praise";

    public static final String TYPE = "type";
    public static final String CONF_ID = "conf_id";
    public static final String THEME_ID = "theme_id";
    public static final String COMMENT_ID = "comment_id";
    public static final String SCORE = "score";
    public static final String CREATOR = "creator";

    private static final long serialVersionUID = -2460975200610481470L;

    @NotNull(groups = Post.class, message = "{@NotNull.Praise.type}")
    @Range(min = 0, max = 1,groups = Post.class,message="{Error.Praise.type}")
    private Integer type;
    @NotNull(groups = Post.class, message = "{@NotNull.Praise.confId}")
    private Long confId;
    @NotNull(groups = Post.class, message = "{@NotNull.Praise.themeId}")
    private Long themeId;
    private Long commentId;
    @NotNull(groups = Post.class, message = "{@NotNull.Praise.score}")
    @Range(min = -1, max = 1,groups = Post.class,message="{Error.Praise.score}")
    private Integer score;

    public Praise() {
    }

    public Praise(Long id) {
        super(id);
    }

    /**
     * @return 点赞类型，0：对主题赞踩，1：对comment赞踩
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 点赞类型，0：对主题赞踩，1：对comment赞踩
     */
    public void setType(Integer type) {
        this.type = type;
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
     * @return 分数，1：点赞，-1：赞
     */
    public Integer getScore() {
        return score;
    }

    /**
     * @param score 分数，1：点赞，-1：赞
     */
    public void setScore(Integer score) {
        this.score = score;
    }
    @Override  
    public Object clone() {
        Praise pra = null;
        try{
            pra = (Praise)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return pra;
    }
}
