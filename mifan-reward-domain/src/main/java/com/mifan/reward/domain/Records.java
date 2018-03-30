package com.mifan.reward.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mifan.reward.domain.support.RewardUsers;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;

import java.util.Date;
import java.util.List;

@ApiModel(description = "参与记录")
@Getter
@Setter
@NoArgsConstructor
public class Records extends BaseEntity {

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(Records.ID)
            .add(Records.PRIZE_ID)
            .add(Records.USER_ID)
            .add(Records.BUY_TIMES)
            .add(Records.IP)
            .build();
    public static final String TABLE_NAME = "records";

    public static final String PRIZE_ID = "prize_id";
    public static final String USER_ID = "user_id";
    public static final String BUY_TIMES = "buy_times";
    public static final String IP = "ip";

    private static final long serialVersionUID = 411104152730729687L;

    private Long prizeId;
    private Long userId;
    private Integer buyTimes;
    private String ip;

    private Date created;
    private Date modified;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<Long> codeInfo;

    private RewardUsers userInfo;
    private Prizes prizeInfo;



}
