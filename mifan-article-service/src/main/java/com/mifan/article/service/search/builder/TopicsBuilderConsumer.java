package com.mifan.article.service.search.builder;

import com.mifan.article.domain.*;
import com.mifan.article.domain.support.Brands;
import com.mifan.article.domain.support.Multilingual.Language;
import com.mifan.article.service.TopicsService;
import com.mifan.article.service.search.suggest.Suggestions;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.moonframework.core.util.ObjectMapperFactory;
import org.moonframework.crawler.parse.Optimizer;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/18
 */
public class TopicsBuilderConsumer implements BuilderConsumer<XContentBuilder, Topics> {

    public static final String FORUM_ID = "forumId";
    public static final String TOPIC_TYPE = "topicType";
    public static final String POST_TYPE = "postType";
    public static final String IMAGE_SIZE = "imageSize";
    public static final String IMAGE_SINGLE = "imageSingle";
    public static final String IMAGE_ROTATION = "imageRotation";
    public static final String CREATOR = "creator";
    public static final String CREATED = "created";
    public static final String MODIFIED = "modified";
    public static final String IMAGES = "images";
    public static final String IMAGES_FILENAME = IMAGES + ".filename";
    public static final String ROTATIONS = "rotations";
    public static final String ROTATIONS_FILENAME = ROTATIONS + ".filename";
    public static final String FEATURES = "features";
    public static final String BRAND = "brand";
    public static final String CATEGORIES = "categories";
    public static final String TAGS = "tags";

    //
    public static final String SEED_ID = "seedId";
    public static final String CHANNEL_ID = "channelId";
    public static final String ORIGIN = "origin";
    public static final String PRICE = "price";
    public static final String PRICE_UNIT = "priceUnit";
    public static final String SALE_RANK = "saleRank";
    public static final String RATING = "rating";
    public static final String AUTHOR = "author";
    public static final String POST_DATE = "postDate";
    public static final String REVIEWS = "reviews";
    public static final String TYPE = "type";
    public static final String UP_TIMES = "upTimes";
    public static final String MP_CATEGORY_ID = "mpCategoryId";
    
    // titles
    public static final String TITLES = "titles";
    public static final String TITLES_EN = "titles.en";
    public static final String TITLES_CN = "titles.cn";

    // descriptions
    public static final String DESCRIPTIONS = "descriptions";
    public static final String DESCRIPTIONS_EN = "descriptions.en";
    public static final String DESCRIPTIONS_CN = "descriptions.cn";

    // contents
    public static final String CONTENTS = "contents";
    public static final String CONTENTS_EN = "contents.en";
    public static final String CONTENTS_CN = "contents.cn";

    public static final String ITEMS = "items";
    public static final String IMAGES_NESTED_DISTANCE = "distance";

    // 动态字段
    public static final String LEVEL = "level_";

    public static final String[] FIELD_LIST = {FORUM_ID, TOPIC_TYPE, POST_TYPE, IMAGE_SINGLE, IMAGE_ROTATION, CREATOR, CREATED, MODIFIED, IMAGES_FILENAME, ROTATIONS_FILENAME, FEATURES, BRAND, CATEGORIES, TAGS, ITEMS + ".*"};
    public static final String[] FIELD_MLT = new String[]{"items.titles.en", "items.titles.cn", "items.contents.en", "items.contents.cn"};

    private static Log logger = LogFactory.getLog(TopicsBuilderConsumer.class);
    private static LocalDateTime start = LocalDateTime.of(1970, 1, 1, 0, 0);

    /**
     *
     */
    private BitSet bitSet = new BitSet();

    private TopicsService topicsService;

    public TopicsBuilderConsumer(TopicsService topicsService) {
        this.topicsService = topicsService;
    }

    /**
     * 初始化位集合
     */
    @Override
    public void init() {
        bitSet.clear();
    }

