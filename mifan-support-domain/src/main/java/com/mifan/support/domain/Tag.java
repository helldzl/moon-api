package com.mifan.support.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Patch;
import org.moonframework.validation.ValidationGroups.Post;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-05-22
 */
public class Tag extends BaseEntity {

    public static final String TABLE_NAME = "tag";

    public static final String TAG_NAME = "tag_name";
    public static final String DESCRIPTION = "description";

    private static final long serialVersionUID = -7588029082938050290L;

    @NotBlank(groups = {Post.class, Patch.class}, message = "{NotNull.Tag.tagName}")
    private String tagName;
    private String description;

    public Tag() {
    }

    public Tag(Long id) {
        super(id);
    }

    /**
     * @return 名称
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * @param tagName 名称
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
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

}
