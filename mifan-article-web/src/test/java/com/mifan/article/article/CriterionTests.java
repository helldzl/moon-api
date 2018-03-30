package com.mifan.article.article;

import com.mifan.article.util.SearchBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.moonframework.elasticsearch.aggregation.Term;
import org.moonframework.model.mybatis.criterion.QueryFieldOperator;

import java.util.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/21
 */
public class CriterionTests {

    public static void a() {
        Map<String, String[]> params = new HashMap<>();
        params.put("filter[q]", new String[]{"你好啊"});
        params.put("filter[categories]", new String[]{"吉他", "贝斯"});
        params.put("filter[brand]", new String[]{"雅马哈"});
        params.put("filter[price:range]", new String[]{"[2000.00, 4000.00]"});
        params.put("filter[mifan.mm.Users.MyAge:range]", new String[]{"[0,999]"});
        params.put("filter[brands]", new String[]{"2box", "Yamaha", "EV"});

        System.out.println();

        QueryBuilder boolQuery = SearchBuilder.builder(null, QueryFieldOperator.criterion(params, false), (builder, expression) -> {
            String propertyName = expression.getPropertyName();
            // match query
            if ("q".equals(propertyName)) {
                return QueryBuilders.multiMatchQuery(expression.getValue(), "items.title.en", "items.title.cn", "items.content.en", "items.content.cn");
            }
            // not analyzed field use term query
            else if ("categories".equals(propertyName)) {
//                builder.should(QueryBuilders.termQuery("categories.en.raw", expression.getValue()));
//                builder.should(QueryBuilders.termQuery("categories.cn.raw", expression.getValue()));
                return QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("categories.en.raw", expression.getValue()))
                        .should(QueryBuilders.termQuery("categories.cn.raw", expression.getValue()));
            }
            // not analyzed field use term query
            else if ("tags".equals(propertyName)) {
                return QueryBuilders.termQuery("tags.raw", expression.getValue());
            }
            //
            else if ("brands".equals(propertyName)) {
                return QueryBuilders.termQuery("brands", expression.getValue());
            }
            // term query is capable of handling numbers, booleans, dates, and text.
            else if ("from".equals(propertyName)) {
                return QueryBuilders.termQuery("items.seedId", expression.getValue());
            }

            // return null;
            return QueryBuilders.termQuery(expression.getPropertyName(), expression.getValue());
        });
        System.out.println(boolQuery);
    }

    public static void b() {
        List<FieldSortBuilder> price = SearchBuilder.sort(new String[]{"price", "colors.ffffff"}, a -> {
            for (int i = 0; i < a.length; i++) {
                String v = a[i];
                if (v.equals("price")) {
                    a[i] = "items.price";
                } else if (v.startsWith("colors.")) {
                    a[i] = "images.distance." + v.substring(v.lastIndexOf(".") + 1);
                }
            }
            return a;
        });
        for (FieldSortBuilder fieldSortBuilder : price) {
            System.out.println(fieldSortBuilder.toString());
        }
    }

    public static void c(String[] names, String[] fields, int size) {
        List<Term> terms = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String field = fields[i];
            Term term;
            if (field.contains("-")) {
                Iterator<String> it = Arrays.asList(field.split("-")).iterator();
                String f = it.next();
                term = new Term(name + "," + f, f);
                Term last = term;
                while (it.hasNext()) {
                    f = it.next();
                    Term current = new Term(name + "," + f, f);
                    last.add(current);
                    last = current;
                }
            } else {
                term = new Term(name + "," + field, field);
            }
            term.setSize(size);
            terms.add(term);
        }

        System.out.println();
    }

    private static CharSequence[] fix(String[] array) {
        for (int i = 0; i < array.length; i++) {
            String s = array[i];
            if (s.contains("-")) {
                array[i] = String.join("-", fix(s.split("-")));
            } else
                check(array, i);
        }
        return array;
    }

    private static void check(String[] array, int index) {
        String s = array[index];
        if (s.matches("(level_[\\d]+|category)\\.raw$")) {
            StringBuilder sb = new StringBuilder(s);
            array[index] = sb.insert(sb.lastIndexOf("."), ".cn").toString();
        }
    }


    public static void main(String[] args) {
//        String[] array= new String[]{"1","2","3"};
//        String s = String.join(".", Arrays.asList(Arrays.copyOfRange(array, 0, array.length - 1)));
//        System.out.println(s);
        String[] strings = {"category.raw", "level.0.raw-level.1.raw-level.2.raw"};

        Map<String, String[]> params = new HashMap<>();
        params.put("aggregation[category]", strings);
        //fix(strings);
        //fix(strings1);

        //c(strings, strings1, 1);

        agg(params);

    }

    private static final String REGEX_AGGREGATION = "aggregation\\[[A-Za-z0-9_]+\\]";

    public static void agg(Map<String, String[]> params) {
        List<Term> list = params.entrySet()
                .stream()
                .filter(entry -> entry.getKey().matches(REGEX_AGGREGATION))
                .map(CriterionTests::function)
                .reduce(new ArrayList<>(), (left, right) -> {
                    left.addAll(right);
                    return left;
                });

        System.out.println(list);
    }

    private static List<Term> function(Map.Entry<String, String[]> entry) {
        Collections.emptyList();

        String name = replace(entry.getKey());
        List<Term> terms = new ArrayList<>();
        for (String value : entry.getValue()) {
            Term head;
            if (value.contains("-")) {
                Iterator<String> it = Arrays.asList(value.split("-")).iterator();
                String next = it.next();
                head = new Term(name + "," + next, next);
                Term tail = head;
                while (it.hasNext()) {
                    next = it.next();
                    Term term = new Term(name + "," + next, next);
                    tail.add(term);
                    tail = term;
                }
            } else {
                head = new Term(name + "," + value, value);
            }
            // term.setSize(size);
            terms.add(head);
        }
        return terms;
    }

    private static String replace(String key) {
        return key.replaceAll(".*\\[|\\]", "");
    }

}
