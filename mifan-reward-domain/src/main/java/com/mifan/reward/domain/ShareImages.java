package com.mifan.reward.domain;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.moonframework.model.mybatis.domain.BaseEntity;

@ApiModel(description = "晒单图集")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class ShareImages extends BaseEntity {

    public static final String TABLE_NAME = "share_images";

    public static final String SHARE_ID = "share_id";
    public static final String IMAGE_ID = "image_id";

    private static final long serialVersionUID = 4824167743832119291L;

    private Long shareId;
    private Long imageId;

}
