package com.mifan.user.domain;

import com.mifan.user.type.AccountType;
import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-03-31
 */
public class UserAddresses extends BaseEntity {

    public static final String TABLE_NAME = "user_addresses";

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

    @NotNull(groups = {Post.class/*, Patch.class*/}, message = "{NotNull.UserAddresses.userId}")
    @Null(groups = {Patch.class}, message = "{MustNull.UserAddresses.userId}")
    private Long userId;

    @NotNull(groups = {Post.class, Patch.class}, message = "{NotNull.UserAddresses.code}")
    private Integer code;

    @Size(min = 1, max = 20, groups = {Post.class, Patch.class}, message = "{Size.UserAddresses.province}")
    private String province;

    @Size(min = 1, max = 20, groups = {Post.class, Patch.class}, message = "{Size.UserAddresses.city}")
    private String city;

    @Size(min = 1, max = 20, groups = {Post.class, Patch.class}, message = "{Size.UserAddresses.district}")
    private String district;

    @Pattern(regexp = AccountType.REGEXP_USERNAME, groups = {Post.class, Patch.class}, message = "{Pattern.UserAddresses.mobile}")
    @NotNull(groups = Post.class, message = "{NotNull.UserAddresses.mobile}")
    private String mobile;

    @Size(min = 1, max = 30, groups = {Post.class, Patch.class}, message = "{Size.UserAddresses.consignee}")
    @NotNull(groups = Post.class, message = "{NotNull.UserAddresses.consignee}")
    private String consignee;

    @Size(min = 1, max = 255, groups = {Post.class, Patch.class}, message = "{Size.UserAddresses.address}")
    @NotNull(groups = Post.class, message = "{NotNull.UserAddresses.address}")
    private String address;

    @Size(min = 1, max = 200, groups = {Post.class, Patch.class}, message = "{Size.UserAddresses.addressLabel}")
    private String addressLabel;

    @Size(min = 1, max = 20, groups = {Post.class, Patch.class}, message = "{Size.UserAddresses.postalCode}")
    private String postalCode;

    @Range(min = 0, max = 9, groups = {Post.class, Patch.class}, message = "{Range.UserAddresses.priority}")
    private Integer priority;

    public UserAddresses() {
    }

    public UserAddresses(Long id) {
        super(id);
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
