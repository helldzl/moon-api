package com.mifan.article.domain.support;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-03-31
 */
public class UserAddresses {

    public static final String TABLE_NAME = "user_addresses";

    public static final String ID = "id";
    public static final String ENABLED = "enabled";
    public static final String CREATOR = "creator";
    public static final String MODIFIER = "modifier";
    public static final String CREATED = "created";
    public static final String MODIFIED = "modified";
    public static final String USER_ID = "user_id";
    public static final String CODE = "code";
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    public static final String DISTRICT = "district";
    public static final String MOBILE = "mobile";
    public static final String CONSIGNEE = "consignee";
    public static final String ADDRESS = "address";
    public static final String PRIORITY = "priority";

    private static final long serialVersionUID = -1227259554221492958L;

    private Long id;

    private Integer enabled;

    private Long creator;

    private Long modifier;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modified;

    private Long userId;

    private Integer code;

    private String province;

    private String city;

    private String district;

    private String mobile;

    private String consignee;

    private String address;

    private String addressLabel;

    private String postalCode;

    private Integer priority;

    public UserAddresses() {
    }

    public UserAddresses(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Integer getEnabled() {
        return enabled;
    }


    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }


    public Long getCreator() {
        return creator;
    }


    public void setCreator(Long creator) {
        this.creator = creator;
    }


    public Long getModifier() {
        return modifier;
    }


    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }


    public Date getCreated() {
        return created;
    }


    public void setCreated(Date created) {
        this.created = created;
    }


    public Date getModified() {
        return modified;
    }


    public void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * @return FK : user id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId FK : user id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return 行政区划代码[6位]
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code 行政区划代码[6位]
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * @return 省
     */
    public String getProvince() {
        return province;
    }

    /**
     * @param province 省
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * @return 市
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city 市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return 区
     */
    public String getDistrict() {
        return district;
    }

    /**
     * @param district 区
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * @return 手机
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile 手机
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return 收货人/收件人
     */
    public String getConsignee() {
        return consignee;
    }

    /**
     * @param consignee 收货人/收件人
     */
    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    /**
     * @return 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressLabel() {
        return addressLabel;
    }

    public void setAddressLabel(String addressLabel) {
        this.addressLabel = addressLabel;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return 优先级
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority 优先级
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
