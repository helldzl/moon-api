package com.mifan.award.domain;

import org.moonframework.model.mongodb.domain.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "b_award_category")
public class Category extends BaseEntity {

    @Field("name")
    private String name;
    @Field("css_class")
    private String cssClass;

    @Field("pic_list")
    private List<Pic> picList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public List<Pic> getPicList() {
        return picList;
    }

    public void setPicList(List<Pic> picList) {
        this.picList = picList;
    }
}
