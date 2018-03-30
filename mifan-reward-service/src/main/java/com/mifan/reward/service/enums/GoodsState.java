package com.mifan.reward.service.enums;

public enum GoodsState {
    /**
     * 创建完成
     * */
    CREATE(1),
    /**
     * 首页显示
     * */
    SHOWING(2),
    /**
     * 不显示
     * */
    RECOMMAND(3);

    GoodsState(Integer state) {
        this.state = state;
    }

    private Integer state;

    public Integer getState() {
        return state;
    }
}
