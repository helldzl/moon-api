package com.mifan.reward.domain;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.moonframework.model.mybatis.domain.BaseEntity;

@ApiModel(description = "商品描述图集")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class GoodHeadImages extends BaseEntity {

    public static final String TABLE_NAME = "good_head_images";

    public static final String GOOD_ID = "good_id";
    public static final String IMAGE_ID = "image_id";

    private static final long serialVersionUID = 1488971297956481533L;

    private Long goodId;
    private Long imageId;


}
