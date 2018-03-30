package com.mifan.article.service;

import com.mifan.article.service.search.builder.BuilderConsumer;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.moonframework.elasticsearch.SearchResult;
import org.moonframework.elasticsearch.SearchWithClustersResult;
import org.moonframework.elasticsearch.Searchable;
import org.moonframework.model.mybatis.domain.BaseEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/19
 */
public interface SearchService {

    AnalyzeResponse analyze(AnalyzeRequest analyzeRequest);

    AnalyzeResponse analyze(String index, String field, String text);

    <T extends BaseEntity & Searchable> DeleteResponse delete(Class<T> classType, String id);

    <T extends BaseEntity & Searchable> void index(Class<T> classType);

    <T extends BaseEntity & Searchable> void index(Class<T> classType, String... ids);

    <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(Class<T> classType, String id);

    <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(Class<T> classType, Collection<String> ids);

    <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(Class<T> classType, Collection<String> ids, boolean ordered);

    <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(Class<T> classType, Collection<String> ids, boolean ordered, boolean cached);

    <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(BuilderConsumer<XContentBuilder, T> bc, Collection<String> ids);

    <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(Class<T> classType, Consumer<SearchRequestBuilder> consumer);

    <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(Class<T> classType, Consumer<SearchRequestBuilder> consumer, Function<Aggregations, Map<String, Object>> function);

    /**
     * <p>MLT selects a set of representative terms of these input documents, forms a query using these terms, executes the query and returns the results.</p>
     *
     * @param classType classType
     * @param size      size
     * @param from      from
     * @param id        id
     * @param excludes  excludes
     * @param consumer  consumer
     * @return result
     */
    <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> moreLikeThisById(Class<T> classType, int from, int size, String id, Set<String> excludes, Consumer<BoolQueryBuilder> consumer);

    /**
     * <p>MLT selects a set of representative terms of these input documents, forms a query using these terms, executes the query and returns the results.</p>
     *
     * @param classType classType
     * @param size      size
     * @param from      from
     * @param likeText  likeText
     * @param excludes  excludes
     * @param consumer  consumer
     * @return result
     */
    <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> moreLikeThisByText(Class<T> classType, int from, int size, String likeText, Set<String> excludes, Consumer<BoolQueryBuilder> consumer);

    <T extends BaseEntity & Searchable> SearchWithClustersResult searchWithClusters(Class<T> classType, String queryHint, Consumer<SearchRequestBuilder> consumer);

}
