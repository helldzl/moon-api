package com.mifan.reward.service.enums;

public enum EntityState {

    /**
     * 创建完成
     * */
    ENABLED(1),

    /**
     * 已删除
     * */
    DISABLED(0);

    EntityState(Integer state) {
        this.state = state;
    }

    private Integer state;

    public Integer getState() {
        return state;
    }
}
