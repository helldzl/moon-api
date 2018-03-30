package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
public class MpBrandCategories extends BaseEntity {

    public static final String TABLE_NAME = "mp_brand_categories";

    public static final String BRAND_ID = "brand_id";
    public static final String MP_CATEGORY_ID = "mp_category_id";

    private static final long serialVersionUID = 6341299560379607874L;

    private Long brandId;
    private Long mpCategoryId;

    public MpBrandCategories() {
    }

    public MpBrandCategories(Long id) {
        super(id);
    }

    /**
     * @return 品牌标识
     */
    public Long getBrandId() {
        return brandId;
    }

    /**
     * @param brandId 品牌标识
     */
    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }
    /**
     * @return 分类标识
     */
    public Long getMpCategoryId() {
        return mpCategoryId;
    }

    /**
     * @param mpCategoryId 分类标识
     */
    public void setMpCategoryId(Long mpCategoryId) {
        this.mpCategoryId = mpCategoryId;
    }

}
