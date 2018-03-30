package com.mifan.article.service.impl;


import com.mifan.article.cache.TopicsCache;
import com.mifan.article.dao.TopicsDao;
import com.mifan.article.domain.*;
import com.mifan.article.domain.support.Currency;
import com.mifan.article.domain.support.Multilingual;
import com.mifan.article.domain.support.TopicsReq;
import com.mifan.article.service.BrandsService;
import com.mifan.article.service.MpDownloadsService;
import com.mifan.article.service.SearchService;
import com.mifan.article.service.TopicsMpdownloadsService;
import com.mifan.article.service.TopicsService;
import com.mifan.article.service.WordDictionaryService;
import com.mifan.article.service.search.builder.TopicsBuilderConsumer;
import com.mifan.article.service.search.query.TopicQueryBuilder;
import com.mifan.article.service.util.EntityUtils;
import com.mifan.article.util.SearchBuilder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.moonframework.elasticsearch.AbstractSearchEngine;
import org.moonframework.elasticsearch.SearchResult;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.MatchMode;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.security.authentication.UserPermissions;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.moonframework.web.utils.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import static java.util.stream.Collectors.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class TopicsServiceImpl extends AbstractBaseService<Topics, TopicsDao> implements TopicsService {

    @Autowired
    private SearchService searchService;

    @Autowired
    private BrandsService brandsService;

    @Autowired
    @Qualifier("wordDictionaryServiceImpl")
    private WordDictionaryService wordDictionaryService;

    @Autowired
    private TopicsCache topicsCache;

    @Autowired
    private RedisTemplate<String, String> template;
    
    @Autowired
    private TopicsMpdownloadsService topicsMpdownloadsService;
    
    @Autowired
    private MpDownloadsService mpDownloadsService;

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> hotSearch(int page,int size,Integer fromDays,Set<String> excludes,String[] sort,HttpServletRequest request){
        TopicQueryBuilder topicQueryBuilder = new TopicQueryBuilder(request, excludes, 15);
        BoolQueryBuilder root = QueryBuilders.boolQuery();
        root.must(topicQueryBuilder.operation());
        if(fromDays != null && fromDays > 0) {
            LocalDate today = LocalDate.now();
            String from = today.minusDays(fromDays).toString();
            root.must(QueryBuilders.rangeQuery("created").gt(from));
        }
        final int pageFrom = ((page < 1 ? 1 : page) - 1) * size;
        final int pageSize = size;
        // [search]
        SearchResult<Map<String, Object>> result = searchService.search(Topics.class, builder -> {
            builder
                    .setQuery(root)
                    .setFetchSource(TopicsBuilderConsumer.FIELD_LIST, null)
                    .setFrom(pageFrom)
                    .setSize(pageSize);
            // [sort]
            if(sort != null && sort.length > 0) {
                SearchBuilder.sort(sort, null).forEach(builder::addSort);
            }
        });
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> hit : AbstractSearchEngine.hits(result)) {
            Map<String, Object> source = (Map<String, Object>) hit.get(AbstractSearchEngine.META_FIELD_SOURCE);
            // data
            Map<String, Object> data = new LinkedHashMap<>();
            Long id = Long.valueOf((String) hit.get(AbstractSearchEngine.META_FIELD_ID));
            data.put("id", id);
            data.putAll(Topics.convert(source, map -> map.putAll(topicsCache.attributes(id, SecurityContextUtils.currentUserId()))));
            list.add(data);
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> hotSearch(Integer imageSize,long[] forumIds,int size,String[] brands,String from,String[] sort) {
        BoolQueryBuilder root = QueryBuilders.boolQuery();
        if(imageSize != null && imageSize >= 0) {
            root.must(QueryBuilders.rangeQuery("imageSize").gte(4));
        }
        if(from != null) {
            root.must(QueryBuilders.rangeQuery("created").gt(from));
        }
        if(forumIds != null && forumIds.length > 0) {
            BoolQueryBuilder forumQuery = QueryBuilders.boolQuery();
            for(long forumId : forumIds) {
                forumQuery.should(QueryBuilders.termQuery("forumId", forumId));
            }
            root.must(forumQuery);
        }
        if(brands != null && brands.length > 0) {
            BoolQueryBuilder brandQuery = QueryBuilders.boolQuery();
            for(String name : brands) {
                brandQuery.should(QueryBuilders.wildcardQuery("brand.name", "*" + name + "*"));
            }
            root.must(brandQuery);
        }
        
        // [search]
        SearchResult<Map<String, Object>> result = searchService.search(Topics.class, builder -> {
            builder
                    .setQuery(root)
                    .setFetchSource(TopicsBuilderConsumer.FIELD_LIST, null)
                    .setFrom(0)
                    .setSize(size);
            // [sort]
            if(sort != null && sort.length > 0) {
                SearchBuilder.sort(sort, null).forEach(builder::addSort);
            }
        });
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> hit : AbstractSearchEngine.hits(result)) {
            Map<String, Object> source = (Map<String, Object>) hit.get(AbstractSearchEngine.META_FIELD_SOURCE);
            // data
            Map<String, Object> data = new LinkedHashMap<>();
            Long id = Long.valueOf((String) hit.get(AbstractSearchEngine.META_FIELD_ID));
            data.put("id", id);
            data.putAll(Topics.convert(source, map -> map.putAll(topicsCache.attributes(id, SecurityContextUtils.currentUserId()))));
            list.add(data);
        }
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    @SuppressWarnings("unchecked")
    @Override
    public Response convert(int page, int size, SearchResult<Map<String, Object>> result, Map<String, String[]> params) {
        Map<String, Object> aggregations = result.getAggregations();
        Map<String, Object> suggest = result.getSuggest();
        List<Map<String, Object>> clusters = Collections.emptyList();
        List<Map<String, Object>> list = new ArrayList<>();
        Map<Long, List<Map<String, Object>>> users = new HashMap<>(16);
        for (Map<String, Object> hit : AbstractSearchEngine.hits(result)) {
            Map<String, Object> source = (Map<String, Object>) hit.get(AbstractSearchEngine.META_FIELD_SOURCE);
            Map<String, Object> highlight = (Map<String, Object>) hit.get(AbstractSearchEngine.FIELD_HIGHLIGHT);

            // meta
            Map<String, Object> meta = new HashMap<>(16);
            meta.put("score", hit.get(AbstractSearchEngine.META_FIELD_SCORE));
            if (!CollectionUtils.isEmpty(highlight)) {
                List<String> contents = highlight.entrySet().stream().flatMap(entry -> {
                    List<String> array = (List<String>) entry.getValue();
                    return array.stream();
                }).collect(Collectors.toList());
                String content = contents.isEmpty() ? null : contents.get(0);
                highlight = new HashMap<>(16);
                highlight.put("content", content);
                meta.put("highlight", highlight);
            }

            // data
            Map<String, Object> data = new LinkedHashMap<>(16);
            Long id = Long.valueOf((String) hit.get(AbstractSearchEngine.META_FIELD_ID));
            data.put("id", id);
            data.put("meta", meta);
            // data.put("attributes", convert(source));
            data.putAll(Topics.convert(source, map -> map.putAll(topicsCache.attributes(id, SecurityContextUtils.currentUserId()))));
            list.add(data);

            // find all data that is created by users
            Long creator = ((Integer) source.get("creator")).longValue();
            if (creator > 0L) {
                if (!users.containsKey(creator)) {
                    users.put(creator, new ArrayList<>());
                }
                users.get(creator).add(data);
            }
        }

        // user
        if (!users.isEmpty()) {
            Set<Long> ids = users.keySet();
            Services.findAll(Channels.class,
                    Restrictions.and(
                            Restrictions.in(Channels.TARGET_ID, ids.toArray()),
                            Restrictions.eq(Channels.CHANNEL_TYPE, Channels.ChannelType.USER.getIndex())),
                    Fields.builder()
                            .add(Channels.ID)
                            .add(Channels.TARGET_ID)
                            .add(Channels.CHANNEL_NAME)
                            .add(Channels.CHANNEL_IMAGE)
                            .build())
                    .forEach(channel -> {
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("channelId", channel.getId());
                        map.put("source", channel.getChannelName());
                        map.put("image", channel.getChannelImage());
                        users.get(channel.getTargetId()).forEach(data -> data.put("from", map));
                    });
        }

        // page
        Responses.DefaultBuilder builder = Responses.builder();
        if (size > 0) {
            Page<Map<String, Object>> pageResult = new PageImpl<>(list, Pages.builder().page(page).size(size).build(), (long) result.getHits().get("total"));
            builder.page(pageResult, "/topics/search", params);
        }

        if (!CollectionUtils.isEmpty(aggregations)) {
            builder.meta("aggregations", aggregations);
        }
        if (!CollectionUtils.isEmpty(suggest)) {
            builder.meta("suggest", suggest);
        }
        if (!CollectionUtils.isEmpty(clusters)) {
            builder.meta("clusters", clusters);
        }
        return builder.data(list);
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    @Cacheable(cacheManager = "ehCacheCacheManager", cacheNames = "article:search:images", key = "T(org.apache.commons.codec.digest.DigestUtils).md5Hex(#queryBuilder.toString())")
    @Override
    public String image(QueryBuilder queryBuilder) {
        if (logger.isInfoEnabled()) {
            logger.info(DigestUtils.md5Hex(queryBuilder.toString()));
        }
        SearchResult<Map<String, Object>> search = searchService.search(Topics.class, builder -> builder
                .setFetchSource(TopicsBuilderConsumer.IMAGES_FILENAME, null)
                .setQuery(queryBuilder)
                .setSize(1));

        // get image
        List<Map<String, Object>> source = AbstractSearchEngine.source(search);
        String image = null;
        outer:
        for (Map<String, Object> map : source) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("images");
            for (Map<String, Object> images : list) {
                image = (String) images.get("filename");
                if (image != null) {
                    break outer;
                }
            }
        }
        return image;
    }


    /**
     * <p>查找主题详细信息</p>
     * <p>自带缓存</p>
     * <ul>
     * <li>只对爬取数据进行classification</li>
     * </ul>
     *
     * @param id     id
     * @param fields fields
     * @return Topics
     */
    @Transactional(readOnly = true)
    @Override
    public Topics queryForObject(Long id, Iterable<? extends Field> fields) {
        if (logger.isInfoEnabled()) {
            logger.info("Read [Topics] from database, ID : " + id);
        }
        return queryForObject(id, fields, true, true);
    }

    /**
     * @param id             ID
     * @param fields         fields
     * @param clustering     是否查找聚合数据
     * @param authentication 是否需要认证
     * @return Topics
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    @Override
    public Topics queryForObject(Long id, Iterable<? extends Field> fields, boolean clustering, boolean authentication) {
        // [ACL]
        boolean authenticated = !authentication || SecurityUtils.getSubject().isAuthenticated();

        // [topics] find topic
        Topics topic = new Topics(id);
        topic.setEnabled(1);
        topic = findOne(topic, fields != null && fields.iterator().hasNext() ? fields : Topics.DEFAULT_FIELDS);
        if (topic == null) {
            return null;
        }

        // [posts] find posts
        Posts post = findPost(topic);
        if (post == null) {
            return null;
        }
        topic.setPost(post);

        // [attachments] find attachments
        topic.attachments(findAttachment(id));

        // [topic] find origin
        SetTopicFrom(id, topic);

        // [topics] find other
        Long forumId = topic.getForumId();
        if (Topics.ForumType.PRODUCT.getIndex() == forumId) {
            // ACLs
            Fields.FieldBuilder builder = Fields.builder();
            builder.add(TopicsProduct.ID);
            builder.add(TopicsProduct.TOPIC_ID);
            builder.add(TopicsProduct.BRAND);
            builder.add(TopicsProduct.SALE_RANK);
            if (authenticated) {
                builder.add(TopicsProduct.PRICE);
                builder.add(TopicsProduct.PRICE_UNIT);
            }

            // find resource product
            TopicsProduct product = new TopicsProduct();
            product.setTopicId(id);
            product = Services.findOne(TopicsProduct.class, product, builder.build());
            if (product != null) {
                if (authenticated) {
                    Page<TopicsProductHistory> page = Services.findAll(TopicsProductHistory.class,
                            Restrictions.eq(TopicsProductHistory.TOPIC_ID, id),
                            Pages.builder().page(1).size(12).sort(Pages.sortBuilder().add(TopicsProductHistory.EFFECTIVE_DATE, false).build()).build(),
                            Fields.builder().add(TopicsProductHistory.PRICE).add(TopicsProductHistory.EFFECTIVE_DATE).build(),
                            false);
                    product.setHistories(page.getContent());
                    product.exchange(Services.findAll(ExchangeRates.class, Restrictions.and(
                            Restrictions.eq(ExchangeRates.ENABLED, 1),
                            Restrictions.eq(ExchangeRates.TO_CODE, Currency.USD.getCode())
                    )));
                } else {
                    product.setHistories(Collections.EMPTY_LIST);
                }
                // [brand]
                product.brand(product.getBrand(), brand -> brandsService.findBrand(brand));
                topic.setProduct(product);
            }
        } else if (topic.document()) {
            // find resource document
            TopicsDocument document = new TopicsDocument();
            document.setTopicId(id);
            topic.setDocument(Services.findOne(TopicsDocument.class, document));
        }


        if (Topics.ForumType.BRAND.getIndex() == forumId) {
            TopicsBrand brand = new TopicsBrand();
            brand.setTopicId(id);
            topic.setTopicsBrand(Services.findOne(TopicsBrand.class, brand));
        }
        //TODO
        /******************* 新版本这段代码要删除 **********************/
        if (Topics.ForumType.MPSUPPORT.getIndex() == forumId) {
            TopicsTune tune = new TopicsTune();
            tune.setTopicId(id);
            topic.setTune(Services.findOne(TopicsTune.class, tune));
        }
        /******************* 新版本这段代码要删除 **********************/
        if (Topics.ForumType.MPSUPPORT.getIndex() == forumId ||
        		Topics.ForumType.ANCHOR.getIndex() == forumId ||
        		Topics.ForumType.MPNEWS.getIndex() == forumId) {
        	TopicsMp mp = new TopicsMp();
        	mp.setTopicId(id);
        	topic.setMp(Services.findOne(TopicsMp.class, mp));//主题相关信息
//        	topic.setMpdownloads(topicsMpdownloadsService.getDownLoadsByTopicId(id));//主题相关下载 TODO 当开始人工管理主题和下载的关联关系时，用这行代码替换下面这行代码
        	topic.setMpdownloads(mpDownloadsService.relatedTitle(topic.getTitle()));
        	
        }

        // [relationships with users resource]
        if (authenticated) {
            Subject subject = SecurityUtils.getSubject();
            UserPermissions principal = (UserPermissions) subject.getPrincipal();
            if (principal != null && principal.getUserId() != null) {
                // TODO relationship check move to ehcache
                // [like relationship]
                UsersTopicsLike like = new UsersTopicsLike();
                like.setUserId(principal.getUserId());
                like.setTopicId(id);
                like.setEnabled(1);
                like = Services.findOne(UsersTopicsLike.class, like, Fields.builder().add(UsersTopicsLike.UP).build());
                if (like != null && like.getUp() != null) {
                    topic.setLiked(like.getUp() == 0 ? -1 : 1);
                }
                // [favorite relationship]
                UsersTopicsFavorite favorite = new UsersTopicsFavorite();
                favorite.setUserId(principal.getUserId());
                favorite.setTopicId(id);
                favorite.setEnabled(1);
                boolean exist = Services.exists(UsersTopicsFavorite.class, favorite);
                topic.setFavorite(exist ? 1 : 0);
            }
        }

        // [include] clustering
        if (clustering) {
            List<TopicsClustering> clusters = Services.findAll(
                    TopicsClustering.class,
                    Restrictions.and(
                            Restrictions.eq(TopicsClustering.TOPIC_ID, id),
                            Restrictions.eq(TopicsClustering.ENABLED, 1)),
                    Pages.builder().page(1).size(8)
                            .sort(Pages.sortBuilder()
                                    .add(TopicsClustering.SCORE, false)
                                    .build())
                            .build(),
                    Fields.builder()
                            .add(TopicsClustering.TYPE)
                            .add(TopicsClustering.TOPIC_ID)
                            .add(TopicsClustering.TARGET_ID)
                            .add(TopicsClustering.SCORE).build(), false).getContent();

            Map<Integer, List<TopicsClustering>> map = clusters.stream().collect(Collectors.groupingBy(TopicsClustering::getType));
            List<TopicsClustering> artificial;
            if (!CollectionUtils.isEmpty(artificial = map.get(1))) {
                clusters = artificial.stream().filter(c -> c.getTargetId() != 0).collect(Collectors.toList());
            }

            List<Topics> similarities = new ArrayList<>();
            for (TopicsClustering cluster : clusters) {
                Topics result = queryForObject(cluster.getTargetId(), fields, false, authentication);
                if (result != null) {
                    similarities.add(result);
                }
            }
            topic.setSimilarities(similarities);
        } else {
            topic.setSimilarities(Collections.emptyList());
        }

        return topic;
    }

    /**
     * <ol>
     * <li>先通过topics_fetch的seed_id,得到topic_id</li>
     * <li>通过topic_id在topics表的等于id</li>
     * <li>通过posts的parent_id=0,topic_id,creator=0得到一个posts的id</li>
     * <li>通过posts_text通过 posts的id 得到title,tag,description,content,feature,category</li>
     * <li>通过topics_product的topic_id得到品牌brand</li>
     * </ol>
     */
    @Transactional(readOnly = true)
    @Override
    public Page<Topics> findAll(Criterion criterion, Pageable pageable, boolean count) {
        // [topics] 根据条件查找主题列表
        Page<Topics> topicsPage = super.findAll(criterion, pageable, count);
        if (!topicsPage.hasContent()) {
            return topicsPage;
        }

        // [topics] 将主题转换为合适的数据结构
        Map<Long, Topics> topicsMap = topicsPage.getContent().stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        Set<Long> topicIds = topicsMap.keySet();
        Object[] topicIdsArray = topicIds.toArray();

        // TODO mapping, 暂时不加映射

        // [posts] 查找所有posts
        Page<Posts> postsPage = Services.findAll(Posts.class,
                Restrictions.and(
                        Restrictions.in(Posts.TOPIC_ID, topicIdsArray),
                        Restrictions.eq(Posts.ENABLED, 1),
                        Restrictions.eq(Posts.PARENT_ID, 0)),
                Pages.builder().page(1).size(topicIdsArray.length).build(),
                false);
        if (!postsPage.hasContent()) {
            return topicsPage;
        }

        // [priority 1] [categories] 查找带有等级结构的人工标注类别信息, 同时需要将父节点查出来, 重写ForumCategories的findAll
        Set<Long> categoryIds = topicsPage.getContent()
                .stream()
                .filter(topic -> topic.getCategoryId() != 0L)
                .map(Topics::getCategoryId).collect(Collectors.toSet());
        Map<Long, ForumCategories> topicsCategoryMap;
        if (!categoryIds.isEmpty()) {
            topicsCategoryMap = new HashMap<>(16);
            Map<Long, ForumCategories> categoryMap = Services.findAll(ForumCategories.class, Restrictions.and(
                    Restrictions.in(BaseEntity.ID, categoryIds.toArray()),
                    Restrictions.eq(BaseEntity.ENABLED, 1)),
                    ForumCategories.DEFAULT_FIELDS)
                    .stream()
                    .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
            for (Topics topic : topicsPage) {
                ForumCategories category = categoryMap.get(topic.getCategoryId());
                if (category != null) {
                    topicsCategoryMap.put(topic.getId(), category);
                }
            }
        } else {
            topicsCategoryMap = Collections.emptyMap();
        }

        // [priority 2] [classification] 查找机器学习出来的类别, 通常用于训练模型时, 将上次训练中产生比较好的结果加入到下次迭代中
        Map<Long, TopicsClassification> topicsClassificationMap = Services.findAll(TopicsClassification.class, Restrictions.in(BaseEntity.ID, topicIdsArray))
                .stream()
                .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

        Map<String, String> dictionary = wordDictionaryService.dictionary();
        postsPage.forEach(post -> {
            post.setDictionary(dictionary);
            post.setForumCategories(topicsCategoryMap.get(post.getTopicId()));
            post.classification(topicsClassificationMap.get(post.getTopicId()));
            post.categories();
            topicsMap.get(post.getTopicId()).setPost(post);
        });

        // ID -> posts
        Map<Long, Posts> postsMap = postsPage.getContent().stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        Set<Long> postIds = postsMap.keySet();

        // [posts_text]
        Services.findAll(PostsText.class,
                Restrictions.in(PostsText.ID, postIds.toArray()),
                Pages.builder().page(1).size(postIds.size()).build(),
                false)
                .forEach(text -> postsMap.get(text.getId()).text(text));

        // [fetch]
        Services.findAll(TopicsFetch.class,
                Restrictions.in(TopicsFetch.TOPIC_ID, topicIdsArray),
                Pages.builder().page(1).size(topicIdsArray.length).build(),
                false)
                .forEach(topicsFetch -> topicsMap.get(topicsFetch.getTopicId()).setFrom(topicsFetch));

        // [others]
        Map<Long, Set<Long>> forums = topicsPage.getContent()
                .stream()
                .collect(Collectors.groupingBy(Topics::getForumId, mapping(BaseEntity::getId, Collectors.toSet())));

        // [products]
        Set<Long> products = forums.get(Topics.ForumType.PRODUCT.getIndex());
        if (!CollectionUtils.isEmpty(products)) {
            Services.findAll(TopicsProduct.class,
                    Restrictions.in(TopicsProduct.TOPIC_ID, products.toArray()),
                    Pages.builder().page(1).size(products.size()).build(),
                    false)
                    .forEach(product -> topicsMap.get(product.getTopicId()).setProduct(product));
        }

        // [documents]
        Set<Long> documents = new HashSet<>();
        Set<Long> news = forums.get(Topics.ForumType.NEWS.getIndex());
        Set<Long> evaluations = forums.get(Topics.ForumType.EVALUATION.getIndex());
        Set<Long> videos = forums.get(Topics.ForumType.VIDEOS.getIndex());
        Set<Long> anchors = forums.get(Topics.ForumType.ANCHOR.getIndex());
        Set<Long> mpsupports = forums.get(Topics.ForumType.MPSUPPORT.getIndex());
        Set<Long> mpNews = forums.get(Topics.ForumType.MPNEWS.getIndex());
        Set<Long> mps = new HashSet<>();
        if (!CollectionUtils.isEmpty(news)) {
            documents.addAll(news);
        }
        if (!CollectionUtils.isEmpty(evaluations)) {
            documents.addAll(evaluations);
        }
        if (!CollectionUtils.isEmpty(videos)) {
            documents.addAll(videos);
        }
        if (!CollectionUtils.isEmpty(anchors)) {
            documents.addAll(anchors);
            mps.addAll(anchors);
        }
        if (!CollectionUtils.isEmpty(mpsupports)) {
            documents.addAll(mpsupports);
            mps.addAll(mpsupports);
        }
        if (!CollectionUtils.isEmpty(mpNews)) {
            documents.addAll(mpNews);
            mps.addAll(mpNews);
        }
        if (!CollectionUtils.isEmpty(documents)) {
            Services.findAll(TopicsDocument.class,
                    Restrictions.in(TopicsDocument.TOPIC_ID, documents.toArray()),
                    Pages.builder().page(1).size(documents.size()).build(),
                    false).forEach(document -> topicsMap.get(document.getTopicId()).setDocument(document));
        }
        // TODO 
        /***************** 新版本要删除这段代码 ******************/
        if (!CollectionUtils.isEmpty(mps)) {
            Services.findAll(TopicsTune.class,
                    Restrictions.in(TopicsTune.TOPIC_ID, mps.toArray()),
                    Pages.builder().page(1).size(mps.size()).build(),
                    false).forEach(tune -> topicsMap.get(tune.getTopicId()).setTune(tune));
        }
        /***************** 新版本要删除这段代码 ******************/
        if (!CollectionUtils.isEmpty(mps)) {
            Services.findAll(TopicsMp.class,
                    Restrictions.in(TopicsMp.TOPIC_ID, mps.toArray()),
                    Pages.builder().page(1).size(mps.size()).build(),
                    false).forEach(mp -> topicsMap.get(mp.getTopicId()).setMp(mp));
        }

        return topicsPage;
    }

    @Override
    public Page<Topics> findAll(TopicsReq req) {
        Criterion criterion = Restrictions.and(Restrictions.eq(Topics.FORUM_ID, req.getForumId()), Restrictions.eq(Topics.ENABLED, 1));
        if (!StringUtils.isEmpty(req.getTitle())) {
            criterion = Restrictions.and(criterion, Restrictions.like(Topics.TITLE, req.getTitle(), MatchMode.ANYWHERE));
        }
        if (req.getId() != null) {
            criterion = Restrictions.and(criterion, Restrictions.eq(Topics.ID, req.getId()));
        }
        Pageable pageable = Pages.builder().page(req.getNum()).size(req.getSize()).sort(Pages.sortBuilder().add(Topics.MODIFIED, false).build()).build();

        return this.findAll(criterion, pageable, true);
    }

    @Override
    public Page<SpiderStatistics> findSpiderStatisticsByTime(String priortime, String latertime, int page, int size) {
        Pageable pageable = Pages.builder().page(page).size(size).build();
        Page<SpiderStatistics> pages = baseDao.findSpiderStatisticsByTime(priortime, latertime, pageable, null);
        return pages;
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    @Override
    public Topics findTopics(Long id) {
        Topics topic = new Topics(id);
        topic.setEnabled(1);
        topic = findOne(topic, Topics.DEFAULT_FIELDS);
        if (topic == null) {
            return null;
        }
        // [posts] find posts
        Posts post = findParent(id);//只查父级
        if (post == null) {
            return null;
        }
        topic.setPost(post);

        // [attachments] find attachments
        topic.attachments(findAttachment(id));

        // [topic] find origin
        TopicsFetch fetch = new TopicsFetch();
        fetch.setTopicId(id);
        topic.setFrom(Services.findOne(TopicsFetch.class, fetch));

        // [topics] find other
        Long forumId = topic.getForumId();
        if (Topics.ForumType.PRODUCT.getIndex() == forumId) {
            // ACLs
            Fields.FieldBuilder builder = Fields.builder();
            builder.add(TopicsProduct.ID);
            builder.add(TopicsProduct.TOPIC_ID);
            builder.add(TopicsProduct.BRAND);
            builder.add(TopicsProduct.PRICE);
            builder.add(TopicsProduct.PRICE_UNIT);

            // find resource product
            TopicsProduct product = new TopicsProduct();
            product.setTopicId(id);
            product = Services.findOne(TopicsProduct.class, product, builder.build());
            if (product != null) {
                // [brand]
                product.brand(product.getBrand(), brand -> brandsService.findBrand(brand));
                topic.setProduct(product);
            }
        } else if (topic.document()) {
            // find resource document
            TopicsDocument document = new TopicsDocument();
            document.setTopicId(id);
            topic.setDocument(Services.findOne(TopicsDocument.class, document));
        }
        //TODO
        /******************* 新版本这段代码要删除 **********************/
        if (Topics.ForumType.MPSUPPORT.getIndex() == forumId) {
            TopicsTune tune = new TopicsTune();
            tune.setTopicId(id);
            topic.setTune(Services.findOne(TopicsTune.class, tune));
        }
        /******************* 新版本这段代码要删除 **********************/
        if (Topics.ForumType.MPSUPPORT.getIndex() == forumId) {
            TopicsMp mp = new TopicsMp();
            mp.setTopicId(id);
            topic.setMp(Services.findOne(TopicsMp.class, mp));
//            topic.setMpdownloads(topicsMpdownloadsService.getDownLoadsByTopicId(id));//主题相关下载 TODO 当开始人工管理主题和下载的关联关系时，用这行代码替换下面这行代码
            topic.setMpdownloads(mpDownloadsService.relatedTitle(topic.getTitle()));
        }
        // [topic] find origin
        SetTopicFrom(id, topic);

        return topic;
    }

    /**
     * <p>根据主题ID查询POST</p>
     * <p>数据约束规则:</p>
     * <ol>
     * <li>每个主题在同一时刻只能存在一个enabled=1 AND parent=0的主数据(primary)</li>
     * <li>每个主题在同一时刻只能存在一个enabled=1 AND parent!=0 AND creator=0 的机器翻译数据</li>
     * <li>每个主题在同一时刻可以存在多个enabled=1 AND parent!=0 AND creator!=0的人工翻译数据</li>
     * <li>树的最大深度等于2</li>
     * </ol>
     * <p>数据显示规则:</p>
     * <ol>
     * <li>返回的POST优先从主数据(primary)的孩子中选择一个评分最高作为结果返回, 如果没有则返回主数据</li>
     * <li>如果返回的数据是孩子节点, 那么孩子节点的parent属性不能为空, 必须指向其父节点</li>
     * <li>如果孩子节点有多个, 根据priority, modified降序排序, 优先显示第一个孩子节点</li>
     * </ol>
     * <p>机器学习之自动分类规则:</p>
     * <ol>
     * <li>优先显示机器学习出来的分类</li>
     * <li>在第一条的基础上, 优先使用中文分类, 如果没有则使用英文分类</li>
     * </ol>
     *
     * @param topic topic
     * @return Posts
     */
    @Transactional(readOnly = true)
    @Override
    public Posts findPost(Topics topic) {
        Long topicId = topic.getId();
        Long categoryId = topic.getCategoryId();

        // [post] find parent
        Posts parent = new Posts();
        parent.setTopicId(topicId);
        parent.setEnabled(1);
        parent.setParentId(0L);
        parent = Services.findOne(Posts.class, parent);
        if (parent == null) {
            return null;
        }

        Posts primary = parent;

        List<Posts> list = new ArrayList<>();
        list.add(parent);

        // [posts] find child
        if (parent.getCreator() == 0L) {
            // [category mapping]
            if (categoryId == null || categoryId == 0L) {
                PostsText text = Services.findOne(PostsText.class, parent.getId(), Fields.builder().add(PostsText.CATEGORY).build());
                String category;
                if (text != null && !StringUtils.isEmpty(category = text.getCategory())) {
                    int index;
                    do {
                        HashOperations<String, Object, Object> opsForHash = template.opsForHash();
                        String cid = (String) opsForHash.get("forum:categories:mapping", category);
                        if (cid != null) {
                            categoryId = Long.valueOf(cid);
                            break;
                        }
                    }
                    while ((index = category.lastIndexOf(",")) != -1 && (category = category.substring(0, index).trim()).length() != 0);
                }
            }

            // [categories]
            List<ForumCategories> categories = null;
            if (categoryId != null && categoryId > 0L) {
                categories = Services.findAll(ForumCategories.class, Restrictions.and(
                        Restrictions.eq(BaseEntity.ID, categoryId),
                        Restrictions.eq(BaseEntity.ENABLED, 1)),
                        ForumCategories.DEFAULT_FIELDS);
            }
            boolean empty = CollectionUtils.isEmpty(categories);

            if (!empty && "Useless".equalsIgnoreCase(categories.get(0).getTitle())) {
                return null;
            }

            // [classification] use classification is exists
            TopicsClassification classification = Services.findOne(TopicsClassification.class, topicId);
            if (empty && classification.getEnabled() == 0) {
                return null;
            }

            topic.setCategoryPath(empty ? null : categories.get(0).getPath());
            parent.setDictionary(wordDictionaryService.dictionary());
            parent.setForumCategories(empty ? null : categories.get(0));
            parent.classification(classification);
            parent.categories();

            Page<Posts> posts = Services.findAll(Posts.class,
                    Restrictions.and(
                            Restrictions.eq(Posts.TOPIC_ID, topicId),
                            Restrictions.eq(Posts.ENABLED, 1),
                            Restrictions.eq(Posts.PARENT_ID, parent.getId())),
                    Pages.builder().page(1).size(1).sort(Pages.sortBuilder().add(Posts.PRIORITY, false).add(Posts.MODIFIED, false).build()).build(),
                    false);
            if (posts.hasContent()) {
                primary = posts.getContent().get(0);
                primary.setParent(parent);
                list.add(primary);
            }
        }

        // [posts_text] find text
        if (!list.isEmpty()) {
            Map<Long, PostsText> map = Services.findAll(PostsText.class, Restrictions.in(BaseEntity.ID, list.stream().map(Posts::getId).collect(toSet()).toArray())).stream().collect(toMap(BaseEntity::getId, postsText -> postsText));
            for (Posts post : list) {
                post.text(map.get(post.getId()), false);
            }
        }

        return primary;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<Long, Posts> findPost(Set<Long> topicIds) {
        // [classification], filter data
        Map<Long, TopicsClassification> classifications = Services.findAll(TopicsClassification.class,
                Restrictions.and(
                        Restrictions.in(TopicsClassification.ID, topicIds.toArray()),
                        Restrictions.eq(TopicsClassification.ENABLED, 1)))
                .stream()
                .collect(toMap(BaseEntity::getId, Function.identity()));

        if (classifications.isEmpty()) {
            return Collections.emptyMap();
        }

        topicIds = classifications.keySet();
        Set<Long> parentIds = new HashSet<>(topicIds);
        parentIds.add(0L);
        Object[] parentIdsArray = parentIds.toArray();
        Object[] topicIdsArray = topicIds.toArray();

        // [post] find all, [key] topic_id_enabled_parent_id_priority_modified_idx
        Page<Posts> posts = Services.findAll(
                Posts.class,
                Restrictions.and(
                        Restrictions.in(Posts.TOPIC_ID, topicIdsArray),
                        Restrictions.eq(Posts.ENABLED, 1),
                        Restrictions.in(Posts.PARENT_ID, parentIdsArray)),
                Pages.builder().page(1).size(topicIds.size() * 5)
                        .sort(Pages.sortBuilder()
                                .add(Posts.TOPIC_ID, false)
                                .add(Posts.ENABLED, false)
                                .add(Posts.PARENT_ID, false)
                                .add(Posts.PRIORITY, false)
                                .add(Posts.MODIFIED, false)
                                .build())
                        .build(),
                false);

        if (!posts.hasContent()) {
            return Collections.emptyMap();
        }

        Map<Long, Posts> postsMap = posts.getContent().stream().collect(toMap(BaseEntity::getId, Function.identity()));

        // [result]
        Map<Long, Posts> result = new HashMap<>(16);
        Set<Long> postIds = new HashSet<>();
        posts.getContent().stream().collect(Collectors.groupingBy(Posts::getTopicId))
                .forEach((topicId, list) -> {
                    int size = list.size();
                    Posts root = list.get(size - 1);
                    root.classification(classifications.get(topicId));
                    root.categories();

                    postIds.add(root.getId());

                    Posts primary;
                    if (size > 1) {
                        primary = list.get(0);
                        primary.setParent(root);
                        postIds.add(primary.getId());
                    } else {
                        primary = root;
                    }
                    result.put(topicId, primary);
                });

        // [posts_text] find text
        Services.findAll(PostsText.class, Restrictions.in(BaseEntity.ID, postIds.toArray()))
                .forEach(text -> postsMap.get(text.getId()).text(text));
        return result;
    }

    /**
     * <p>产品比较</p>
     *
     * @param ids doc id
     * @return Response
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    @Override
    public Response compare(Set<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }

        // [search and sorted]
        Map<Object, Integer> index = new HashMap<>(16);
        int i = 0;
        for (String id : ids) {
            index.put(id, i++);
        }
        List<Map<String, Object>> hits = AbstractSearchEngine.hits(searchService.search(Topics.class, ids));
        hits.sort(Comparator.comparing(o -> index.get(o.get(AbstractSearchEngine.META_FIELD_ID))));

        // [merge]
        List<Map<String, Object>> features = new ArrayList<>();                     // 存放所有记录的参数MAP
        List<Map<String, Object>> prices = new ArrayList<>();                       // 存放所有记录的价格MAP
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> hit : hits) {
            // get meta data
            Long id = Long.valueOf((String) hit.get(AbstractSearchEngine.META_FIELD_ID));
            Map<String, Object> source = (Map<String, Object>) hit.get(AbstractSearchEngine.META_FIELD_SOURCE);
            if (!source.containsKey("features")) {
                source.put("features", Collections.emptyList());
            }

            // reduce feature list to the map
            features.add(((List<Map<String, Object>>) source.get("features"))
                    .stream()
                    .reduce(new LinkedHashMap<>(), (left, right) -> {

                        Map<String, Object> multi = (Map<String, Object>) Multilingual.getValue(right, 0, Multilingual.DEFAULT_LANGUAGES);
                        if (multi != null) {
                            String key = (String) multi.get("_name");
                            if (key != null && !"".equals(key = key.trim())) {
                                left.put(key, multi.get("_value"));
                            }
                        }

                        return left;
                    }));

            // reduce price list to the map and merge it
            prices.add(((List<Map<String, Object>>) source.get("items"))
                    .stream()
                    .reduce(new LinkedHashMap<>(), (left, right) -> {

                        String seedId = right.get("seedId").toString();
                        Double price = (Double) right.get("price");
                        String priceUnit = (String) right.get("priceUnit");
                        if (price != null) {
                            left.put(seedId, String.format("%s %.2f", priceUnit == null ? "" : priceUnit, price));
                        }

                        return left;
                    }));

            // put data
            Map<String, Object> data = Topics.convert(source, null);
            data.put("id", id);
            result.add(data);
        }

        Responses.DefaultBuilder builder = Responses.builder();
        int size = result.size();
        if (size > 0) {
            builder.page(new PageImpl<>(result, Pages.builder().page(1).size(size).build(), size), null, null);
        }

        builder.meta("features", Posts.merge(features));
        if (SecurityUtils.getSubject().isAuthenticated()) {
            builder.meta("prices", Posts.merge(prices, k -> Services.findOne(Seeds.class, Long.valueOf(k)).getSource()));
        } else {
            builder.meta("prices", Collections.emptyList());
        }
        return builder.data(result);
    }


    /**
     * <p>根据主题ID查找附件</p>
     *
     * @param topicId Topic ID
     * @return Attachments
     */
    @Transactional(readOnly = true)
    @Override
    public Map<Attachments.ContentType, List<Attachments>> findAttachment(Long topicId) {
        Set<Long> set = Services.findAll(TopicsAttachments.class,
                Restrictions.and(
                        Restrictions.eq(TopicsAttachments.TOPIC_ID, topicId),
                        Restrictions.eq(TopicsAttachments.ENABLED, 1)),
                Fields.builder().add(TopicsAttachments.ATTACHMENT_ID).build()).stream().map(TopicsAttachments::getAttachmentId).collect(toSet());

        if (set.isEmpty()) {
            return Collections.emptyMap();
        }

        return attachments(set);
    }

    @Transactional(readOnly = true)
    @Override
    public Map<Long, Map<Attachments.ContentType, List<Attachments>>> findAttachment(Set<Long> topicIds) {
        // topic ID -> attachment ID
        Map<Long, Set<Long>> mapping = Services.findAll(TopicsAttachments.class,
                Restrictions.and(
                        Restrictions.in(TopicsAttachments.TOPIC_ID, topicIds.toArray()),
                        Restrictions.eq(TopicsAttachments.ENABLED, 1)),
                Fields.builder()
                        .add(TopicsAttachments.TOPIC_ID)
                        .add(TopicsAttachments.ATTACHMENT_ID)
                        .build()).stream().collect(Collectors.groupingBy(TopicsAttachments::getTopicId, mapping(TopicsAttachments::getAttachmentId, toSet())));

        if (mapping.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Map<Attachments.ContentType, List<Attachments>>> result = new HashMap<>(16);
        attachments(mapping.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(toSet()))
                .forEach((contentType, attachments) -> mapping
                        .forEach((topicId, set) -> attachments.stream().filter(attachment -> set.contains(attachment.getId()))
                                .forEach(attachment -> {

                                    if (!result.containsKey(topicId)) {
                                        result.put(topicId, new EnumMap<>(Attachments.ContentType.class));
                                    }

                                    Map<Attachments.ContentType, List<Attachments>> map = result.get(topicId);

                                    if (!map.containsKey(contentType)) {
                                        map.put(contentType, new ArrayList<>());
                                    }

                                    map.get(contentType).add(attachment);
                                })
                        )
                );
        return result;
    }

    @Override
    public List<Topics> findHotKeywords(int product) {
        List<Topics> hots = new ArrayList<Topics>();
        int size = 0;
        LocalDate today = LocalDate.now();
        String mydate = today.minusWeeks(2).toString();
        if (product != 0) {
            List<Topics> microphones = baseDao.findHotKeywordForMicrophonesByReviews(product, 20, 3,mydate);
            hots.addAll(microphones);
            size = 3 - microphones.size();
            if (size > 0) {
                hots.addAll(baseDao.findHotKeywordForMicrophonesByQuality(product, size));
            }
        }
        size = 10 - hots.size();
        List<Topics> others = baseDao.findHotKeywordForOthersByReviews(product, 20, size,mydate);
        hots.addAll(others);
        size = 10 - hots.size();
        if (size > 0) {
            hots.addAll(baseDao.findHotKeywordForOthersByQuality(product, size));
        }
        hots.forEach(t -> t.setTitle(t.getTitle().trim()));
        return hots;
    }


    /**
     * <p>保存或更新</p>
     * <ol>
     * <li>爬取的数据保存  更新</li>
     * <li>人工新增数据保存 更新</li>
     * <li>数据修改保存 更新</li>
     * </ol>
     * <p>
     * <ul>
     * <li>POST语义</li>
     * <li>PATCH语义</li>
     * </ul>
     *
     * @param entity entity
     * @param <S>    S
     * @return effective rows
     */
    @Override
    public <S extends Topics> int save(S entity) {
        int n;
        TopicsFetch from = new TopicsFetch();
        if (entity.getTitle() != null) {
            entity.setTitleHash(EntityUtils.asLong(entity.getTitle()));
        }
        //把created 变成postdate 这个时间
        if (entity.getDocument() != null && entity.getDocument().getPostDate() != null) {
            entity.setCreated(entity.getDocument().getPostDate());
        }
        //判断是否是公众号文章 true为是公众号文章  false是非公众号文章
        if (entity.getOfficialAccounts() != null && "true".equals(entity.getOfficialAccounts())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //由于公众号的文章链接每次都是要变化的，所以再次爬取的时候无法通过origin 和origin_hash来进行区分了，所以只能走其他的路线
            //目前是通过 文章的title和title_hash进行区分，然后再通过日期区分，这样一样的概率就变得很小了
            List<Topics> topicsList = Services.findAll(Topics.class,
                    Restrictions.and(
                            Restrictions.eq(Topics.TITLE, entity.getTitle()),
                            Restrictions.eq(Topics.TITLE_HASH, entity.getTitleHash()),
                            Restrictions.eq(Topics.ENABLED, 1)
                    ),
                    Fields.builder().add(Topics.ID).add(Topics.CREATED).build());
            int size = topicsList.size();
            //查到0个 说明没有，直接保存
            // 或者1个 说明有一个，但是日期也不一样，直接保存
            if (size == 0 || (size == 1 && !sdf.format(topicsList.get(0).getCreated()).equals(sdf.format(entity.getDocument().getPostDate())))) {
                //先存入topics
                Date date = new Date();
                entity.setModified(date);
                //文章中 标题 或内容为空的时候 ，enabled=0
                if ((entity.getForumId() == 3 || entity.getForumId() == 4) && entity.getPost() != null && (entity.getPost().getContent() == null || entity.getPost().getTitle() == null)) {
                    entity.setEnabled(0);
                }
                n = baseDao.save(entity);
                if (n > 0) {
                    TopicsClassification classification = new TopicsClassification(entity.getId());
                    Services.save(TopicsClassification.class, classification);
                }
                //不能设置topic_from为空

            } else if (size >= 1) {
                topicsList = topicsList.stream().filter(topics -> sdf.format(topics.getCreated()).equals(sdf.format(entity.getDocument().getPostDate()))).collect(Collectors.toList());
                if (topicsList.size() == 0) {
                    //先存入topics
                    Date date = new Date();
                    entity.setModified(date);
                    //文章中 标题 或内容为空的时候 ，enabled=0
                    if ((entity.getForumId() == 3 || entity.getForumId() == 4) && entity.getPost() != null && (entity.getPost().getContent() == null || entity.getPost().getTitle() == null)) {
                        entity.setEnabled(0);
                    }
                    n = baseDao.save(entity);
                    if (n > 0) {
                        TopicsClassification classification = new TopicsClassification(entity.getId());
                        Services.save(TopicsClassification.class, classification);
                    }
                } else {
                    entity.setId(topicsList.get(0).getId());
                    entity.setFrom(null);
                }
            }

        } else {
            //如果不是公众号的文章判断重复还是通过TopicFetch表中的origin 和 origin_hash进行判断的
            // [fetch] 判断数据是否存在于fetch中
            from = entity.getFrom();
            if (from != null && from.getOrigin() != null) {
                from.setOriginHash(EntityUtils.asLong(from.getOrigin()));

                // 不要使用from作为查询条件, 显示设置查询条件
                TopicsFetch one = new TopicsFetch();
                one.setSeedId(from.getSeedId());
                one.setOrigin(from.getOrigin());
                one.setOriginHash(from.getOriginHash());
                one = Services.findOne(TopicsFetch.class, one, Fields.builder().add(TopicsFetch.TOPIC_ID).add(TopicsFetch.ID).build());
                if (one != null) {
                    one.setReviews(from.getReviews());
                    Long topicId = one.getTopicId();
                    entity.setId(topicId);
                    Services.update(TopicsFetch.class, one);
                    // 2017/9/14 这个不能删除，因为如果这个不设置为null，就会在后面继续保存一条，虽然在线下分类需要使用，建议在最下面再重新塞入一次，或者在其他地方再查询一次获得
                    entity.setFrom(null);
                }
            } else {
                entity.setFrom(null);
            }
        }
        // [topics]
        if (entity.getId() != null) {
            n = super.update(entity);
        } else {
            Date date = new Date();
            //文章中 标题 或内容为空的时候 ，enabled=0
            if ((entity.getForumId() == 3 || entity.getForumId() == 4) && entity.getPost() != null && (entity.getPost().getContent() == null || entity.getPost().getTitle() == null)) {
                entity.setEnabled(0);
            }

            //存产品 的时候 没有时间
            if (entity.getCreated() == null) {
                entity.setCreated(date);
            }
            entity.setModified(date);
            n = baseDao.save(entity);
            if (n > 0) {
                TopicsClassification classification = new TopicsClassification(entity.getId());
                Services.save(TopicsClassification.class, classification);
            }
        }
        if (n == 0) {
            throw new IllegalStateException("保存主题失败");
        }

        // [seedId] 不为空说明用户选择了来源频道，是用户的原创，需要手动编造一个fetch
        // 如果seedId不为null，则需要构建该主题的来源频道关联信息
        if (entity.getSeedId() != null) {
            int m = Services.update(Seeds.class, new Seeds(entity.getSeedId()));
            if(m == 0) {
                throw new IllegalStateException("seedId不存在！");
            }
            if (entity.getFrom() == null) {
                TopicsFetch fetch = new TopicsFetch();
                fetch.setSeedId(entity.getSeedId());
                fetch.setOrigin("http://www.mifanxing.com/p/" + entity.getId());
                fetch.setOriginHash(EntityUtils.asLong(fetch.getOrigin()));
                entity.setFrom(fetch);
            }
        }

        // [fetch] 不为空说明在数据库中没有匹配到, 是新的来源, 直接保存
        from = entity.getFrom();
        if (from != null) {
            from.setTopicId(entity.getId());
            from.setOriginHash(EntityUtils.asLong(from.getOrigin()));
            Services.saveOrUpdate(TopicsFetch.class, from);
        }

        // [attachments] 保存与附件的关联关系
        List<Attachments> attachments = entity.getAttachments();
        if (attachments != null) {
            Map<Long, Map<String, Object>> data = new HashMap<>(16);
            for (Long attachmentId : attachments.stream().map(Attachments::getId).collect(Collectors.toSet())) {
                data.put(attachmentId, null);
            }
            // if attachments is empty reset relationship, otherwise update it.
            Services.doToManyPatch(Topics.class, entity.getId(), Topics.RELATIONSHIPS_ATTACHMENTS, data, false);
        }

        // [posts] 保存内容信息
        Posts post = entity.getPost();
        notNull(post);
        post.setTopicId(entity.getId());
        n += Services.save(Posts.class, post);

        // [product] 保存产品, 产品历史价格信息
        TopicsProduct product = entity.getProduct();
        if (product != null) {
            product.setTopicId(entity.getId());
            n += Services.save(TopicsProduct.class, product);
        }

        // [document] 保存文档信息
        TopicsDocument document = entity.getDocument();
        if (document != null) {
            document.setTopicId(entity.getId());
            n += Services.save(TopicsDocument.class, document);
            // TODO save brand map if not exists
        }

        // [brand] 保存品牌信息
        TopicsBrand brand = entity.getTopicsBrand();
        if (brand != null) {
            brand.setTopicId(entity.getId());
            n += Services.save(TopicsBrand.class, brand);
        }

        //[category] 保存分类信息
        Long categoryId = entity.getCategoryId();
        if (categoryId != null && categoryId > 0L && !Services.exists(ForumCategories.class, categoryId)) {
            throw new IllegalStateException("不存在该类别ID");
        }
        
        // [tune] 保存美频信息 TODO
        /***************** 新版本删除这段代码 ***********************/
        TopicsTune tune = entity.getTune();
        if (tune != null) {
            tune.setTopicId(entity.getId());
            n += Services.save(TopicsTune.class, tune);
        }
        /***************** 新版本删除这段代码 ***********************/
        // [mp] 保存美频信息
        TopicsMp mp = entity.getMp();
        if (mp != null) {
        	mp.setTopicId(entity.getId());
            n += Services.saveOrUpdate(TopicsMp.class, mp);
        }
        List<TopicsMpdownloads> topicsMpdownloads = entity.getTopicsMpdownloads();
        if(!CollectionUtils.isEmpty(topicsMpdownloads)) {
        	topicsMpdownloads.forEach(tmd -> tmd.setTopicId(entity.getId()));
        	n += topicsMpdownloadsService.saveTopicsMpdownloads(topicsMpdownloads);
        }

        return n;
    }

    @Override
    public <S extends Topics> int update(S entity) {
        Long categoryId = entity.getCategoryId();
        if (categoryId != null && categoryId > 0L && !Services.exists(ForumCategories.class, categoryId)) {
            throw new IllegalStateException("不存在该类别ID");
        }

        int n = super.update(entity);
        if (entity.getReplies() != null) {
            topicsCache.put(entity.getId(), TopicsCache.Statistics.REPLIES, entity.getReplies());
        }
        return n;
    }

    private Map<Attachments.ContentType, List<Attachments>> attachments(Set<Long> ids) {
        return Services.findAll(Attachments.class,
                Restrictions.and(
                        Restrictions.in(Attachments.ID, ids.toArray()),
                        Restrictions.eq(Attachments.ENABLED, 1),
                        Restrictions.gt(Attachments.GROUP_ID, 0)),          // important
                Attachments.DEFAULT_FIELDS)
                .stream()
                .collect(Collectors.groupingBy(
                        attach -> Attachments.MediaType.from(attach.getMime(), attach.getGroupId()).getContentType(),
                        () -> new EnumMap<>(Attachments.ContentType.class),
                        toList()));
    }

    private Posts findParent(Long topicId) {
        Posts parent = new Posts();
        parent.setTopicId(topicId);
        parent.setEnabled(1);
        parent.setParentId(0L);
        parent = Services.findOne(Posts.class, parent);
        if (parent == null) {
            return null;
        }
        if (parent.getCreator() == 0L) {
            // [classification] use classification is exists
            TopicsClassification classification = Services.findOne(TopicsClassification.class, topicId);
            if (classification.getEnabled() == 0) {
                return null;
            }
            parent.classification(classification);
            parent.categories();
        }
        return parent;
    }

    private void SetTopicFrom(Long id, Topics topic) {
        TopicsFetch fetch = new TopicsFetch();
        fetch.setTopicId(id);
        fetch = Services.findOne(TopicsFetch.class, fetch);
        if(fetch != null) {
            topic.setFrom(fetch);
            topic.setReviews(topic.getFrom().getReviews() + topic.getReviews());
        }else {
            if(topic.getCreator() != null && topic.getCreator() > 0) {
                from(topic);
            }
        }
    }

    private void from(Topics topic) {
        // UserProfiles profile = Services.findOne(UserProfiles.class, topic.getCreator(), UserProfiles.AVATAR_FIELDS);
        Channels channel = Services.findOne(Channels.class,
                Restrictions.and(
                        Restrictions.eq(Channels.TARGET_ID, topic.getCreator()),
                        Restrictions.eq(Channels.CHANNEL_TYPE, Channels.ChannelType.USER.getIndex())),
                Fields.builder()
                        .add(Channels.ID)
                        .add(Channels.CHANNEL_NAME)
                        .add(Channels.CHANNEL_IMAGE)
                        .build());
        if (channel != null) {
            TopicsFetch fetch = new TopicsFetch();
            fetch.setChannelId(channel.getId());
            fetch.setSource(channel.getChannelName());
            fetch.setImage(channel.getChannelImage());
            fetch.setReviews(topic.getReviews());
            topic.setFrom(fetch);
        }
    }

}
