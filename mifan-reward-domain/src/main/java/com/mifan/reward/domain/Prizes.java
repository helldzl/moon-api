package com.mifan.reward.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mifan.reward.domain.support.RewardUsers;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.moonframework.model.mybatis.domain.BaseEntity;

import java.util.List;

@ApiModel(description = "夺宝信息")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class Prizes extends BaseEntity {

    public static final String TABLE_NAME = "prizes";

    public static final String GOOD_ID = "good_id";
    public static final String CATEGORY_ID = "category_id";
    public static final String PERIOD = "period";
    public static final String STATE = "state";

    private static final long serialVersionUID = 1141740963826559300L;

    private Long goodId;
    private Long categoryId;
    private String period;
    private Integer state;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Integer existingTimes;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private PrizeInfo prizeInfo;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Goods goodInfo;

}
