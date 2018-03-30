package com.mifan.article.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.moonframework.core.support.OperateComponent;
import org.moonframework.elasticsearch.SearchResult;
import org.moonframework.elasticsearch.aggregation.AggregationOperator;
import org.moonframework.elasticsearch.aggregation.AggregationType;
import org.moonframework.elasticsearch.aggregation.Bucket;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.QueryFieldOperator;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Include;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.security.authentication.UserPermissions;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mifan.article.cache.KeywordsCache;
import com.mifan.article.cache.TopicsCache;
import com.mifan.article.cache.TopicsHistoryCache;
import com.mifan.article.domain.Navigation;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.TopicsRank;
import com.mifan.article.domain.UsersTopicsFavorite;
import com.mifan.article.service.SearchService;
import com.mifan.article.service.TopicsFetchService;
import com.mifan.article.service.TopicsService;
import com.mifan.article.service.UsersTopicsHideService;
import com.mifan.article.service.attachment.ImageColor;
import com.mifan.article.service.search.aggregation.Aggregation;
import com.mifan.article.service.search.aggregation.Aggregations;
import com.mifan.article.service.search.builder.TopicsBuilderConsumer;
import com.mifan.article.service.search.query.TopicBoostingDecorator;
import com.mifan.article.service.search.query.TopicChannelDecorator;
import com.mifan.article.service.search.query.TopicFunctionScoreDecorator;
import com.mifan.article.service.search.query.TopicQueryBuilder;
import com.mifan.article.service.search.suggest.Suggestions;
import com.mifan.article.util.SearchBuilder;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/12
 */
@RestController
@RequestMapping("/topics")
public class TopicsController extends RestfulController<Topics> {

    private static final String X_HEADER_USER = "X-User-ssid";

    @Autowired
    private TopicsCache topicsCache;

    @Autowired
    private TopicsHistoryCache topicsHistoryCache;

    @Autowired
    private KeywordsCache keywordsCache;

    @Autowired
    private TopicsService topicsService;

    @Autowired
    private UsersTopicsHideService usersTopicsHideService;

    @Autowired
    private SearchService searchService;

    @Qualifier("topicAggregations")
    @Autowired
    private Aggregations aggregations;
    
    @Autowired
    private TopicsFetchService topicsFetchService;


    
    /**
     * <p>产品比较</p>
     *
     * @param art art
     * @return Response
     */
    @RequestMapping(value = "/compare", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<Response> compare(
            @RequestParam(value = "art[]") String[] art) {

        return ResponseEntity.ok(topicsService.compare(new LinkedHashSet<>(Arrays.asList(art))));
    }

    /**
     * <p>我的收藏</p>
     *
     * @param page    page
     * @param size    size
     * @param request request
     * @return Response
     */
    @RequiresAuthentication
    @RequestMapping(value = "/favorites", method = RequestMethod.GET)
    public HttpEntity<Response> doPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            HttpServletRequest request) {

        // [check] size
        if (size > 100) {
            size = 100;
        }
        if (size < 0) {
            size = 0;
        }

        List<Criterion> criteria = new ArrayList<>();
        criteria.add(Restrictions.eq(UsersTopicsFavorite.ENABLED, 1));
        criteria.add(Restrictions.eq(UsersTopicsFavorite.USER_ID, getCurrentUserId()));

        String key = "filter[forumId]";
        String[] forums = request.getParameterMap().get(key);
        if (forums != null && forums.length != 0) {
            criteria.add(Restrictions.in(UsersTopicsFavorite.FORUM_ID, forums));
        }

        // [find]
        Page<UsersTopicsFavorite> favorites = Services.findAll(UsersTopicsFavorite.class,
                Restrictions.and(criteria),
                Pages.builder()
                        .page(page)
                        .size(size)
                        .sort(Pages.sortBuilder()
                                .add(UsersTopicsFavorite.CREATED, false)
                                .build())
                        .build(),
                Fields.builder()
                        .add(UsersTopicsFavorite.TOPIC_ID)
                        .build(),
                true);

        Collection<String> collection = favorites
                .getContent()
                .stream()
                .map(topicsRank -> topicsRank.getTopicId().toString())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return response(request, collection, page, size, favorites.getTotalElements(), false);
    }

