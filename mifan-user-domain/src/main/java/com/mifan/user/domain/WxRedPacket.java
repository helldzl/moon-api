package com.mifan.user.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-09-14
 */
public class WxRedPacket extends BaseEntity {

    public static final String TABLE_NAME = "wx_red_packet";

    public static final String RE_OPENID = "re_openid";
    public static final String TOTAL_AMOUNT = "total_amount";
    public static final String USER_ID = "user_id";
    public static final String WX_USER_ID = "wx_user_id";
    public static final String IS_SUCCESS = "is_success";
    public static final String MCH_BILLNO = "mch_billno";
    public static final String SEND_LISTID = "send_listid";
    public static final String NONCE_STR = "nonce_str";
    public static final String ERR_CODE = "err_code";
    public static final String ERR_CODE_DES = "err_code_des";

    private static final long serialVersionUID = -4361781628180653670L;

    private String reOpenid;
    private Double totalAmount;
    private Long userId;
    private Long wxUserId;
    private Integer isSuccess;
    private String mchBillno;
    private String sendListid;
    private String nonceStr;
    private String errCode;
    private String errCodeDes;

    public WxRedPacket() {
    }

    public WxRedPacket(Long id) {
        super(id);
    }
    
    public WxRedPacket(Double totalAmount, Long userId, String mchBillno, String nonceStr) {
        this.totalAmount = totalAmount;
        this.userId = userId;
        this.mchBillno = mchBillno;
        this.nonceStr = nonceStr;
    }

    /**
     * @return 微信公众号唯一标识
     */
    public String getReOpenid() {
        return reOpenid;
    }

    /**
     * @param reOpenid 微信公众号唯一标识
     */
    public void setReOpenid(String reOpenid) {
        this.reOpenid = reOpenid;
    }
    /**
     * @return 红包金额
     */
    public Double getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount 红包金额
     */
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    /**
     * @return 手机帐号的ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId 手机帐号的ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    /**
     * @return 微信帐号的ID
     */
    public Long getWxUserId() {
        return wxUserId;
    }

    /**
     * @param wxUserId 微信帐号的ID
     */
    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }
    /**
     * @return 是否成功，0：失败，1：成功
     */
    public Integer getIsSuccess() {
        return isSuccess;
    }

    /**
     * @param isSuccess 是否成功，0：失败，1：成功
     */
    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }
    /**
     * @return 订单号
     */
    public String getMchBillno() {
        return mchBillno;
    }

    /**
     * @param mchBillno 订单号
     */
    public void setMchBillno(String mchBillno) {
        this.mchBillno = mchBillno;
    }
    /**
     * @return 微信单号
     */
    public String getSendListid() {
        return sendListid;
    }

    /**
     * @param sendListid 微信单号
     */
    public void setSendListid(String sendListid) {
        this.sendListid = sendListid;
    }
    /**
     * @return 随机字符串
     */
    public String getNonceStr() {
        return nonceStr;
    }

    /**
     * @param nonceStr 随机字符串
     */
    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }
    /**
     * @return 错误代码
     */
    public String getErrCode() {
        return errCode;
    }

    /**
     * @param errCode 错误代码
     */
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
    /**
     * @return 错误信息
     */
    public String getErrCodeDes() {
        return errCodeDes;
    }

    /**
     * @param errCodeDes 错误信息
     */
    public void setErrCodeDes(String errCodeDes) {
        this.errCodeDes = errCodeDes;
    }

}
