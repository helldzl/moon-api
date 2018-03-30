/**
 * 
 */
package com.mifan.user.utils.wxpay;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ZYW
 *
 */
@ConfigurationProperties(WXPayConfig.PREFIX)
public class WXPayConfig {

    public static final String PREFIX = "moon.data.wxpay";

    private String mchID = "1488379182";//商户号

    private String notifyUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";

    private String certPath = "D://cert/apiclient_cert.p12";

    private String key = "mifanxingsongningbo1234588888888";//商户号API密钥

    private String sendName="米饭平台";//商户名称（微信红包发送者名称,最好用英文）

    private String wishing="祝工作顺利";//红包祝福语

    private String clientIp="192.168.1.169";//ip地址

    private String actName="米饭网扫码送红包";//活动名称
    
    private String remark="没有备注";

    private String totalNum = "1";//红包发放总人数

//    String scene_id="PRODUCT_1";//场景id，商品促销，微信红包200以上必填

    public InputStream getCertStream() throws Exception {
        File file = new File(certPath);
        InputStream inputStream = new FileInputStream(file);
        byte[] certData = new byte[(int) file.length()];
        inputStream.read(certData);
        inputStream.close();
        return new ByteArrayInputStream(certData);
    }

    public Map<String,String> getParams(){
        Map<String,String> params = new HashMap<String,String>(16);
        params.put("act_name", this.actName);
        params.put("client_ip", this.clientIp);
        params.put("mch_id", this.mchID);
        params.put("remark", this.remark);
        params.put("send_name", this.sendName);
        params.put("total_num", this.totalNum);
        params.put("wishing", this.wishing);
        return params;
    }

    public String getMchID() {
        return mchID;
    }

    public void setMchID(String mchID) {
        this.mchID = mchID;
    }
    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getWishing() {
        return wishing;
    }

    public void setWishing(String wishing) {
        this.wishing = wishing;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

}
