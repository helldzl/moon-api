package com.mifan.article.service.search.aggregation;

import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/8/16
 */
public class TermsAggregation extends AbstractAggregation {

    /**
     * term分组策略
     */
    private TermGroup termGroup;

    /**
     * 消费buckets
     */
    private Consumer<List<Map<String, Object>>> consumer;

    public TermsAggregation(String name, String field) {
        super(name, field);
    }

    public TermsAggregation(String name, String field, int mask) {
        super(name, field, mask);
    }

    @Override
    public TermsBuilder build() {
        return AggregationBuilders.terms(getName()).field(getField());
    }

    @Override
    public Object apply(List<Map<String, Object>> buckets, Boolean group) {
        if (consumer != null) {
            consumer.accept(buckets);
        }

        if (group == null ? termGroup != null : group) {
            TreeMap<? extends String, List<Map<String, Object>>> result = buckets
                    .stream()
                    .collect(Collectors.groupingBy(map -> termGroup.apply((String) map.get("key")), TreeMap::new, Collectors.toList()));
            result.remove("");
            return result;
        }
        return null;
    }

    public TermGroup getTermGroup() {
        return termGroup;
    }

    public void setTermGroup(TermGroup termGroup) {
        this.termGroup = termGroup;
    }

    public Consumer<List<Map<String, Object>>> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<List<Map<String, Object>>> consumer) {
        this.consumer = consumer;
    }

}
