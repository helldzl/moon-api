package com.mifan.reward.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;

@ApiModel(description = "图片信息")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class Images extends BaseEntity {

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(Images.ID)
            .add(Images.IMAGE)
            .add(Images.TITLE)
            .add(Images.ALT)
            .build();

    public static final String TABLE_NAME = "images";

    public static final String IMAGE = "image";
    public static final String TITLE = "title";
    public static final String ALT = "alt";

    private static final long serialVersionUID = -7764076082092225548L;

    private String image;
    private String title;
    private String alt;


}
