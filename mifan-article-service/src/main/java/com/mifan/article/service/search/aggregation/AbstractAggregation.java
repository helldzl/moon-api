package com.mifan.article.service.search.aggregation;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.moonframework.elasticsearch.AbstractSearchEngine.FIELD_BUCKETS;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/8/16
 */
public abstract class AbstractAggregation implements Aggregation, BiFunction<List<Map<String, Object>>, Boolean, Object> {

    /**
     * 聚合名称
     */
    private String name;

    /**
     * 聚合字段
     */
    private String field;

    /**
     *
     */
    private int mask;

    public AbstractAggregation(String name, String field) {
        this.name = name;
        this.field = field;
    }

    public AbstractAggregation(String name, String field, int mask) {
        this.name = name;
        this.field = field;
        this.mask = mask;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void accept(Map<String, Object> aggregations, Boolean group) {
        if (aggregations == null) {
            return;
        }

        Map<String, Object> bucketsMap = (Map<String, Object>) aggregations.get(name);

        if (bucketsMap == null) {
            return;
        }

        List<Map<String, Object>> buckets = (List<Map<String, Object>>) bucketsMap.get(FIELD_BUCKETS);

        if (buckets == null) {
            return;
        }

        Object obj = apply(buckets, group);

        if (obj != null) {
            bucketsMap.put(FIELD_BUCKETS, obj);
        }
    }

    /**
     * <p>将buckets转换为其他格式, 或进行重组</p>
     * <p>hook method</p>
     *
     * @param buckets buckets
     * @param group   group: 是否按首字母分组
     * @return obj
     */
    @Override
    public Object apply(List<Map<String, Object>> buckets, Boolean group) {
        return null;
    }

    @Override
    public int getMask() {
        return mask;
    }

    // get and set method

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
