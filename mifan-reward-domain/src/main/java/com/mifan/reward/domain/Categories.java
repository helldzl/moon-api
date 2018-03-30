package com.mifan.reward.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author DXZ
 * @version 1.0
 * @since 2017-08-31
 */
@ApiModel(description = "商品类别")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Categories extends BaseEntity {

    public static final String TABLE_NAME = "categories";

    public static final String NAME = "name";
    public static final String CSS_CLASS = "css_class";
    public static final String FILENAME = "filename";
    public static final String DISPLAY_ORDER = "display_order";

    private static final long serialVersionUID = -2097111201795672047L;

    @ApiModelProperty(value = "类别名称")
    private String name;
    @ApiModelProperty(value = "类别css")
    private String cssClass;
    @ApiModelProperty(value = "类别图片")
    private String filename;
    @ApiModelProperty(value = "显示顺序")
    private Integer displayOrder;

}
