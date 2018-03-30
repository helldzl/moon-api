package com.mifan.reward.domain;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.moonframework.model.mybatis.domain.BaseEntity;

@ApiModel(description = "商品内容图集")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class GoodContentImages extends BaseEntity {

    public static final String TABLE_NAME = "good_content_images";

    public static final String GOOD_ID = "good_id";
    public static final String IMAGE_ID = "image_id";

    private static final long serialVersionUID = 2975939395100190814L;

    private Long goodId;
    private Long imageId;


}
