package com.mifan.article.service.search.aggregation;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.moonframework.core.support.Builder;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/8/16
 */
public interface Aggregation extends BiConsumer<Map<String, Object>, Boolean>, Builder<AbstractAggregationBuilder> {

    /**
     * <p>计算聚合数据之后, 消费聚合对象, 进行额外处理</p>
     *
     * @param aggregations aggregations
     */
    @Override
    void accept(Map<String, Object> aggregations, Boolean group);

    /**
     * <p>计算聚合数据之前, 构建聚合建造器</p>
     *
     * @return builder
     */
    @Override
    AbstractAggregationBuilder build();

    /**
     * @return mask
     */
    int getMask();

}