    /**
     * <p>创建索引核心逻辑</p>
     *
     * @param builder content builder
     * @param topic   topic
     */
    @Override
    public void accept(XContentBuilder builder, Topics topic) {
        try {
            Posts post = topic.getPost();

            // images
            List<Attachments> images = topic.items().stream().flatMap(t -> t.getImages().stream()).collect(Collectors.toList());
            List<Attachments> rotations = topic.items().stream().flatMap(t -> t.getRotations().stream()).collect(Collectors.toList());
            int x = images.size();
            int y = rotations.size();
            int z = x + y;

            // categories
            List<Map<String, String>> categories = post.classification();
            if (categories == null) {
                categories = translation(
                        post,
                        Posts::getCategories,
                        Posts::getCategories);
            }

            // brands
            Brands brand = null;
            Map<String, Object> brandMap = null;
            if (topic.getProduct() != null && (brand = topic.getProduct().getBrandInfo()) != null) {
                brandMap = Brands.toMap(brand);
            }else if(topic.getDocument() != null) {
            	String brandName = topic.getDocument().getBrand();
            	if(!CollectionUtils.isEmpty(topic.getDocument().getBrands())) {
            		brandName = String.join(",",topic.getDocument().getBrands());
            	}
            	brandMap = new HashMap<>(6);
            	brandMap.put("id", 0L);
            	brandMap.put("name", null);
            	brandMap.put("aliasName",null);
            	brandMap.put("logo", null);
            	brandMap.put("rating", 0D);
            	brandMap.put("reviews", 0);
            	brandMap.put("top", false);
            	if(brandName != null && !"".equals(brandName.trim())) {
                    brandMap.put("name", brandName);
                    String aliasName = brandName.replaceAll("[^a-zA-Z0-9,]", "").toLowerCase();
                    brandMap.put("aliasName",aliasName);
                }
            }

            topic.setBoost(boost(topic, x, y));
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            builder
                    .field("boost", topic.getBoost())
                    .field("suggest", suggest(topic, images, categories, brand))
                    .field(FORUM_ID, topic.getForumId())                        // 产品,新闻,评测,视频
                    .field(TOPIC_TYPE, topic.getTopicType())                    // 正常,置顶,公告
                    .field(POST_TYPE, post.getPostType())                       // 原创,爬取,精翻,机翻
                    .field(IMAGE_SIZE, z)                                       // 图片数量
                    .field(IMAGE_SINGLE, topic.isImageSingle())                 // 是否只有一张图
                    .field(IMAGE_ROTATION, topic.isImageRotation())             // 是否有旋转图
                    .field(CREATOR, topic.getCreator())                         // 创建人
                    .field(CREATED, topic.getCreated(), dtf)                    // 创建时间
                    .field(MODIFIED, topic.getModified(), dtf)                  // 修改时间
                    .field(BRAND, brandMap)                                     // 品牌
                    .field(CATEGORIES, categories)                              // 分类
                    .array(TAGS, post.getTags().toArray())                      // 标签
                    .field(IMAGES, toList(images, true))                        // 图片
                    .field(ROTATIONS, toList(rotations, false))                 // 旋转图
                    .field(FEATURES, translation(                               // 参数
                            post,
                            Posts::getFeatures,
                            Posts::getFeatures,
                            true));
            similarities(builder, topic);                                       // 相似文档
            level(builder, categories);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * <p>查找相关对象</p>
     *
     * @param list list
     * @return page
     */
    @Override
    public List<Topics> list(List<Topics> list) {
        // FIXME 该方式冗余代码少, 实现简单, 但是效率比较慢. page中只查询待索引的资源ID, 根据ID查找详细信息再进行索引.
        return list
                .stream()
                .filter(topic -> !bitSet.get(topic.getId().intValue()))
                .peek(topic -> bitSet.set(topic.getId().intValue()))
                .map(topic -> topicsService.queryForObject(topic.getId(), null, true, false))
                .filter(Objects::nonNull)
                .peek(Topics::item)
                .peek(topic -> topic.items().forEach(t -> bitSet.set(t.getId().intValue())))
                // .peek(topic -> logger.info(() -> String.format("Index : %s", topic.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public String getIndex() {
        return "topic";
    }

    @Override
    public String getType() {
        return "post";
    }

    @Override
    public String getTitleFieldName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentFieldName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getIncludeListFields() {
        return FIELD_LIST;
    }

    @Override
    public String[] getMLTFields() {
        return FIELD_MLT;
    }

    @Override
    public String getTid() {
        return "1";
    }

    /**
     * <p>boost以天为基本单位进行计算, 从1970-01-01 00:00:00开始</p>
     * <ol>
     * <li>时间助推:从1970-01-01 00:00:00到创建时间的小时差值除以24</li>
     * <li>图片助推:如果包含普通图, 加[90]天助推数</li>
     * <li>图片助推:如果包含旋转图, 加[90]天助推数</li>
     * <li>人工助推:topic.boost</li>
     * <li>内容长度:</li>
     * </ol>
     *
     * @param topic topic
     * @param x     普通图片数量
     * @param y     旋转图片数量
     * @return 综合助推数
     */
    private Double boost(Topics topic, int x, int y) {
        double value = topic.getBoost() + (x > 0 ? 90 : 0) + (y > 0 ? 90 : 0);
        LocalDateTime created = LocalDateTime.ofInstant(topic.getCreated().toInstant(), ZoneId.systemDefault());
        if (created.isAfter(start)) {
            value += Duration.between(start, created).toHours() / 24D;
        }

        String s = topic.toString();
        if (s != null && s.length() > 140) {
            value += 20;
        }
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private Map<String, Object> suggest(Topics topic, List<Attachments> images, List<Map<String, String>> categories, Brands brand) {
        String title = topic.getTitle() == null ? "" : topic.getTitle().trim();
        String brandName = brand == null ? null : brand.getName();

        Map<String, Object> map = new HashMap<>(8);

        // input
        map.put("input", Suggestions.input(title));

        // output
        map.put("output", String.format("%s - %s", topic.getForumName(), title));

        // payload
        Map<String, Object> attributes = new HashMap<>(16);
        attributes.put("forumId", topic.getForumId());
        attributes.put("title", title);
        if (brandName != null) {
            attributes.put("brand", brandName);
        }
        if (!CollectionUtils.isEmpty(images)) {
            attributes.put("images", toList(images, false));
        }
        if (!CollectionUtils.isEmpty(categories)) {
            attributes.put("categories", categories);
        }
        Map<String, Object> data = new HashMap<>(16);
        data.put("type", "topics");
        data.put("id", topic.getId());
        data.put("attributes", attributes);
        Map<String, Object> payload = new HashMap<>(16);
        payload.put("data", data);
        map.put("payload", payload);

        // context
        Map<String, Object> context = new HashMap<>(16);
        context.put("forumId", context(topic.getForumId().toString()));
        context.put("category", context(categories));
        context.put("brand", context(brandName));
        map.put("context", context);

        // weight
        map.put("weight", topic.getBoost().intValue());
        return map;
    }

    private List<String> context(List<Map<String, String>> list) {
        List<String> result = context();
        list.forEach(map -> map.forEach((lan, cat) -> result.add(cat)));
        return result;
    }

    private List<String> context(String value) {
        List<String> result = context();
        if (value != null) {
            result.add(value);
        }
        return result;
    }

    private List<String> context() {
        List<String> result = new ArrayList<>();
        result.add("-");
        return result;
    }

    private <T> List<Map<String, T>> translation(Posts post, Function<Posts, List<T>> x, Function<Posts, List<T>> y) {
        return translation(post, x, y, false);
    }

    /**
     * <p>翻译处理</p>
     *
     * @param post post
     * @param x    x
     * @param y    y
     * @param <T>  T
     * @return result
     */
    private <T> List<Map<String, T>> translation(Posts post, Function<Posts, List<T>> x, Function<Posts, List<T>> y, boolean auto) {
        List<Map<String, T>> list = new ArrayList<>();
        List<T> en = null;
        List<T> cn = null;
        if (post.getParent() != null) {
            en = x.apply(post.getParent());
            cn = y.apply(post);
        } else {
            Integer language = post.getLanguage();
            if (Language.CN.getIndex() == language) {
                cn = y.apply(post);
            } else if (Language.EN.getIndex() == language) {
                en = y.apply(post);
            }
        }
        if (en == null) {
            en = Collections.emptyList();
        }
        if (cn == null) {
            cn = Collections.emptyList();
        }
        int size = Math.max(cn.size(), en.size());
        for (int i = 0; i < size; i++) {
            Map<String, T> feature = new HashMap<>(16);
            T e = i < en.size() ? en.get(i) : null;
            T c = i < cn.size() ? cn.get(i) : null;
            if (auto) {
                if (e == null && c != null) {
                    e = c;
                }
                if (c == null && e != null) {
                    c = e;
                }
            }
            feature.put(Language.EN.getName(), e);
            feature.put(Language.CN.getName(), c);
            list.add(feature);
        }
        return list;
    }

    private void similarities(XContentBuilder builder, Topics topic) throws IOException {
        List<Map<String, Object>> list = topic.items().stream().map(this::similarities).collect(Collectors.toList());
        builder.field(ITEMS, list);
    }

    private Map<String, Object> similarities(Topics topic) {
        Map<String, Object> item = new HashMap<>(16);

        TopicsFetch topicsFetch = topic.getFrom();
        if (topicsFetch != null) {
            item.put(SEED_ID, topicsFetch.getSeedId());
            item.put(ORIGIN, topicsFetch.getOrigin());
            item.put(RATING, topicsFetch.getRating());
            item.put(REVIEWS, topic.getReviews());

            // deprecated now
            // item.put(CHANNEL_ID, topicsFetch.getChannelId());
        }

        TopicsProduct product = topic.getProduct();
        if (product != null && product.getPrice() != null) {
            item.put(PRICE, product.getPrice().setScale(2, RoundingMode.HALF_UP).doubleValue());
            item.put(PRICE_UNIT, product.getPriceUnit());
            if(product.getSaleRank() != null) {
            	item.put(SALE_RANK, product.getSaleRank());
            }
        }

        TopicsDocument document = topic.getDocument();
        if (document != null) {
            item.put(AUTHOR, document.getAuthor());
            if (document.getPostDate() != null) {
                item.put(POST_DATE, DateTimeFormat.forPattern("yyyy-MM-dd").print(document.getPostDate().getTime()));
            }
        }
        
        TopicsMp mp = topic.getMp();
        if(mp != null) {
        	item.put(TYPE, mp.getType());
        	item.put(UP_TIMES, mp.getUpTimes());
        	item.put(MP_CATEGORY_ID, mp.getMpCategoryId());
        }

        // 索引原文与翻译, 翻译选择质量最好的一篇
        item.put(TITLES, translation(
                topic.getPost(),
                x -> Collections.singletonList(x.getTitle()),
                y -> Collections.singletonList(y.getTitle())));
        item.put(DESCRIPTIONS, translation(
                topic.getPost(),
                x -> Collections.singletonList(x.getDescription()),
                y -> Collections.singletonList(y.getDescription())));
        item.put(CONTENTS, translation(
                topic.getPost(),
                x -> Collections.singletonList(format(x.getContent())),
                y -> Collections.singletonList(format(y.getContent()))));
        return item;
    }

    private String format(String s) {
        s = Optimizer.removeHtml(s);
        if (s == null) {
            return null;
        }
        return s.replaceAll("&nbsp;", "").trim();
    }

    /**
     * <p>将attachments转换成待索引的list对象</p>
     *
     * @param list   list
     * @param format format
     * @return list
     */
    private List<Map<String, Object>> toList(List<Attachments> list, boolean format) {
        return list.stream().map(attachment -> toMap(attachment, format)).collect(Collectors.toList());
    }

    /**
     * <p>将attachment转换成待索引的map对象</p>
     *
     * @param attachment attachment
     * @param format     是否格式化颜色距离
     * @return map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(Attachments attachment, boolean format) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(Attachments.FILENAME, attachment.getFilename());
        if (format && attachment.getExtra() != null) {
            try {
                Map<String, Double> score = ObjectMapperFactory.readValue(attachment.getExtra(), Map.class);
                map.put(IMAGES_NESTED_DISTANCE, score);
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return map;
    }

    /**
     * <p>dynamic templates fields for categories</p>
     *
     * @param builder    content builder
     * @param categories categories
     * @throws IOException exception
     */
    private void level(XContentBuilder builder, List<Map<String, String>> categories) throws IOException {
        for (int i = 0; i < categories.size(); i++) {
            builder.field(LEVEL + i, categories.get(i));
        }
    }

}
