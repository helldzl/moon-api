package com.mifan.article.es;

import com.mifan.article.AbstractTests;
import com.mifan.article.domain.Topics;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.suggest.SuggestRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.junit.Test;
import org.moonframework.elasticsearch.Searchable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * <p>https://stackoverflow.com/questions/35040625/how-to-analyze-text-in-elasticsearch-using-java-api</p>
 * <p>https://stackoverflow.com/questions/41291989/call-search-analyzer-on-text-using-elasticsearch-java-api</p>
 */
public class AnalyzeTests extends AbstractTests {

    @Autowired
    private Client client;

    @Test
    public void testFindOne() {
        String index = "topic";
        String type = "modified";
        String field = "_id";
        String id = "1";
        SearchResponse response = client.prepareSearch(index)
                .setTypes(type)
                .setQuery(QueryBuilders.idsQuery().ids(id))
//                .setQuery(QueryBuilders.termQuery(META_FIELD_ID, id))
                .execute()
                .actionGet();

        response = client.prepareSearch(index)
                .setTypes(type)
                .setQuery(QueryBuilders.termQuery(field, id))
                .execute()
                .actionGet();

        System.out.println();
    }

    /**
     * <p>实时更新统计数</p>
     *
     * @throws Exception
     */
    @Deprecated
    @Test
    public void testUpdate() {
        Map<String, Object> params = new HashMap<>();
        params.put("count", 1);
        Script script = new Script("ctx._source.statistics.reviews += count",
                ScriptService.ScriptType.INLINE,
                "groovy", params);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("reviews", 1);
        statistics.put("votes", 1);
        Map<String, Object> upsert = new HashMap<>();
        upsert.put("statistics", statistics);

        for (int i = 0; i < 100000; i++) {
            UpdateResponse updateResponse = client
                    .prepareUpdate("my_index", "my_type", "1")
                    .setScript(script)
                    .setUpsert(upsert)
                    .get();
            System.out.println(i);
        }

        System.out.println();
    }

    @Test
    public void testAnalyze() {
        AnalyzeRequest request = new AnalyzeRequest("topic")
                .analyzer("smartcn")
                .text("我爱中华人民共和国");
        AnalyzeResponse analyzeTokens = client.admin().indices().analyze(request).actionGet();
        List<AnalyzeResponse.AnalyzeToken> tokens = analyzeTokens.getTokens();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            // do something with each token...
            System.out.println(token);
        }
        // TODO
        SuggestRequest suggestRequest = new SuggestRequest();
        CompletionSuggestionBuilder builder = new CompletionSuggestionBuilder("ac");
        client.suggest(suggestRequest);
    }

    public static void main(String[] args) {
        BoolQueryBuilder filter = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("title", "hello java"))
                .filter(QueryBuilders.rangeQuery("price").from(1).to(20));

        int[] a = new int[]{1, 2, 3};
        String collect = Stream.of(a).map(Object::toString).collect(joining(","));
        System.out.println(filter);


        Topics topic = new Topics();
        topic.setEnabled(1);
        aa(topic);

    }

    public static void aa(Searchable searchable) {
        System.out.println(searchable.getEnabled());
    }

}
