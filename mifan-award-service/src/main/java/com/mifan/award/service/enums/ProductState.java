package com.mifan.award.service.enums;

public enum ProductState {
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
    HIDING(3),
    /**
     * 审核不通过
     * */
    FAILED(9),

    /**
     * 已删除
     * */
    DELETED(0);

    ProductState(Integer state) {
        this.state = state;
    }

    private Integer state;

    public Integer getState() {
        return state;
    }
}
