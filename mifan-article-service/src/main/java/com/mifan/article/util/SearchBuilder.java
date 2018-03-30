package com.mifan.article.util;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.moonframework.model.mybatis.criterion.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/21
 */
public class SearchBuilder {

    public static String replace(String s) {
        if (s.contains("-")) {
            String[] array = s.split("-");
            for (int i = 0; i < array.length; i++) {
                array[i] = replace(array[i], ".cn");
            }
            return String.join("-", (CharSequence[]) array);
        } else {
            return replace(s, ".cn");
        }
    }

    public static String decode(String s) {
        if (s == null) {
            return null;
        }
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static QueryBuilder builder(QueryBuilder parent, Criterion criterion, BiFunction<QueryBuilder, SimpleExpression, QueryBuilder> function) {
        if (criterion instanceof Junction) {
            BoolQueryBuilder builder = QueryBuilders.boolQuery();
            Iterable<Criterion> conditions = ((Junction) criterion).conditions();
            if (criterion instanceof Conjunction) {
                builder(builder, conditions, function, builder::must);
            } else {
                builder(builder, conditions, function, builder::should);
            }
            return builder;
        } else if (criterion instanceof SimpleExpression) {
            SimpleExpression expression = (SimpleExpression) criterion;
            String op = expression.getOp();
            Object value = expression.getValue();
            if (">".equals(op)) {
                return QueryBuilders.rangeQuery(expression.getPropertyName()).gt(value);
            } else if (">=".equals(op)) {
                return QueryBuilders.rangeQuery(expression.getPropertyName()).gte(value);
            } else if ("<".equals(op)) {
                return QueryBuilders.rangeQuery(expression.getPropertyName()).lt(value);
            } else if ("<=".equals(op)) {
                return QueryBuilders.rangeQuery(expression.getPropertyName()).lte(value);
            } else if ("<>".equals(op)) {
                return QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery(expression.getPropertyName(), value));
            }else if(" LIKE ".equals(op)) {
                return QueryBuilders.wildcardQuery("brand.name", "*" + value + "*");
            }else {
                return function.apply(parent, expression);
            }
        } else if (criterion instanceof BetweenExpression) {
            BetweenExpression expression = (BetweenExpression) criterion;
            String from = expression.getLo().toString();
            String to = expression.getHi().toString();
            if ("*".equals(from)) {
                return QueryBuilders.rangeQuery(expression.getPropertyName()).lte(Double.parseDouble(to));
            } else if ("*".equals(to)) {
                return QueryBuilders.rangeQuery(expression.getPropertyName()).gt(Double.parseDouble(from));
            } else {
                return QueryBuilders.rangeQuery(expression.getPropertyName()).gt(from).lte(to);
            }
        } else if (criterion instanceof LogicalExpression) {
            LogicalExpression expression = (LogicalExpression) criterion;
            BoolQueryBuilder builder = QueryBuilders.boolQuery();
            if ("and".equalsIgnoreCase(expression.getOp())) {
                builder(parent, Arrays.asList(expression.getLhs(), expression.getRhs()), function, builder::must);
            } else {
                builder(parent, Arrays.asList(expression.getLhs(), expression.getRhs()), function, builder::should);
            }
            return builder;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static List<FieldSortBuilder> sort(String[] array, Function<String[], String[]> function) {
        if (array == null) {
            return Collections.emptyList();
        }

        if (function != null) {
            array = function.apply(array);
        }

        List<FieldSortBuilder> list = new ArrayList<>();
        for (String value : array) {
            if (value.startsWith("-")) {
                list.add(SortBuilders.fieldSort(value.substring(1)).order(SortOrder.DESC));
            } else {
                list.add(SortBuilders.fieldSort(value).order(SortOrder.ASC));
            }
        }
        return list;
    }

    private static void builder(QueryBuilder parent, Iterable<Criterion> conditions, BiFunction<QueryBuilder, SimpleExpression, QueryBuilder> function, Consumer<QueryBuilder> consumer) {
        conditions.forEach(criterion -> {
            QueryBuilder builder = builder(parent, criterion, function);
            if (builder != null) {
                consumer.accept(builder);
            }
        });
    }

    private static String replace(String s, String insert) {
        if (s.matches("(level_[\\d]+|categories)\\.raw$")) {
            StringBuilder sb = new StringBuilder(s);
            return sb.insert(sb.lastIndexOf("."), insert).toString();
        }
        return s;
    }

}
