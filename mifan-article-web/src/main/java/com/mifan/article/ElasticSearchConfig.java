package com.mifan.article;

import com.mifan.article.domain.Seeds;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.support.Brands;
import com.mifan.article.service.BrandsService;
import com.mifan.article.service.TopicsService;
import com.mifan.article.service.search.PagedSearchEngine;
import com.mifan.article.service.search.SearchDocument;
import com.mifan.article.service.search.aggregation.Aggregations;
import com.mifan.article.service.search.aggregation.RangeAggregation;
import com.mifan.article.service.search.aggregation.TermsAggregation;
import com.mifan.article.service.search.builder.TopicsBuilderConsumer;
import com.mifan.article.util.PinyinUtils;
import org.elasticsearch.client.Client;
import org.moonframework.elasticsearch.autoconfigure.ElasticsearchAutoConfiguration;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/20
 */
@Configuration
@AutoConfigureAfter(ElasticsearchAutoConfiguration.class)
public class ElasticSearchConfig {

    @Bean
    public PagedSearchEngine pagedSearchEngine(Client client, SearchDocument searchDocument) {
        return new PagedSearchEngine(client, searchDocument);
    }

    @Bean
    public SearchDocument searchDocument(TopicsService topicsService) {
        SearchDocument.IndexBuilder<Topics> topicsIndexBuilder = new SearchDocument.IndexBuilder<>();
        topicsIndexBuilder.put(SearchDocument.IndexType.LIST, new TopicsBuilderConsumer(topicsService));

        SearchDocument searchDocument = new SearchDocument();
        searchDocument.put(Topics.class, topicsIndexBuilder);

        return searchDocument;
    }

    @Bean(name = "topicAggregations")
    public Aggregations topicAggregations(BrandsService brandsService) {
        Aggregations aggregations = new Aggregations();

        // 分类
        TermsAggregation category = new TermsAggregation("category", "level_0.cn.raw", 1);
        category.setTermGroup(PinyinUtils::group);

        // 品牌
        TermsAggregation brand = new TermsAggregation("brand", "brand.name", 1 << 1);
        brand.setTermGroup(PinyinUtils::group);
        brand.setConsumer(list -> {
            // 关联
            list.forEach(map -> map.putAll(Brands.toMap(brandsService.findBrand((String) map.get("key")))));
            // 排序
            list.sort(Comparator.comparing(o -> ((String) o.get("key")).toLowerCase()));
        });

        // 价格
        RangeAggregation price = new RangeAggregation("price", "items.price", 1 << 2);
        price.setRanges(new double[]{100D, 200D, 300D, 500D, 1000D, 2000D, 5000D, 10000D, 50000D});

        // 来源
        TermsAggregation from = new TermsAggregation("from", "items.seedId", 1 << 3);
        from.setConsumer(list -> list.forEach(map -> {
            // read from ehcache
            Long key = Long.valueOf((String) map.get("key"));
            Seeds.putSource(map, Services.findOne(Seeds.class, key));
        }));

        // add aggregations
        aggregations.add(brand);
        aggregations.add(category);
        aggregations.add(price);
        aggregations.add(from);

        return aggregations;
    }

}
