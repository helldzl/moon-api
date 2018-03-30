package com.mifan.article.service.search.query;

import com.mifan.article.domain.ElasticFunctionScore;
import com.mifan.article.domain.ElasticQueryBuilder;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.fieldvaluefactor.FieldValueFactorFunctionBuilder;
import org.moonframework.core.support.OperateComponent;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/27
 */
public class TopicFunctionScoreDecorator extends TopicOperateDecorator {

    private String scoreMode;
    private String boostMode;
    private List<ElasticFunctionScore> functionScoreList;
    private ElasticQueryBuilder elasticQueryBuilder;

    public TopicFunctionScoreDecorator(OperateComponent<QueryBuilder> operate, ElasticQueryBuilder elasticQueryBuilder) {
        super(operate);
        this.scoreMode = elasticQueryBuilder.getFunctionScoreMode().toLowerCase();
        this.boostMode = elasticQueryBuilder.getFunctionBoostMode().toLowerCase();
        this.functionScoreList = elasticQueryBuilder.getFunctionScoreList();
        this.elasticQueryBuilder = elasticQueryBuilder;
    }

    @Override
    public QueryBuilder operation() {
        if (CollectionUtils.isEmpty(functionScoreList)) {
            throw new IllegalArgumentException("except at least one global function, but find none. please see table [elastic_function_score]");
        }

        FunctionScoreQueryBuilder functionScoreQueryBuilder = functionScoreQuery()
                .scoreMode(scoreMode)
                .boostMode(boostMode);

        for (ElasticFunctionScore functionScore : functionScoreList) {
            FieldValueFactorFunctionBuilder functionBuilder = new FieldValueFactorFunctionBuilder(functionScore.getNumericField())
                    .factor(functionScore.getFunctionFactor().floatValue()) // Optional factor to multiply the field value with, defaults to 1.
                    .missing(functionScore.getFunctionMissing())
                    .modifier(FieldValueFactorFunction.Modifier.valueOf(functionScore.getFunctionModifier()));

            functionBuilder.setWeight(functionScore.getWeight().floatValue());
            String filters = functionScore.getFilters();
            if (filters == null) {
                functionScoreQueryBuilder.add(functionBuilder);
            } else {
                BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery();
                for (String filter : filters.split(",")) {
                    String[] split = filter.split(":");
                    filterBuilder.must(QueryBuilders.termQuery(split[0], split[1]));
                }
                functionScoreQueryBuilder.add(filterBuilder, functionBuilder);
            }
        }

        return functionScoreQueryBuilder;
    }

    private FunctionScoreQueryBuilder functionScoreQuery() {
        BoolQueryBuilder boolQueryBuilder = boolQueryBuilder().filter(filter());

        if ((elasticQueryBuilder.getQueryFieldsEn() != null && elasticQueryBuilder.getPositiveQueryStringEn() != null)
                || (elasticQueryBuilder.getQueryFieldsCn() != null && elasticQueryBuilder.getPositiveQueryStringCn() != null)) {
            BoolQueryBuilder builder = QueryBuilders.boolQuery();
            queryString(builder, elasticQueryBuilder.getQueryFieldsEn(), elasticQueryBuilder.getPositiveQueryStringEn(), "en");
            queryString(builder, elasticQueryBuilder.getQueryFieldsCn(), elasticQueryBuilder.getPositiveQueryStringCn(), "cn");
            boolQueryBuilder.must(builder);
        }

        return QueryBuilders.functionScoreQuery(boolQueryBuilder);
    }

    private QueryBuilder filter() {
        if (elasticQueryBuilder.getFilterCategoriesEn() == null
                && elasticQueryBuilder.getFilterCategoriesCn() == null) {
            return QueryBuilders.matchAllQuery();
        }

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (elasticQueryBuilder.getFilterCategoriesEn() != null) {
            queryBuilder.should(QueryBuilders.termsQuery("categories.en.raw", split(elasticQueryBuilder.getFilterCategoriesEn())));
        }
        if (elasticQueryBuilder.getFilterCategoriesCn() != null) {
            queryBuilder.should(QueryBuilders.termsQuery("categories.cn.raw", split(elasticQueryBuilder.getFilterCategoriesCn())));
        }

        return queryBuilder;
    }
}
