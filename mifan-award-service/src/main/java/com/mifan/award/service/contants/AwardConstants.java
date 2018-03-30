package com.mifan.award.service.contants;

import java.io.Serializable;

public class AwardConstants implements Serializable {

    private static final long serialVersionUID = -3548246668258328270L;
    /**
     * CODE REDIS 前缀
     */
    public static final String CODE_REDIS_PREFIX = "award:prize:";
    /**
     * CODE 数目 前缀
     */
    public static final long CODE_NUM_PREFIX = 10000001L;

    /**
     * 计算抽奖结果选取的记录数目
     */
    public static final int AWARD_RESULT_RECORD_SIZE = 10;

    /**
     * 获取时时彩抽奖码的API地址(http)
     */
    public static final String CODE_HTTP_API = "http://f.apiplus.net/cqssc-1.json";

    private AwardConstants() {
    }
}
