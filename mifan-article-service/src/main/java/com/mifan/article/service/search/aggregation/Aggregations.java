package com.mifan.article.service.search.aggregation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/8/16
 */
public class Aggregations {

    private Map<String, Aggregation> map = new HashMap<>();

    /**
     * <p>添加聚合</p>
     *
     * @param aggregation
     */
    public void add(Aggregation aggregation) {
        AbstractAggregation a = (AbstractAggregation) aggregation;
        map.put(a.getName(), a);
    }

    public Aggregation from(String name) {
        return map.get(name);
    }

    public List<Aggregation> toList() {
        return map.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public List<Aggregation> toList(long mask) {
        return map.entrySet().stream().map(Map.Entry::getValue).filter(aggregation -> (aggregation.getMask() & mask) != 0).collect(Collectors.toList());
    }

}
