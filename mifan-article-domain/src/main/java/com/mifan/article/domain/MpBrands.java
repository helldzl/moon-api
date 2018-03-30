package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
public class MpBrands extends BaseEntity {

    public static final String TABLE_NAME = "mp_brands";

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String FEATURE = "feature";
    public static final String IMAGE = "image";

    private static final long serialVersionUID = 46525112142115035L;

    private String title;
    private String description;
    private String feature;
    private String image;

    public MpBrands() {
    }

    public MpBrands(Long id) {
        super(id);
    }

    /**
     * @return 品牌名
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 品牌名
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
    public String getFeature() {
        return feature;
    }

    /**
     * @param feature 
     */
    public void setFeature(String feature) {
        this.feature = feature;
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

}
