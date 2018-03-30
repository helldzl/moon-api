package com.mifan.support.domain;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Patch;
import org.moonframework.validation.ValidationGroups.Post;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-05-22
 */
public class CommentConf extends BaseEntity {

    public static final String TABLE_NAME = "comment_conf";

    public static final String CONF_NAME = "conf_name";
    public static final String DESCRIPTION = "description";

    private static final long serialVersionUID = 4691643426615266929L;

    @NotBlank(groups = {Post.class, Patch.class}, message = "{NotNull.commentConf.confName}")
    private String confName;
    private String description;
    
    private List<Tag> tags;//该配置关联的标签
    
    private List<Long> tagIds;//该配置关联的标签id

    public CommentConf() {
    }

    public CommentConf(Long id) {
        super(id);
    }

    /**
     * @return 名称
     */
    public String getConfName() {
        return confName;
    }

    /**
     * @param confName 名称
     */
    public void setConfName(String confName) {
        this.confName = confName;
    }
    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CommentConf [confName=" + confName + ", description="
                + description + ", tags=" + tags + ", tagIds=" + tagIds + "]";
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

}
