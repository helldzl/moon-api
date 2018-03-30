package com.mifan.article.es;

import com.mifan.article.domain.ElasticFunctionScore;
import com.mifan.article.domain.ElasticQueryBuilder;
import com.mifan.article.service.search.query.TopicBoostingDecorator;
import com.mifan.article.service.search.query.TopicChannelDecorator;
import com.mifan.article.service.search.query.TopicFunctionScoreDecorator;
import com.mifan.article.service.search.query.TopicQueryBuilder;
import com.mifan.article.util.SearchBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/26
 */
public class ComponentTests {

    public static void test() {
        System.out.println(SearchBuilder.replace("level_0.raw-level_1.raw"));
        System.out.println(SearchBuilder.replace("categories.cn.raw"));

        // (currentTimeMillis / 1000L) + (86400 * n(days))
        long l = (System.currentTimeMillis() / 1000L) + (86400 * 200);
        System.out.println(l);
    }

    public static void main(String[] args) {
        // STEP 1
        Set<String> excludes = new HashSet<>();
        excludes.add("1234");
        excludes.add("1235");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("filter[q]", "bass,guitar");
        request.setParameter("filter[category]", "Guitars and Basses,EV");
        request.setParameter("filter[brand]", "Yamaha");

        // [ALWAYS] [GLOBAL QUERY]
        TopicQueryBuilder topicQueryBuilder = new TopicQueryBuilder(request, excludes);

        // [CHANNELS]
        TopicChannelDecorator topicChannelDecorator = new TopicChannelDecorator(topicQueryBuilder, null, 2L, 1, 20);

        ElasticFunctionScore functionScore1 = new ElasticFunctionScore();
        functionScore1.setNumericField("items.price");
        functionScore1.setFilters("forumId:1");
        functionScore1.setWeight(2D);
        functionScore1.setFunctionModifier("LOG1P");
        functionScore1.setFunctionFactor(0.1D);
        functionScore1.setFunctionMissing(1D);

        ElasticFunctionScore functionScore2 = new ElasticFunctionScore();
        functionScore2.setNumericField("created");
        functionScore2.setWeight(8D);
        functionScore2.setFunctionModifier("LOG1P");
        functionScore2.setFunctionFactor(0.1D);
        functionScore2.setFunctionMissing(1D);

        // [ALWAYS] [DEFAULT 0] [NAVIGATION]
        ElasticQueryBuilder elasticQueryBuilder = new ElasticQueryBuilder();
        elasticQueryBuilder.setQueryFieldsEn("items.titles.en^3,items.contents.en");
        elasticQueryBuilder.setQueryFieldsCn("items.titles.cn^3,items.contents.cn");
        elasticQueryBuilder.setFilterCategoriesEn("Guitars and Basses,Acoustic Guitars");
        elasticQueryBuilder.setFilterCategoriesCn(null);
        elasticQueryBuilder.setPositiveQueryStringEn("+guitar -acoustic");
        elasticQueryBuilder.setPositiveQueryStringCn("+吉他 -acoustic");
        elasticQueryBuilder.setNegativeQueryStringEn("+Kaossilator");
        elasticQueryBuilder.setNegativeQueryStringCn("+Kaossilator");
        elasticQueryBuilder.setNegativeBoost(1D);
        elasticQueryBuilder.setFunctionScoreMode("SUM");
        elasticQueryBuilder.setFunctionBoostMode("SUM");
        elasticQueryBuilder.setFunctionScoreList(Arrays.asList(
                functionScore1,
                functionScore2
        ));

        TopicBoostingDecorator decorator = new TopicBoostingDecorator(
                new TopicFunctionScoreDecorator(
                        topicQueryBuilder,
                        elasticQueryBuilder),
                elasticQueryBuilder);
        QueryBuilder operation = decorator.operation();

        //
        System.out.println(operation);

    }

}
