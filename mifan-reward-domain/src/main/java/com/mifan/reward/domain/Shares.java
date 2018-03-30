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

@ApiModel(description = "晒单信息")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class Shares extends BaseEntity {

    public static final String TABLE_NAME = "shares";

    public static final String PRIZE_ID = "prize_id";
    public static final String GOOD_ID = "good_id";
    public static final String USER_ID = "user_id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String STATE = "state";

    private static final long serialVersionUID = -1790117450211578821L;

    private Long prizeId;
    private Long goodId;
    private Long userId;
    private String title;
    private String content;
    private Integer state;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<Images> shareImages;


    private Prizes prizeInfo;
    private RewardUsers userInfo;

}
