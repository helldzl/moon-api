package com.mifan.article.service.impl;

import com.mifan.article.domain.Topics;
import com.mifan.article.service.SearchService;
import com.mifan.article.service.search.PagedSearchEngine;
import com.mifan.article.service.search.SearchDocument;
import com.mifan.article.service.search.builder.BuilderConsumer;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.moonframework.elasticsearch.*;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.mifan.article.service.search.SearchDocument.IndexType;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/11/23
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private PagedSearchEngine pagedSearchEngine;

    @Autowired
    private SearchDocument searchDocument;

    @Override
    public AnalyzeResponse analyze(AnalyzeRequest analyzeRequest) {
        return pagedSearchEngine.analyze(analyzeRequest);
    }

    @Override
    public AnalyzeResponse analyze(String index, String field, String text) {
        return pagedSearchEngine.analyze(new AnalyzeRequest(index).field(field).text(text));
    }

    @Override
    public <T extends BaseEntity & Searchable> DeleteResponse delete(Class<T> classType, String id) {
        BuilderConsumer<XContentBuilder, T> bc = searchDocument.get(classType, IndexType.LIST);
        return pagedSearchEngine.delete(bc.getIndex(), bc.getType(), id);
    }

    /**
     * <p>绝对不要再事物中执行, 如果在事务中执行, 并且索引的数据量巨大的时候, 可能会发生OutOfMemoryError</p>
     *
     * @param classType classType
     * @param <T>       T
     */
    @Override
    public <T extends BaseEntity & Searchable> void index(Class<T> classType) {
        pagedSearchEngine.index(classType);
    }

    @Override
    public <T extends BaseEntity & Searchable> void index(Class<T> classType, String... ids) {
        pagedSearchEngine.index(classType, ids);
    }

    @Override
    public <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(Class<T> classType, String id) {
        return search(classType, Collections.singleton(id));
    }

    @Override
    public <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(Class<T> classType, Collection<String> ids) {
        return search(searchDocument.get(classType, IndexType.LIST), ids);
    }

    @Override
    public <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(Class<T> classType, Collection<String> ids, boolean ordered) {
        SearchResult<Map<String, Object>> result = search(searchDocument.get(classType, IndexType.LIST), ids);
        if (ordered) {
            AbstractSearchEngine.order(result, ids);
        }
        return result;
    }

    @Cacheable(cacheManager = "ehCacheCacheManager", cacheNames = "article:topics", key = "T(org.apache.commons.codec.digest.DigestUtils).md5Hex(#classType.getSimpleName() + ':' + #ids.toString())", condition = "#cached")
    @Override
    public <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(Class<T> classType, Collection<String> ids, boolean ordered, boolean cached) {
        return search(classType, ids, ordered);
    }

    @Override
    public <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(BuilderConsumer<XContentBuilder, T> bc, Collection<String> ids) {
        return search(Topics.class, builder -> builder.setQuery(QueryBuilders.idsQuery().ids(ids))
                .setFetchSource(bc.getIncludeListFields(), null)
                .setSize(ids.size()));
    }

    /**
     * <p>通用搜索API</p>
     *
     * @param classType classType
     * @param consumer  consumer
     * @param <T>       T
     * @return result
     */
    @Override
    public <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(Class<T> classType, Consumer<SearchRequestBuilder> consumer) {
        BuilderConsumer<XContentBuilder, T> bc = searchDocument.get(classType, IndexType.LIST);
        return pagedSearchEngine.search(bc.getIndex(), bc.getType(), consumer);
    }

    @Override
    public <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> search(Class<T> classType, Consumer<SearchRequestBuilder> consumer, Function<Aggregations, Map<String, Object>> function) {
        BuilderConsumer<XContentBuilder, T> bc = searchDocument.get(classType, IndexType.LIST);
        return pagedSearchEngine.search(bc.getIndex(), bc.getType(), consumer, function);
    }

    @Override
    public <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> moreLikeThisById(Class<T> classType, int from, int size, String id, Set<String> excludes, Consumer<BoolQueryBuilder> consumer) {
        BuilderConsumer<XContentBuilder, T> bc = searchDocument.get(classType, IndexType.LIST);

        SearchResult<Map<String, Object>> result = pagedSearchEngine.search(bc.getIndex(), bc.getType(), builder -> builder
                .setQuery(QueryBuilders.idsQuery().ids(id))
                .setFetchSource(new String[]{"items"}, null)
                .setSize(1));

        List<Map<String, Object>> list = AbstractSearchEngine.source(result);
        if (list.isEmpty()) {
            return SearchUtils.emptySearchResult();
        }

        StringBuilder text = new StringBuilder();
        AbstractSearchEngine.list(list, null, token -> {
            String route = token.getRoute();
            if ("items.contents.en".equals(route) || "items.descriptions.en".equals(route) || "items.titles.en".equals(route)) {
                text.append(token.getValue()).append(",");
            }
        });
        if (text.length() == 0) {
            return SearchUtils.emptySearchResult();
        }
        return moreLikeThisByText(classType, from, size, text.toString(), excludes, consumer);
    }

    /**
     * <p>More Like This</p>
     * <p>The query filter is deprecated as is it no longer needed — all queries can be used in query or filter context.</p>
     * <p>The filtered query is deprecated in favour of the bool query. Instead of the following:</p>
     *
     * @param classType classType
     * @param size      size
     * @param likeText  likeText
     * @param <T>       type
     * @param consumer  consumer
     * @return result
     */
    @Override
    public <T extends BaseEntity & Searchable> SearchResult<Map<String, Object>> moreLikeThisByText(Class<T> classType, int from, int size, String likeText, Set<String> excludes, Consumer<BoolQueryBuilder> consumer) {
        BuilderConsumer<XContentBuilder, T> bc = searchDocument.get(classType, IndexType.LIST);
        return pagedSearchEngine.search(bc.getIndex(), bc.getType(), builder -> {
            // [more like this]
            MoreLikeThisQueryBuilder moreLikeThisQueryBuilder = QueryBuilders.moreLikeThisQuery(bc.getMLTFields());
            moreLikeThisQueryBuilder.addLikeText(likeText);
            moreLikeThisQueryBuilder.minTermFreq(1).maxQueryTerms(12);

            // [query builder]
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(moreLikeThisQueryBuilder);

            // [exclude]
            if (excludes != null) {
                queryBuilder.mustNot(QueryBuilders.idsQuery().ids(excludes));
            }

            // [consumer]
            if (consumer != null) {
                consumer.accept(queryBuilder);
            }

            builder.setQuery(queryBuilder)
                    .setFetchSource(bc.getIncludeListFields(), null)
                    .setFrom(from)
                    .setSize(size);
        });
    }

    @Override
    public <T extends BaseEntity & Searchable> SearchWithClustersResult searchWithClusters(Class<T> classType, String queryHint, Consumer<SearchRequestBuilder> consumer) {
        BuilderConsumer<XContentBuilder, T> bc = searchDocument.get(classType, IndexType.LIST);
        return pagedSearchEngine.searchWithClusters(bc.getIndex(), bc.getType(), queryHint, bc.getTitleFieldName(), bc.getContentFieldName(), consumer);
    }

}
