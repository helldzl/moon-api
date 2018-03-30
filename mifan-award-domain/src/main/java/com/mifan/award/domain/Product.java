package com.mifan.award.domain;

import org.moonframework.model.mongodb.domain.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "b_award_product")
public class Product extends BaseEntity {
    @Field("category_id")
    private String categoryId;
    @Field("title")
    private String title;
    @Field("desc")
    private String desc;
    @Field("price")
    private String price;
    @Field("buy_unit")
    private Integer buyUnit;
    @Field("buy_times")
    private Integer buyTimes;
    @Field("image_url")
    private String imageURL;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getBuyUnit() {
        return buyUnit;
    }

    public void setBuyUnit(int buyUnit) {
        this.buyUnit = buyUnit;
    }

    public int getBuyTimes() {
        return buyTimes;
    }

    public void setBuyTimes(int buyTimes) {
        this.buyTimes = buyTimes;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
