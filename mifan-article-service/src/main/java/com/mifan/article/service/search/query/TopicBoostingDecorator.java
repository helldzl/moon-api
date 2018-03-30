package com.mifan.article.service.search.query;

import com.mifan.article.domain.ElasticQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.moonframework.core.support.OperateComponent;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/27
 */
public class TopicBoostingDecorator extends TopicOperateDecorator {

    private ElasticQueryBuilder elasticQueryBuilder;

    public TopicBoostingDecorator(OperateComponent<QueryBuilder> operate, ElasticQueryBuilder elasticQueryBuilder) {
        super(operate);
        this.elasticQueryBuilder = elasticQueryBuilder;
    }

    @Override
    public QueryBuilder operation() {
        return QueryBuilders.boostingQuery()
                .positive(super.operation())
                .negative(negative())
                .negativeBoost(elasticQueryBuilder.getNegativeBoost().floatValue());
    }

    private QueryBuilder negative() {
        if (elasticQueryBuilder.getNegativeQueryStringEn() == null && elasticQueryBuilder.getNegativeQueryStringCn() == null) {
            return QueryBuilders.matchAllQuery();
        }

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryString(queryBuilder, elasticQueryBuilder.getQueryFieldsEn(), elasticQueryBuilder.getNegativeQueryStringEn(), "en");
        queryString(queryBuilder, elasticQueryBuilder.getQueryFieldsCn(), elasticQueryBuilder.getNegativeQueryStringCn(), "cn");
        return queryBuilder;
    }
}
