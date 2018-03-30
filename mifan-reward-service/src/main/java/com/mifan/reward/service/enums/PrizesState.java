package com.mifan.reward.service.enums;

public enum PrizesState {
    /**
     * 创建完成
     * */
    CREATE(1),
    /**
     * 集资中
     * */
    PRIZING(1),
    /**
     * 等待揭晓中(以后会等待时时彩结果~~)
     * */
    COMPUTING(3),
    /**
     * 已揭晓
     * */
    COMPLETE(4);

    PrizesState(Integer state) {
        this.state = state;
    }

    private Integer state;

    public Integer getState() {
        return state;
    }
}
