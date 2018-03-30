package com.mifan.reward.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mifan.reward.domain.support.RewardUsers;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.moonframework.model.mybatis.domain.BaseEntity;
import java.util.Date;
import java.util.List;

@ApiModel(description = "夺宝详细信息")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class PrizeInfo extends BaseEntity {

    public static final String TABLE_NAME = "prize_info";

    public static final String LOTTERY_PERIOD = "lottery_period";
    public static final String LOTTERY_CODE = "lottery_code";
    public static final String LOTTERY_TIME = "lottery_time";
    public static final String RECORD_TIME_TOTAL = "record_time_total";
    public static final String LUCK_CODE = "luck_code";
    public static final String LUCK_USER_ID = "luck_user_id";
    public static final String FINISH_TIME = "finish_time";

    private static final long serialVersionUID = 4335236951350733596L;

    private Long lotteryPeriod;
    private Integer lotteryCode;
    private Date lotteryTime;
    private Long recordTimeTotal;
    private Long luckCode;
    private Long luckUserId;
    private Date finishTime;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<Records> resultRecordInfo;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private RewardUsers luckUserInfo;

}
