package com.mifan.user.type;

public enum AppType {

    BUDEE_QLY(100, "千里眼", "qly.co", "budee_view"),
    BUDEE_INDEX(101, "宝迪官网", "www.budee.com", "budee_view"),
    BUDEE_PRODUCT(102, "宝迪产品中心", "product.budee.com", "budee_view"),
    BUDEE_WIKI(103, "宝迪维基", "wiki.budee.com", "budee_view"),
    BUDEE_CHEERS(104, "启声官网", "www.cheersmusic.cn", "budee_view"),
    YUELUX_O2O(110, "O2O", "o2o.yuelux.com", "yuelux_view"),
    YUELUX_BO(111, "BO", "bo.yuelux.com", "yuelux_view"),
    YUELUX_INDEX(112, "乐享生活", "www.yuelux.com", "yuelux_view"),
    YYREN_INDEX(120, "我·音乐人", "www.iyyren.com", "yyren_view"),
    YYREN_SUPPORT(121, "技术支持", "support.iyyren.com", "yyren_view"),
    YYREN_AWARD(122, "抽奖平台", "8.iyyren.com", "yyren_view"),
    YYREN_M_AWARD(123, "抽奖平台", "m.8.iyyren.com", "yyren_view"),
    YYREN_USER(124, "用户中心", "user.iyyren.com", "yyren_view"),
    YYREN_M_USER(125, "用户中心", "m.user.iyyren.com", "yyren_view"),
    YYREN_PRODUCT(126, "产品中心", "product.iyyren.com", "yyren_view"),
    YYREN_M_PRODUCT(127, "产品中心", "product.iyyren.com", "yyren_view"),
    DEFAULT(1,"默认","default", "budee_view");

    private int appId;
    private String name;
    private String url;
    private String view;

    AppType(int appId, String name, String url, String view) {
        this.appId = appId;
        this.name = name;
        this.url = url;
        this.view = view;
    }

    public static AppType getValueByAppId(int appId) {
        for (AppType app : AppType.values()) {
            if (app.getAppId() == appId) {
                return app;
            }
        }

        return null;
    }

    public static AppType getValueByURL(String url) {
        for (AppType app : AppType.values()) {
            if (app.getUrl().equals(url.trim())) {
                return app;
            }
        }

        return null;
    }
    public int getAppId() {
        return appId;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getView() {
        return view;
    }

}
