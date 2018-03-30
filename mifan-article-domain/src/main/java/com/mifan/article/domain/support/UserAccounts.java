package com.mifan.article.domain.support;

import org.moonframework.model.mybatis.domain.BaseEntity;

import java.nio.charset.Charset;
import java.util.zip.CRC32;

/**
 * @author ZYW
 * @version 1.0
 * @since 2016-10-28
 */
public class UserAccounts extends BaseEntity {

    public static final String TABLE_NAME = "user_accounts";

    public static final String USER_ID = "user_id";
    public static final String ACCOUNT_TYPE = "account_type";
    public static final String ACCOUNT = "account";
    public static final String ISBIND = "isbind";
    public static final String ACCOUNT_CRC = "account_crc";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String OPENID = "openid";

    private static final long serialVersionUID = 3737141841707926338L;

    private Long userId;
    private Integer accountType;
    private String account;
    private Integer isbind;

    // CRC是无符号的int, 所以java需要使用Long来保存
    private Long accountCrc;
    private String accessToken;
    private String refreshToken;
    private String openid;

    public UserAccounts() {
    }

    public UserAccounts(Long id) {
        super(id);
    }

    /**
     * @return FK 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId FK 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return 账号类型 0:mobile 1:email 3:微信
     */
    public Integer getAccountType() {
        return accountType;
    }

    /**
     * @param accountType 账号类型 0:mobile 1:email 3:微信
     */
    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    /**
     * @return 登陆账号；第三方登录的open_id，针对公众号唯一
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account 登陆账号；第三方登录的open_id，针对公众号唯一
     */
    public void setAccount(String account) {
        CRC32 crc32 = new CRC32();
        crc32.update(account.getBytes(Charset.defaultCharset()));
        this.account = account;
        this.accountCrc = crc32.getValue();
    }

    /**
     * @return 该帐号是否已绑定主账号，0：否，1：是
     */
    public Integer getIsbind() {
        return isbind;
    }

    /**
     * @param isbind 该帐号是否已绑定主账号，0：否，1：是
     */
    public void setIsbind(Integer isbind) {
        this.isbind = isbind;
    }

    /**
     * @return CRC
     */
    public Long getAccountCrc() {
        return accountCrc;
    }

    /**
     * @param accountCrc CRC
     */
    public void setAccountCrc(Long accountCrc) {
        this.accountCrc = accountCrc;
    }

    /**
     * @return 第三方授权令牌，有效时间2小时
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken 第三方授权令牌，有效时间2小时
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return 刷新令牌的参数，有效时间30天
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * @param refreshToken 刷新令牌的参数，有效时间30天
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

}
