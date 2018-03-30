package com.mifan.article.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Patch;
import org.moonframework.validation.ValidationGroups.Post;

/**
 * @author LiuKai
 * @version 1.0
 * @since 2017-08-09
 */
public class Links extends BaseEntity {

    public static final String TABLE_NAME = "links";

    public static final String PARENT_ID = "parent_id";
    public static final String TYPE = "type";
    public static final String SORT = "sort";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";
    public static final String HREF = "href";
    public static final String BLANK = "blank";

    private static final long serialVersionUID = -7128015740307190724L;

    private Long parentId;
    @NotNull(groups = {Post.class}, message = "{NotNull.Links.type}")
    @Range(min = 1,groups = Patch.class,message="{Error.Links.type}")
    private Integer type;
    @NotNull(groups = {Post.class}, message = "{NotNull.Links.sort}")
    private Integer sort;
    @NotBlank(groups = {Post.class,Patch.class}, message = "{NotNull.Links.title}")
    private String title;
    private String description;
    private String image;
    private String href;
    private Integer blank;

    public Links() {
    }

    public Links(Long id) {
        super(id);
    }

    /**
     * @return 
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId 
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    /**
     * @return 1:PC导航, 2:手机???, 3:手机导航 4:友情链接 5 广告
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 1:PC导航, 2:手机???, 3:手机导航 4:友情链接 5 广告
     */
    public void setType(Integer type) {
        this.type = type;
    }
    /**
     * @return 
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * @param sort 
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }
    /**
     * @return 
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return 
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return 
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image 
     */
    public void setImage(String image) {
        this.image = image;
    }
    /**
     * @return 
     */
    public String getHref() {
        return href;
    }

    /**
     * @param href 
     */
    public void setHref(String href) {
        this.href = href;
    }
    /**
     * @return 0不在窗口打开   1新窗口打开
     */
    public Integer getBlank() {
        return blank;
    }

    /**
     * @param blank 0不在窗口打开   1新窗口打开
     */
    public void setBlank(Integer blank) {
        this.blank = blank;
    }

}
