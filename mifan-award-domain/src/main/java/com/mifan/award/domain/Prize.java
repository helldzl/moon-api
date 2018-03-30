package com.mifan.award.domain;

import org.moonframework.model.mongodb.domain.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document(collection = "b_award_prize")
public class Prize extends BaseEntity {


    @Field("product_id")
    private String productId;

    @Field("category_id")
    private String categoryId;

    @Field("period")
    private String period;

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

    private Integer existingTimes;

    @Field("head_pic_list")
    private List<Pic> headPicList;

    @Field("content_pic_list")
    private List<Pic> contentPicList;

    @Field("lottery_period")
    private String lotteryPeriod;

    @Field("lottery_code")
    private String lotteryCode;

    @Field("lottery_time")
    private Date lotteryTime;

    @Field("record_time_total")
    private String recordTimeTotal;

    private List<Record> computeRecordList;

    @Field("code")
    private String code;

    @Field("user_id")
    private String userId;

    private String userName;

    private String userAvatar;

    @Field("user_codes")
    private List<String> userCodes;

    @Field("user_time")
    private Date userTime;

    @Field("finish_time")
    private Date finishTime;

    //TODO prize属性添加
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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

    @Override
    public Integer getState() {
        return state;
    }

    @Override
    public void setState(Integer state) {
        this.state = state;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getBuyTimes() {
        return buyTimes;
    }

    public void setBuyTimes(int buyTimes) {
        this.buyTimes = buyTimes;
    }

    public int getBuyUnit() {
        return buyUnit;
    }

    public void setBuyUnit(int buyUnit) {
        this.buyUnit = buyUnit;
    }

    public int getExistingTimes() {
        return existingTimes;
    }

    public void setExistingTimes(int existingTimes) {
        this.existingTimes = existingTimes;
    }

    public List<Pic> getHeadPicList() {
        return headPicList;
    }

    public void setHeadPicList(List<Pic> headPicList) {
        this.headPicList = headPicList;
    }

    public List<Pic> getContentPicList() {
        return contentPicList;
    }

    public void setContentPicList(List<Pic> contentPicList) {
        this.contentPicList = contentPicList;
    }

    public String getLotteryPeriod() {
        return lotteryPeriod;
    }

    public void setLotteryPeriod(String lotteryPeriod) {
        this.lotteryPeriod = lotteryPeriod;
    }

    public String getLotteryCode() {
        return lotteryCode;
    }

    public void setLotteryCode(String lotteryCode) {
        this.lotteryCode = lotteryCode;
    }

    public Date getLotteryTime() {
        return lotteryTime;
    }

    public void setLotteryTime(Date lotteryTime) {
        this.lotteryTime = lotteryTime;
    }

    public String getRecordTimeTotal() {
        return recordTimeTotal;
    }

    public void setRecordTimeTotal(String recordTimeTotal) {
        this.recordTimeTotal = recordTimeTotal;
    }

    public List<Record> getComputeRecordList() {
        return computeRecordList;
    }

    public void setComputeRecordList(List<Record> computeRecordList) {
        this.computeRecordList = computeRecordList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public List<String> getUserCodes() {
        return userCodes;
    }

    public void setUserCodes(List<String> userCodes) {
        this.userCodes = userCodes;
    }

    public Date getUserTime() {
        return userTime;
    }

    public void setUserTime(Date userTime) {
        this.userTime = userTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
}
