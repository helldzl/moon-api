package com.mifan.article.service.search.aggregation;

import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/8/16
 */
public class RangeAggregation extends AbstractAggregation {

    private double[] ranges;

    public RangeAggregation(String name, String field) {
        super(name, field);
    }

    public RangeAggregation(String name, String field, int mask) {
        super(name, field, mask);
    }

    @Override
    public RangeBuilder build() {
        RangeBuilder rangeBuilder = AggregationBuilders.range(getName()).field(getField());
        rangeBuilder.addUnboundedTo(ranges[0]);
        for (int i = 1; i < ranges.length - 1; i++) {
            rangeBuilder.addRange(ranges[i - 1], ranges[i]);
        }
        rangeBuilder.addUnboundedFrom(ranges[ranges.length - 1]);
        return rangeBuilder;
    }

    @Override
    public Object apply(List<Map<String, Object>> buckets, Boolean group) {
        return buckets.stream().filter(map -> Integer.parseInt(map.get("doc_count").toString()) != 0).collect(Collectors.toList());
    }

    public double[] getRanges() {
        return ranges;
    }

    public void setRanges(double[] ranges) {
        this.ranges = ranges;
    }

}
