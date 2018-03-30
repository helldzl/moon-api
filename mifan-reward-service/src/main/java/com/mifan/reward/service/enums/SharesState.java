package com.mifan.reward.service.enums;

public enum SharesState {
    /*待审核*/
    CREATE(1),
    /*审核通过*/
    APPROVED(2),
    /*审核未通过*/
    UNAPPROVED(3);

    SharesState(Integer code) {
        this.code = code;
    }

    private Integer code;

    public Integer getCode() {
        return code;
    }
}