    /**
     * <p>MLT API</p>
     *
     * @param id      id
     * @param page    page
     * @param size    size
     * @param request request
     * @return Response
     */
    @RequestMapping(value = "/{id}/relates", method = RequestMethod.GET)
    public HttpEntity<Response> doPage(
            @PathVariable String id,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            HttpServletRequest request) {

        // [check] size
        if (size > 100) {
            size = 100;
        }
        if (size < 0) {
            size = 0;
        }

        // [convert] to elastic index
        final int pageFrom = ((page < 1 ? 1 : page) - 1) * size;
        final int pageSize = size;

        Long userId = getCurrentUserId();
        Set<String> excludes;
        if (userId != null) {
            excludes = usersTopicsHideService.findByUserId(userId);
        } else {
            excludes = new HashSet<>();
        }
        excludes.add(id);
        SearchResult<Map<String, Object>> result = searchService.moreLikeThisById(Topics.class, pageFrom, pageSize, id, excludes, builder -> {
            TopicQueryBuilder topicQueryBuilder = new TopicQueryBuilder(request, null);
            builder.must(topicQueryBuilder.operation());
        });
        return ResponseEntity.ok(topicsService.convert(page, size, result, request.getParameterMap()));
    }

    /**
     * <p>全局</p>
     * <ul>
     * <li>filter[q]: title and content</li>
     * <li>filter[categories]</li>
     * <li>filter[tags]</li>
     * <li>filter[forumId]</li>
     * <li>filter[topicType]</li>
     * <li>filter[postType]</li>
     * <li>filter[creator]</li>
     * <li>filter[hasRotations]</li>
     * <li>filter[hasImages]</li>
     * <li>filter[hasVideos]</li>
     * <li>filter[hasAudios]</li>
     * </ul>
     * <p>产品</p>
     * <ul>
     * <li>filter[brand]=2box</li>
     * <li>filter[price:between]=[12,34]</li>
     * <li>filter[color]=FF0000</li>
     * <li>filter[from]=11</li>
     * </ul>
     * <p>排序</p>
     * <ul>
     * <li>颜色: images.distance.{0xFF0000}</li>
     * <li>价格: items.price</li>
     * <li>时间: created</li>
     * </ul>
     * <p>include: clustering</p>
     * <p>aggregation parameters, aggregation[name]=fields</p>
     *
     * @param page    page
     * @param size    size
     * @param sort    sort
     * @param include include
     * @return response
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public HttpEntity<Response> doPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include,
            @RequestParam(required = false, name = "agg[mask]", defaultValue = "15") int aggMask,
            @RequestParam(required = false, name = "agg[size]", defaultValue = "0") int aggSize,
            @RequestParam(required = false, name = "agg[sort]", defaultValue = "term") String aggSort,
            @RequestParam(required = false, name = "agg[image]", defaultValue = "false") boolean aggImage,
            @RequestParam(required = false, name = "agg[group]") Boolean aggGroup,
            @RequestParam(required = false, name = "suggest[size]", defaultValue = "0") int suggestSize,
            @RequestParam(required = false, name = "suggest[type]", defaultValue = "phrase") String suggestType,
            @RequestParam(required = false, name = "filter[navigation]", defaultValue = "0") Long navigationId,
            @RequestParam(required = false, name = "filter[channel]") Long channel,
            @RequestParam(required = false, name = "filter[channelId]") Long channelId,
            HttpServletRequest request ) {

        if ("completion".equals(suggestType)) {
            return ResponseEntity.ok(topicsService.convert(page, size, searchService.search(Topics.class, builder -> {
                builder.setSize(0).setSearchType(SearchType.QUERY_THEN_FETCH);
                Suggestions.completion(builder, suggestSize, getString("filter[q]"), request.getParameterMap());
            }), request.getParameterMap()));
        }

        if (channel != null && channelId == null) {
            channelId = channel;
        }

        // [include] : clustering
        if (include != null) {
            Restrictions.put(Include.class, new Include(include));
        }

        // [check] size
        if (size > 100) {
            size = 100;
        }
        if (size < 0) {
            size = 0;
        }

        // [convert] to elastic index
        final int pageFrom = ((page < 1 ? 1 : page) - 1) * size;
        final int pageSize = size;

        // [rank]
        HttpEntity<Response> rank = rank(page, size, sort, request);
        if (rank != null) {
            return rank;
        }

        // [user hide topics]
        Long userId = getCurrentUserId();
        Set<String> excludes = userId == null ? null : usersTopicsHideService.findByUserId(userId);

        // [query]
        boolean decorated = false;
        TopicQueryBuilder topicQueryBuilder = new TopicQueryBuilder(request, excludes, aggMask);
        OperateComponent<QueryBuilder> component;
        TopicChannelDecorator topicChannelDecorator = null;
        if (channelId != null) {
            topicChannelDecorator = new TopicChannelDecorator(topicQueryBuilder, topicsCache, channelId, page, pageSize);
            topicChannelDecorator.operation();
            TopicChannelDecorator.Result result = topicChannelDecorator.getResult();
            decorated = topicChannelDecorator.isDecorated();
            if (result != null) {
                return response(request, result.getIds(), page, size, result.getTotal(), false);
            } else {
                component = topicChannelDecorator;
            }
        } else {
            component = topicQueryBuilder;
        }

        // [navigation]
        QueryBuilder root;
        if (decorated) {
            root = topicChannelDecorator.operation();
        } else {
            Navigation navigation = Services.findOne(Navigation.class, navigationId);
            TopicBoostingDecorator decorator = new TopicBoostingDecorator(
                    new TopicFunctionScoreDecorator(
                            component,
                            navigation.getElasticQueryBuilder()),
                    navigation.getElasticQueryBuilder());
            root = decorator.operation();
        }

        // [search]
        SearchResult<Map<String, Object>> result = searchService.search(Topics.class, builder -> {
            builder
                    .setQuery(root)
                    .setFetchSource(TopicsBuilderConsumer.FIELD_LIST, null)
                    .setFrom(pageFrom)
                    .setSize(pageSize)
                    .addHighlightedField("items.contents.en", 100, 2)
                    .addHighlightedField("items.contents.en", 100, 2)
                    .setHighlighterPreTags("<span class='highlight'>").setHighlighterPostTags("</span>");

            // [suggest]
            Suggestions.phrase(builder, suggestSize, topicQueryBuilder.getQuery());

            // [sort]
            SearchBuilder.sort(sort, null).forEach(builder::addSort);

            // [aggregation]
            builder.setSearchType(SearchType.QUERY_THEN_FETCH);
            if (aggSize >= 0 && !AggregationOperator.addAggregation(builder, request.getParameterMap(), aggSize, aggSort, SearchBuilder::replace)) {
                aggregation(builder, topicQueryBuilder.getMask(), aggSize, aggSort);
            }
        });

        Map<String, Object> aggregations = result.getAggregations();
        if (aggregations != null) {
            // [aggregations image]
            if (aggImage) {
                List<Bucket> buckets = new ArrayList<>();
                AggregationType.aggregations(aggregations, buckets::add);
                for (Bucket bucket : buckets) {
                    bucket.getAggregations().put("image", topicsService.image(
                            QueryBuilders.boolQuery()
                                    .must(root)
                                    .must(QueryBuilders.termQuery(bucket.getNameField().getField(), bucket.getValue()))
                                    .filter(QueryBuilders.existsQuery(TopicsBuilderConsumer.IMAGES_FILENAME))
                    ));
                }
            } else {
                AggregationType.aggregations(aggregations, null);
            }

            // [aggregations group]
            for (Aggregation aggregation : this.aggregations.toList(topicQueryBuilder.getMask())) {
                aggregation.accept(aggregations, aggGroup);
            }
        }

        // [word record]
        String query = topicQueryBuilder.getQuery();
        if (!result.isEmpty() && !StringUtils.isEmpty(query)) {
            String text = SearchBuilder.decode(query);
            AnalyzeResponse analyze = searchService.analyze("topic", "items.titles.cn", text);
            List<AnalyzeResponse.AnalyzeToken> tokens = analyze.getTokens();
            if (!tokens.isEmpty()) {
                for (AnalyzeResponse.AnalyzeToken analyzeToken : analyze.getTokens()) {
                    String term = analyzeToken.getTerm();
                    String type = analyzeToken.getType();
                    if (logger.isInfoEnabled()) {
                        logger.info(String.format("type : %s, term : %s", type, term));
                    }
                }
                keywordsCache.increment(text);
            }
        }

        return ResponseEntity.ok(topicsService.convert(page, size, result, request.getParameterMap()));
    }

    /**
     * 搜索热词
     * zyw
     * @return
     */
    @RequestMapping(value = "/hotKeywords", method = RequestMethod.GET)
    public HttpEntity<Response> hotKeywords(@RequestParam(required = true) int product){
        List<Topics> result = topicsService.findHotKeywords(product);
        return ResponseEntity.ok(Responses.builder().data(result));
    }
    
