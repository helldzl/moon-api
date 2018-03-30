package com.mifan.support.domain;


import javax.validation.constraints.NotNull;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Post;
/**
 * @author ZYW
 * @version 1.0
 * @since 2017-06-07
 */
public class EventLog extends BaseEntity {

    public static final String TABLE_NAME = "event_log";

    public static final String SOURCE = "source";
    public static final String SOURCE_TITLE = "source_title";
    public static final String EVENT_CODE = "event_code";
    public static final String URL_LOG = "url_log";
    public static final String METHOD_TYPE = "method_type";
    public static final String PARAMS = "params";
    public static final String VERSION = "version";
    public static final String PC_MOBILE = "pc_mobile";
    public static final String IS_SUCCESS = "is_success";
    public static final String IS_WECHAT = "is_wechat";
    public static final String SSID = "ssid";
    public static final String UA = "ua";
    public static final String IP = "ip";

    private static final long serialVersionUID = -3460051239110795838L;

    private String source;
    private String sourceTitle;
    @NotNull(groups = Post.class, message = "{NotNull.eventlog.eventCode}")
    private String eventCode;
    private String urlLog;
    private String methodType;
    private String params;
    private String version;
    private Integer pcMobile;
    private Integer isSuccess;
    private Integer isWechat;
    private String ssid;
    private String ua;
    private String ip;
    
    private Long topicId;
    private long pageViews;//浏览量

    public EventLog() {
    }

    public EventLog(Long id) {
        super(id);
    }

    /**
     * @return 来源
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source 来源
     */
    public void setSource(String source) {
        this.source = source;
    }
    /**
     * @return 来源标题
     */
    public String getSourceTitle() {
        return sourceTitle;
    }

    /**
     * @param sourceTitle 来源标题
     */
    public void setSourceTitle(String sourceTitle) {
        this.sourceTitle = sourceTitle;
    }
    /**
     * @return 事件编码
     */
    public String getEventCode() {
        return eventCode;
    }

    /**
     * @param eventCode 事件编码
     */
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }
    /**
     * @return 请求连接
     */
    public String getUrlLog() {
        return urlLog;
    }

    /**
     * @param urlLog 请求连接
     */
    public void setUrlLog(String urlLog) {
        this.urlLog = urlLog;
    }
    /**
     * @return 请求方法类型
     */
    public String getMethodType() {
        return methodType;
    }

    /**
     * @param methodType 请求方法类型
     */
    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }
    /**
     * @return 参数，json形式
     */
    public String getParams() {
        return params;
    }

    /**
     * @param params 参数，json形式
     */
    public void setParams(String params) {
        this.params = params;
    }
    /**
     * @return 事件是否成功
     */
    public Integer getIsSuccess() {
        return isSuccess;
    }

    /**
     * @param isSuccess 事件是否成功
     */
    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }
    /**
     * @return 是否是微信浏览
     */
    public Integer getIsWechat() {
        return isWechat;
    }

    /**
     * @param isWechat 是否是微信浏览
     */
    public void setIsWechat(Integer isWechat) {
        this.isWechat = isWechat;
    }
    /**
     * @return 会话标识
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * @param ssid 会话标识
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
    /**
     * @return 浏览器信息
     */
    public String getUa() {
        return ua;
    }

    /**
     * @param ua 浏览器信息
     */
    public void setUa(String ua) {
        this.ua = ua;
    }
    /**
     * @return ip地址
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip ip地址
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getPageViews() {
        return pageViews;
    }

    public void setPageViews(long pageViews) {
        this.pageViews = pageViews;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * pc端或移动端，1：pc，2：mobile
     * @return
     */
    public Integer getPcMobile() {
        return pcMobile;
    }
    /**
     * pc端或移动端，1：pc，2：mobile
     * @param pcMobile
     */
    public void setPcMobile(Integer pcMobile) {
        this.pcMobile = pcMobile;
    }

}
