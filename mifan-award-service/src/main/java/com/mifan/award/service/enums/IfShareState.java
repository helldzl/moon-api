package com.mifan.award.service.enums;

public enum IfShareState {
     /*未晒单*/
    UNCREATE(0),
    /*已经晒单*/
    CREATE(1);

    IfShareState(Integer code) {
        this.code = code;
    }

    private Integer code;

    public Integer getCode() {
        return code;
    }
}
