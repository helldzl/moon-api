package com.mifan.article.service.search.query;

import com.mifan.article.domain.ForumCategories;
import com.mifan.article.domain.Topics;
import com.mifan.article.util.SearchBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.moonframework.core.support.OperateComponent;
import org.moonframework.model.mybatis.criterion.QueryFieldOperator;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Include;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/27
 */
public class TopicQueryBuilder implements OperateComponent<QueryBuilder> {

    private HttpServletRequest request;
    private Set<String> excludes;
    private int mask;
    private String query;

    public TopicQueryBuilder(HttpServletRequest request, Set<String> excludes) {
        this(request, excludes, (1 << 4) - 1);
    }

    public TopicQueryBuilder(HttpServletRequest request, Set<String> excludes, int mask) {
        this.request = request;
        this.excludes = excludes;
        this.mask = mask;
    }

    @Override
    public QueryBuilder operation() {
        // convert sql criterion to elastic query builder
        QueryBuilder queryBuilder = SearchBuilder.builder(null, QueryFieldOperator.criterion(request.getParameterMap(), false), (builder, expression) -> {
            String propertyName = expression.getPropertyName();
            String value = (String) expression.getValue();
            // String value = SearchBuilder.decode((String) expression.getValue());

            // multi match query
            if ("q".equals(propertyName) || "query".equals(propertyName)) {
                // Fields can be specified with wildcards
                // Individual fields can be boosted with the caret (^) notation
                // The [title] field is n times as important as the [content] field.
                // https://www.elastic.co/guide/en/elasticsearch/reference/2.4/query-dsl-multi-match-query.html#type-cross-fields
                setQuery(value);
                return QueryBuilders
                        .multiMatchQuery(value, "items.titles.*^3", "items.contents.*")
                        .operator(MatchQueryBuilder.Operator.AND);
            }
            // not analyzed field use term query
            else if ("category".equals(propertyName)) {
                unset(1);
                return QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("categories.en.raw", value))
                        .should(QueryBuilders.termQuery("categories.cn.raw", value));
            } else if ("categoryId".equals(propertyName)) {
                unset(1);
                Restrictions.put(Include.class, new Include(new String[]{ForumCategories.NODE_PARENT}));

                // Use cache version : findOne is cached, queryForObject is not cached
                ForumCategories category = Services.findOne(ForumCategories.class, Long.valueOf(value), null);
                Restrictions.remove();
                if (category == null) {
                    return null;
                } else {
                    BoolQueryBuilder root = QueryBuilders.boolQuery();
                    do {
                        int depth = category.getDepth() - 1;
                        root.must(QueryBuilders.boolQuery()
                                .should(QueryBuilders.termQuery("level_" + depth + ".en.raw", category.getTitle()))
                                .should(QueryBuilders.termQuery("level_" + depth + ".cn.raw", category.getTitle())));
                    } while ((category = category.getParent()) != null);
                    return root;
                }
            }
            // not analyzed field use term query
            else if ("tag".equals(propertyName)) {
                return QueryBuilders.termQuery("tags.raw", value);
            }
            // not analyzed field use term query
            else if ("brand".equals(propertyName)) {
                unset(2);
                return QueryBuilders.termQuery("brand.name", value);
            }
            // term query is capable of handling numbers, booleans, dates, and text.
            else if ("from".equals(propertyName)) {
                unset(8);
                return QueryBuilders.termQuery("items.seedId", value);
            }
            // [forum]
            else if ("forum".equals(propertyName)) {
                return QueryBuilders.termQuery("forumId", value);
            }
            // [channel]
            else if ("channel".equals(propertyName) || "channelId".equals(propertyName)) {
                // ignore this case
                return null;
            }
            // [navigation]
            else if ("navigation".equals(propertyName) || "navigationId".equals(propertyName)) {
                // ignore this case
                return null;
            }
            return QueryBuilders.termQuery(SearchBuilder.replace(propertyName), value);
        });

        // wrap to a bool query
        BoolQueryBuilder boolQueryBuilder;
        if (queryBuilder instanceof BoolQueryBuilder) {
            boolQueryBuilder = (BoolQueryBuilder) queryBuilder;
        } else {
            boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(queryBuilder);
        }

        // excludes unnecessary forum if not specified forum IDs
        if (request.getParameter("filter[forumId]") == null
                && request.getParameter("filter[forum]") == null) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("forumId", Topics.ForumType.BRAND.getIndex()));
        }

        // excludes unnecessary topic IDs
        if (!CollectionUtils.isEmpty(excludes)) {
            boolQueryBuilder.mustNot(QueryBuilders.idsQuery().ids(excludes));
        }

        return boolQueryBuilder;
    }

    private void unset(int mask) {
        this.mask &= (this.mask - mask);
    }

    public int getMask() {
        return mask;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
