package com.mifan.award.domain;

import org.moonframework.model.mongodb.domain.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "b_award_record")
public class Record extends BaseEntity {

    @Field("prize_id")
    private String prizeId;
    private String prizeTitle;
    @Field("user_id")
    private String userId;
    private String userName;
    private String userAvatar;
    @Field("draw_code")
    private List<String> drawCode;

    private Integer personalTimes;
    @Field("ip")
    private String ip;
    @Field("ip_state")
    private String ipState;
    @Field("ip_address")
    private String ipAddress;

    public String getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(String prizeId) {
        this.prizeId = prizeId;
    }

    public String getPrizeTitle() {
        return prizeTitle;
    }

    public void setPrizeTitle(String prizeTitle) {
        this.prizeTitle = prizeTitle;
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

    public List<String> getDrawCode() {
        return drawCode;
    }

    public void setDrawCode(List<String> drawCode) {
        this.drawCode = drawCode;
    }

    public Integer getPersonalTimes() {
        return personalTimes;
    }

    public void setPersonalTimes(Integer personalTimes) {
        this.personalTimes = personalTimes;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIpState() {
        return ipState;
    }

    public void setIpState(String ipState) {
        this.ipState = ipState;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}





