/**
 * 
 */
package com.mifan.user.domain.support;

/**
 * @author ZYW
 *
 */
public class Signature{
    private String signature;
    private String appid;
    private String noncestr;
    private long timestamp;
    
    public Signature(String signature, String appid, String noncestr, long timestamp) {
        super();
        this.signature = signature;
        this.appid = appid;
        this.noncestr = noncestr;
        this.timestamp = timestamp;
    }
    
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getNoncestr() {
        return noncestr;
    }
    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
