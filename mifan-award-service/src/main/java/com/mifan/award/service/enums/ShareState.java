package com.mifan.award.service.enums;

public enum ShareState {
    /*待审核*/
    CREATE(1),
    /*审核通过*/
    APPROVED(2),
    /*审核未通过*/
    UNAPPROVED(3),
    /*已删除*/
    DELETED(0);

    ShareState(Integer code) {
        this.code = code;
    }

    private Integer code;

    public Integer getCode() {
        return code;
    }
}
