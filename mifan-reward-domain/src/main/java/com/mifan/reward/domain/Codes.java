package com.mifan.reward.domain;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;

@ApiModel(description = "夺宝号码")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class Codes extends BaseEntity {

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(Codes.ID)
            .add(Codes.RECORD_ID)
            .add(Codes.PRIZE_ID)
            .add(Codes.USER_ID)
            .add(Codes.CODE)
            .build();

    public static final String TABLE_NAME = "codes";

    public static final String RECORD_ID = "record_id";
    public static final String PRIZE_ID = "prize_id";
    public static final String USER_ID = "user_id";
    public static final String CODE = "code";

    private static final long serialVersionUID = -413570890025466896L;

    private Long recordId;
    private Long prizeId;
    private Long userId;
    private Long code;

}
