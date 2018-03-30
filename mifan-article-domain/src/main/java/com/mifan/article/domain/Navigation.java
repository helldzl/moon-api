package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-25
 */
public class Navigation extends BaseEntity {

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(Navigation.ID)
            .add(Navigation.PARENT_ID)
            .add(Navigation.ELASTIC_QUERY_BUILDER_ID)
            .add(Navigation.DISPLAY_ORDER)
            .add(Navigation.TITLE)
            .add(Navigation.IMAGE)
            .build();

    public static final String TABLE_NAME = "navigation";

    public static final String PARENT_ID = "parent_id";
    public static final String ELASTIC_QUERY_BUILDER_ID = "elastic_query_builder_id";
    public static final String DISPLAY_ORDER = "display_order";
    public static final String TITLE = "title";
    public static final String IMAGE = "image";

    private static final long serialVersionUID = 7245827215181917042L;

    private Long parentId;
    private Long elasticQueryBuilderId;
    private Integer displayOrder;
    private String title;
    private String image;

    //

    private List<Navigation> children = new ArrayList<>();
    private ElasticQueryBuilder elasticQueryBuilder;

    /**
     * @param list an ordered list (order by parentId and display order ASC)
     * @return result
     */
    public static List<Navigation> navigate(List<Navigation> list) {
        Map<Long, Navigation> map = list.stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        List<Navigation> result = new ArrayList<>();
        for (Navigation navigation : list) {
            if (navigation.isRoot()) {
                result.add(navigation);
            } else {
                Navigation parent = map.get(navigation.getParentId());
                if (parent != null) {
                    parent.add(navigation);
                }
            }
        }
        return result;
    }

    public Navigation() {
    }

    public Navigation(Long id) {
        super(id);
    }

    public boolean isRoot() {
        return parentId != null && parentId == 0L;
    }

    public boolean add(Navigation navigation) {
        return children.add(navigation);
    }

    /**
     * @return PARENT ID
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId PARENT ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return
     */
    public Long getElasticQueryBuilderId() {
        return elasticQueryBuilderId;
    }

    /**
     * @param elasticQueryBuilderId
     */
    public void setElasticQueryBuilderId(Long elasticQueryBuilderId) {
        this.elasticQueryBuilderId = elasticQueryBuilderId;
    }

    /**
     * @return display order
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /**
     * @param displayOrder display order
     */
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * @return 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Navigation> getChildren() {
        return children;
    }

    public void setChildren(List<Navigation> children) {
        this.children = children;
    }

    public ElasticQueryBuilder getElasticQueryBuilder() {
        return elasticQueryBuilder;
    }

    public void setElasticQueryBuilder(ElasticQueryBuilder elasticQueryBuilder) {
        this.elasticQueryBuilder = elasticQueryBuilder;
    }
}
