package com.mifan.user.type;

public enum SpecType {

    /**
     * 修改手机号
     */
    MODIFY_MOBILE_PHONE("modifyMobile"),

    /**
     * 手机注册
     */
    MOBILE_REGISTER("mobileRegister"),
    /**
     * 手机登陆
     */
    MOBILE_LOGIN("mobileLogin"),
    /**
     * 找回密码
     */
    MOBILE_FIND_PASSWORD("mobileFindPwd"),
    /**
     * 网站忘记密码
     */
    WEB_FIND_PASSWORD("webFindPwd"),
    /**
     * 网站注册
     */
    WEB_REGISTER("webRegister"),
    /**
     * 网站登陆
     */
    WEB_LOGIN("webLogin");

    private String description;

    SpecType(String description) {
        this.description = description;
    }

    public static SpecType getValueByDesc(String description) {
        for (SpecType sp : SpecType.values()) {
            if (sp.getDescription().equals(description)) {
                return sp;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }
}
