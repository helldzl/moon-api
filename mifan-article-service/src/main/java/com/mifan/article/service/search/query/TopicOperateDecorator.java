package com.mifan.article.service.search.query;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.moonframework.core.support.OperateComponent;
import org.moonframework.core.support.OperateDecorator;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/27
 */
public abstract class TopicOperateDecorator extends OperateDecorator<QueryBuilder> {

    private OperateComponent<QueryBuilder> operate;

    public TopicOperateDecorator(OperateComponent<QueryBuilder> operate) {
        super(operate);
        this.operate = operate;
    }

    public OperateComponent<QueryBuilder> getOperate() {
        return operate;
    }

    /**
     * <p>Query String Query</p>
     * <a href='https://www.elastic.co/guide/en/elasticsearch/reference/2.4/query-dsl-query-string-query.html#query-dsl-query-string-query'>query-dsl-query-string-query</a>
     *
     * @param fields      fields
     * @param queryString query string
     * @param analyzer    analyzer
     * @return QueryBuilder
     */
    protected QueryBuilder queryString(String[] fields, String queryString, String analyzer) {
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(queryString);
        for (String field : fields) {
            queryStringQueryBuilder.field(field);
        }
        return queryStringQueryBuilder.analyzer(analyzer);
    }

    /**
     * @param boolQueryBuilder bool
     * @param queryField       query field
     * @param queryString      query string
     * @param analyzer         analyzer
     */
    protected void queryString(BoolQueryBuilder boolQueryBuilder, String queryField, String queryString, String analyzer) {
        if (queryField != null && queryString != null) {
            boolQueryBuilder.should(queryString(split(queryField), queryString, analyzer));
        }
    }

    /**
     * @param s string
     * @return array
     */
    protected String[] split(String s) {
        return s.split(",");
    }

    /**
     * <p>Turn to Bool Query Builder</p>
     *
     * @return Bool Query Builder
     */
    protected BoolQueryBuilder boolQueryBuilder() {
        QueryBuilder queryBuilder = super.operation();
        BoolQueryBuilder boolQueryBuilder;
        if (queryBuilder instanceof BoolQueryBuilder) {
            boolQueryBuilder = (BoolQueryBuilder) queryBuilder;
        } else {
            boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(queryBuilder);
        }
        return boolQueryBuilder;
    }
}