    /**
     * @param size size
     * @return Response
     */
    @RequestMapping(value = "/keywords", method = RequestMethod.GET)
    public ResponseEntity<Response> keywords(
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size) {
        return ResponseEntity.ok(Responses.builder().data(keywordsCache.keywords(size)));
    }
    /**
     * 美频热榜新闻
     * @param seedIds
     * @param page
     * @param size
     * @param request
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/mpHot", method = RequestMethod.GET)
    public HttpEntity<Response> mpHotNews(@RequestParam(required = true, name = "filter[seedIds]") String seedIds,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            HttpServletRequest request){
        SearchResult<Map<String, Object>> result = topicsFetchService.mpHotNews(seedIds, size);
        return ResponseEntity.ok(topicsService.convert(page, size, result, request.getParameterMap()));
    }
    /**
     * 美频推荐新闻
     * @param seedIds
     * @param size
     * @param request
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/mpRecommend", method = RequestMethod.GET)
    public HttpEntity<Response> mpRecommendNews(@RequestParam(required = true, name = "filter[seedIds]") String seedIds,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            HttpServletRequest request){
        SearchResult<Map<String, Object>> result = topicsFetchService.mpRecommendNews(seedIds, size);
        return ResponseEntity.ok(topicsService.convert(1, size, result, request.getParameterMap()));
    }
    /**
     * @param size    size
     * @param request request
     * @return Response
     */
    @RequestMapping(value = "/histories", method = RequestMethod.GET)
    public ResponseEntity<Response> histories(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false, name = "filter[forumId]") Long forumId,
            HttpServletRequest request ) {

        // [check] size
        if (size > 100) {
            size = 100;
        }
        if (size < 0) {
            size = 0;
        }

        final int pageSize = size;

        return user((token, username) -> {
            Page<String> result = topicsHistoryCache.page(token, forumId, page, pageSize);
            return response(request, result.getContent(), page, pageSize, result.getTotalElements(), false);
        });
    }

    /**
     * <p>查找详细</p>
     *
     * @param id      id
     * @param include include
     * @return response
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {

        return super.doGet(id, include);
    }

    @Override
    protected void afterGet(Topics topic) {
        if (topic != null) {
            Long id = topic.getId();
            Services.transactional(Topics.class, () -> {
                topicsCache.reviews(id);
                return 1;
            });
            user((token, username) -> {
                topicsHistoryCache.viewed(token, username, topic.getForumId(), id);
                return null;
            });
        }
    }

    /**
     * <p>查询颜色集合</p>
     *
     * @return Response
     */
    @RequestMapping(value = "/colors", method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage() {

        List<String> list = Arrays.stream(ImageColor.values()).map(ImageColor::getName).collect(Collectors.toList());
        Page<String> result = new PageImpl<>(list, null, list.size());
        ResponseEntity.ok(Responses.builder().page(result).data(result.getContent()));
        return ResponseEntity.ok(Responses.builder().page(result).data(result.getContent()));
    }

    private HttpEntity<Response> rank(int page, int size, String[] sorts, HttpServletRequest request) {

        if (sorts == null || sorts.length != 1) {
            return null;
        }

        String name = sorts[0];
        boolean asc = !name.startsWith("-");
        name = asc ? name : name.substring(1);
        TopicsRank.RankType rankType = TopicsRank.RankType.from(name);
        if (rankType != null) {
            // [criterion] forums
            Map<String, String[]> params = new HashMap<>(1);
            params.put(String.format("filter[%s]", TopicsRank.RANK_TYPE), new String[]{String.valueOf(rankType.getIndex())});
            String[] keys = new String[]{"filter[forumId]", "filter[topicId:ne]", "filter[topicId:nin]"};
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (String key : keys) {
                String[] values = parameterMap.get(key);
                if (values != null && values.length != 0) {
                    params.put(key, values);
                }
            }

            Criterion criterion = QueryFieldOperator.criterion(params);

            // [find]
            Page<TopicsRank> ranks = Services.findAll(TopicsRank.class,
                    criterion,
                    Pages.builder()
                            .page(page)
                            .size(size)
                            .sort(Pages.sortBuilder()
                                    .add(TopicsRank.SCORE, asc)
                                    .build())
                            .build(),
                    Fields.builder()
                            .add(TopicsRank.TOPIC_ID)
                            .build(),
                    true);
            Collection<String> collection = ranks
                    .getContent()
                    .stream()
                    .map(topicsRank -> topicsRank.getTopicId().toString())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            return response(request, collection, page, size, ranks.getTotalElements(), true);
        }
        return null;
    }

    private void aggregation(SearchRequestBuilder builder, int mask, int aggSize, String aggSort) {

        List<Aggregation> list = aggregations.toList(mask);
        Terms.Order order = AggregationOperator.order(aggSort);
        for (com.mifan.article.service.search.aggregation.Aggregation Aggregation : list) {
            AbstractAggregationBuilder aggregationBuilder = Aggregation.build();
            if (aggregationBuilder instanceof TermsBuilder) {
                TermsBuilder termsBuilder = (TermsBuilder) aggregationBuilder;
                termsBuilder.size(aggSize).order(order);
            }
            builder.addAggregation(aggregationBuilder);
        }
    }

    /**
     * <p>根据<b>ID标识符集合</b>查询数据, 并按集合顺序返回</p>
     *
     * @param request    request
     * @param collection ID标识符集合
     * @param page       page
     * @param size       size
     * @param total      total
     * @param cached     是否缓存
     * @return Response
     */
    @SuppressWarnings("unchecked")
    private ResponseEntity<Response> response(HttpServletRequest request, Collection<String> collection, int page, int size, long total, boolean cached) {
        Response data;
        if (collection.isEmpty()) {
            data = Responses.builder()
                    .page(new PageImpl<>(Collections.EMPTY_LIST, Pages.builder().page(page).size(size).build(), total), "/topics/search", request.getParameterMap())
                    .data(Collections.EMPTY_LIST);
        } else {
            SearchResult<Map<String, Object>> result = searchService.search(Topics.class, collection, true, cached);
            result.setTotal(total);
            data = topicsService.convert(page, size, result, request.getParameterMap());
        }
        return ResponseEntity.ok(data);
    }

    /**
     * @param function function
     * @param <T>      T
     * @return T
     */
    private <T> T user(BiFunction<String, String, T> function) {
        // [token]
        HttpServletRequest request = getHttpServletRequest();
        UserPermissions principal = getPrincipal();
        String token;
        String username;
        if (principal != null) {
            token = principal.getUserId().toString();
            username = principal.getUsername();
        } else {
            token = request.getHeader(X_HEADER_USER);
            username = null;
        }
        return function.apply(token, username);
    }

}

