/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.sku.domain.support.TagSummary
 *
 * @description:承载标签统计信息
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年6月5日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.support.domain.support;

/**
 * @author ZYW
 *
 */
public class TagSummary {
    private Long tagId;
    
    private String tagName;
    
    private Integer sum;

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    
    
}
