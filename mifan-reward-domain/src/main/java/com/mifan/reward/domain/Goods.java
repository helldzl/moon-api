package com.mifan.reward.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.moonframework.model.mybatis.annotation.OneToMany;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;

import java.util.List;

@ApiModel(description = "商品信息")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class Goods extends BaseEntity {

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(Goods.ID)
            .add(Goods.CATEGORY_ID)
            .add(Goods.TITLE)
            .add(Goods.DESC)
            .add(Goods.PRICE)
            .add(Goods.BUY_UNIT)
            .add(Goods.BUY_TIMES)
            .add(Goods.STATE)
            .build();
    public static final String RELATIONSHIPS_HEAD_IMAGES = "headImages";
    public static final String RELATIONSHIPS_CONTENT_IMAGES = "contentImages";

    public static final String TABLE_NAME = "goods";

    public static final String CATEGORY_ID = "category_id";
    public static final String TITLE = "title";
    public static final String DESC = "desc";
    public static final String PRICE = "price";
    public static final String BUY_UNIT = "buy_unit";
    public static final String BUY_TIMES = "buy_times";
    public static final String STATE = "state";

    private static final long serialVersionUID = 193242320423701289L;

    @OneToMany(value = RELATIONSHIPS_HEAD_IMAGES,
            targetEntity = GoodHeadImages.class,
            mappedBy = GoodHeadImages.GOOD_ID,
            mappedTo = GoodHeadImages.IMAGE_ID)
    @OneToMany(value = RELATIONSHIPS_CONTENT_IMAGES,
            targetEntity = GoodContentImages.class,
            mappedBy = GoodContentImages.GOOD_ID,
            mappedTo = GoodContentImages.IMAGE_ID)
    @Accessors(chain = false)
    private Long id;

    private Long categoryId;
    private String title;
    private String desc;
    private Integer price;
    private Integer buyUnit;
    private Integer buyTimes;
    private Integer state;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<Images> headImages;                // 图片
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<Images> contentImages;                // 图片
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Prizes prizeInfo;


}
