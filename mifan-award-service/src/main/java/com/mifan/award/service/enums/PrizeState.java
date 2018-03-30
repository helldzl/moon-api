package com.mifan.award.service.enums;

public enum PrizeState {
    /**
     * 创建完成
     * */
    CREATE(1),
    /**
     * 集资中
     * */
    PRIZING(2),
    /**
     * 等待揭晓中(以后会等待时时彩结果~~)
     * */
    COMPUTING(3),
    /**
     * 已揭晓
     * */
    COMPLETE(4),

    /**
     * 已删除
     * */
    DELETED(0);

    PrizeState(Integer state) {
        this.state = state;
    }

    private Integer state;

    public Integer getState() {
        return state;
    }
}
