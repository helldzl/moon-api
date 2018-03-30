package com.mifan.award.domain.support;

import com.mifan.award.domain.Record;

import java.util.Date;
import java.util.List;

public class UserRecord {
    //奖品的id
    private String prizeId;
    //奖品的产品id
    private String prizeProductId;
    //奖品的期数
    private String prizePeriod;
    //图片 （默认取第一张header图）
    private String prizeHeaderPic;
    //奖品总需购买的次数
    private int prizeBuyTimes;
    //奖品的剩余购买次数
    private String prizeRestTimes;
    //奖品的名称
    private String prizeTitle;
    //奖品开始时间
    private Date prizeCreateTime;
    //奖品揭晓时间
    private Date prizeFinishTime;
    //奖品抽奖状态
    private int prizeState;
    //奖品抽奖幸运号码
    private String prizeCode;
    //奖品抽奖幸运用户
    private String awardUserId;
    //奖品抽奖幸运用户昵称
    private String awardNickname;
    //奖品抽奖幸运用户夺宝时间
    private Date awardUserTime;
    //奖品抽奖幸运用户参与的购买抽奖码
    private List<String> awardUserCodes;
    //参与人次
    private int recordBuyTimes;
    //返回抽奖记录的详细信息
    private List<Record> recordList;

    //晒单ID
    private String shareId;
    //是否已晒单(是个标记（未晒单：0；已晒单：1）)
    private Integer isShared;
    //晒单的主题
    private String shareSummary;
    //晒单的描述
    private String shareDescription;
    //晒单的图片
    private List<String> sharePicList;
    //晒单审核状态(待审核：1  审核通过：2  审核为通过：3  删除：0)
    private Integer shareState;
    //晒单创建时间
    private Date shareCreateTime;

    public String getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(String prizeId) {
        this.prizeId = prizeId;
    }

    public String getPrizeProductId() {
        return prizeProductId;
    }

    public void setPrizeProductId(String prizeProductId) {
        this.prizeProductId = prizeProductId;
    }

    public String getPrizePeriod() {
        return prizePeriod;
    }

    public void setPrizePeriod(String prizePeriod) {
        this.prizePeriod = prizePeriod;
    }

    public String getPrizeHeaderPic() {
        return prizeHeaderPic;
    }

    public void setPrizeHeaderPic(String prizeHeaderPic) {
        this.prizeHeaderPic = prizeHeaderPic;
    }

    public int getPrizeBuyTimes() {
        return prizeBuyTimes;
    }

    public void setPrizeBuyTimes(int prizeBuyTimes) {
        this.prizeBuyTimes = prizeBuyTimes;
    }

    public String getPrizeRestTimes() {
        return prizeRestTimes;
    }

    public void setPrizeRestTimes(String prizeRestTimes) {
        this.prizeRestTimes = prizeRestTimes;
    }

    public String getPrizeTitle() {
        return prizeTitle;
    }

    public void setPrizeTitle(String prizeTitle) {
        this.prizeTitle = prizeTitle;
    }

    public Date getPrizeCreateTime() {
        return prizeCreateTime;
    }

    public void setPrizeCreateTime(Date prizeCreateTime) {
        this.prizeCreateTime = prizeCreateTime;
    }

    public Date getPrizeFinishTime() {
        return prizeFinishTime;
    }

    public void setPrizeFinishTime(Date prizeFinishTime) {
        this.prizeFinishTime = prizeFinishTime;
    }

    public int getPrizeState() {
        return prizeState;
    }

    public void setPrizeState(int prizeState) {
        this.prizeState = prizeState;
    }

    public String getPrizeCode() {
        return prizeCode;
    }

    public void setPrizeCode(String prizeCode) {
        this.prizeCode = prizeCode;
    }

    public String getAwardUserId() {
        return awardUserId;
    }

    public void setAwardUserId(String awardUserId) {
        this.awardUserId = awardUserId;
    }

    public Date getAwardUserTime() {
        return awardUserTime;
    }

    public void setAwardUserTime(Date awardUserTime) {
        this.awardUserTime = awardUserTime;
    }

    public List<String> getAwardUserCodes() {
        return awardUserCodes;
    }

    public void setAwardUserCodes(List<String> awardUserCodes) {
        this.awardUserCodes = awardUserCodes;
    }

    public int getRecordBuyTimes() {
        return recordBuyTimes;
    }

    public void setRecordBuyTimes(int recordBuyTimes) {
        this.recordBuyTimes = recordBuyTimes;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public Integer getIsShared() {
        return isShared;
    }

    public void setIsShared(Integer isShared) {
        this.isShared = isShared;
    }

    public String getShareSummary() {
        return shareSummary;
    }

    public void setShareSummary(String shareSummary) {
        this.shareSummary = shareSummary;
    }

    public String getShareDescription() {
        return shareDescription;
    }

    public void setShareDescription(String shareDescription) {
        this.shareDescription = shareDescription;
    }

    public List<String> getSharePicList() {
        return sharePicList;
    }

    public void setSharePicList(List<String> sharePicList) {
        this.sharePicList = sharePicList;
    }

    public Integer getShareState() {
        return shareState;
    }

    public void setShareState(Integer shareState) {
        this.shareState = shareState;
    }

    public Date getShareCreateTime() {
        return shareCreateTime;
    }

    public void setShareCreateTime(Date shareCreateTime) {
        this.shareCreateTime = shareCreateTime;
    }

    public String getAwardNickname() {
        return awardNickname;
    }

    public void setAwardNickname(String awardNickname) {
        this.awardNickname = awardNickname;
    }
}
