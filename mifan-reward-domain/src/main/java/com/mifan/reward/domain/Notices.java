package com.mifan.reward.domain;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.moonframework.model.mybatis.domain.BaseEntity;

@ApiModel(description = "系统公告")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class Notices extends BaseEntity {

    public static final String TABLE_NAME = "notices";

    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String STATE = "state";
    public static final String DISPLAY_ORDER = "display_order";

    private static final long serialVersionUID = 5181201986680062007L;

    private String title;
    private String content;
    private Integer state;
    private Integer displayOrder;

}
