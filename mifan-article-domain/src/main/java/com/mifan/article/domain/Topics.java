package com.mifan.article.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mifan.article.domain.support.Currency;
import com.mifan.article.domain.support.Multilingual;
import com.mifan.article.domain.support.ValidationGroups.BrandPost;
import com.mifan.article.domain.support.ValidationGroups.MpPost;
import com.mifan.article.domain.support.ValidationGroups.NewsPost;
import com.mifan.article.domain.support.ValidationGroups.ProductPost;
import com.mifan.article.domain.support.ValidationGroups.VideosPost;
import org.apache.shiro.SecurityUtils;
import org.hibernate.validator.constraints.Range;
import org.moonframework.elasticsearch.Searchable;
import org.moonframework.model.mybatis.annotation.OneToMany;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.*;
import java.util.function.Consumer;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class Topics extends BaseEntity implements Searchable {

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(Topics.ID)
            .add(Topics.TITLE)
            .add(Topics.FORUM_ID)
            .add(Topics.CATEGORY_ID)
            .add(Topics.TOPIC_TYPE)
            .add(Topics.REVIEWS)
            .add(Topics.REPLIES)
            .add(Topics.THUMBS_UP)
            .add(Topics.THUMBS_DOWN)
            .add(Topics.BOOST)
            .add(Topics.TRAIN_SAMPLE)
            .add(Topics.LOCKED)
            .add(Topics.MODERATED)
            .add(Topics.CREATOR)
            .add(Topics.MODIFIER)
            .add(Topics.CREATED)
            .add(Topics.MODIFIED).build();

    public static Iterable<? extends Field> STATISTICS_FIELDS = Fields.builder()
            .add(Topics.ID)
            .add(Topics.REVIEWS)
            .add(Topics.REPLIES)
            .add(Topics.THUMBS_UP)
            .add(Topics.THUMBS_DOWN)
            .build();

    public static final String RELATIONSHIPS_ATTACHMENTS = "images";

    public static final String TABLE_NAME = "topics";

    public static final String FORUM_ID = "forum_id";
    public static final String CATEGORY_ID = "category_Id";
    public static final String TITLE = "title";
    public static final String TITLE_HASH = "title_hash";
    public static final String TOPIC_TYPE = "topic_type";
    public static final String REVIEWS = "reviews";
    public static final String REPLIES = "replies";
    public static final String THUMBS_UP = "thumbs_up";
    public static final String BOOST = "boost";
    public static final String TRAIN_SAMPLE = "train_sample";
    public static final String THUMBS_DOWN = "thumbs_down";
    public static final String LOCKED = "locked";
    public static final String MODERATED = "moderated";

    private static final long serialVersionUID = 5637926262411208503L;

    @OneToMany(value = RELATIONSHIPS_ATTACHMENTS,
            targetEntity = TopicsAttachments.class,
            mappedBy = TopicsAttachments.TOPIC_ID,
            mappedTo = TopicsAttachments.ATTACHMENT_ID)
    private Long id;

    @NotNull(groups = {MpPost.class},message = "{NotNull.Topics.forumId}")
    @Range(groups = {MpPost.class}, message = "{Error.Topics.forumId}", min = 6,max = 8)
    private Long forumId;
    private Long categoryId;
    private String title;
    private Long titleHash;
    private Integer topicType;
    private Integer reviews;
    private Integer replies;
    private Integer thumbsUp;
    private Integer thumbsDown;
    private Double boost;

    @Range(groups = {Post.class, Patch.class}, message = "{Range.Topics.trainSample}", max = 1)
    private Integer trainSample;

    @Range(groups = {Post.class, Patch.class}, message = "{Range.Topics.locked}", max = 1)
    private Integer locked;

    @Range(groups = {Post.class, Patch.class}, message = "{Range.Topics.moderated}", max = 1)
    private Integer moderated;

    private String categoryPath;                                        // 分类树级路径
    private String docId;                                               // 文档ID
    private List<Attachments> attachments;                              // 附件
    private List<Topics> similarities;                                  // 相同产品集合, 包含自己, 方便按内容完整度排序显示

    private String image;                                               // primary image
    private List<Attachments> rotations;                                // 360图
    private List<Attachments> images;                                   // 图片
    private List<Attachments> audios;                                   // 音频
    private List<Attachments> videos;                                   // 视频

    // private Map<String, List<? extends BaseEntity>> relationships;   // 附件:按类型聚合的
    @Null(groups = {BrandPost.class, ProductPost.class, NewsPost.class, VideosPost.class,MpPost.class}, message = "{MustNull.topics.from}")
    private TopicsFetch from;                                            // 来源相关数据
    @Null(groups = {BrandPost.class, NewsPost.class, VideosPost.class,MpPost.class}, message = "{MustNull.topics.product}")
    private TopicsProduct product;                                      // 产品数据
    @Null(groups = {BrandPost.class, ProductPost.class}, message = "{MustNull.topics.document}")
    private TopicsDocument document;                                    // 新闻与评测数据.
    private TopicsBrand topicsBrand;                                         //品牌
    @NotNull(groups = MpPost.class,message = "{NotNull.topics.mp}")
    @Valid
    private TopicsMp mp;												//美频相关
    private List<MpDownloads> mpdownloads;								//美频相关下载
    private List<TopicsMpdownloads> topicsMpdownloads;					//美频相关下载关联
    private TopicsTune tune;//旧相关 TODO，新版本可删除
    

    private boolean imageSingle;                                        // 是否是单图
    private boolean imageRotation;                                      // 是否有360度旋转图
    private String officialAccounts;                                  // 是否是公众号文章 true是公众号文章

    @NotNull(groups = MpPost.class,message = "{NotNull.topics.seedId}")
    private Long seedId;//来源频道
    private String shortTitle;//短标题


    // 统计相关数据
    private int liked;
    private int favorite;
    private List<Topics> items;

    /**
     * 优先级从高到低
     * <ul>
     * <li>原创</li>
     * <li>精翻</li>
     * <li>机翻</li>
     * <li>爬取</li>
     * </ul>
     */
    @NotNull(groups = {BrandPost.class, ProductPost.class, VideosPost.class, NewsPost.class,MpPost.class}, message = "{NotNull.topics.post}")
    @Valid
    private Posts post;

    public Topics() {
    }

    public Topics(Long id) {
        super(id);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> convert(Map<String, Object> source, Consumer<Map<String, Object>> consumer) {
        Long forumId = ((Integer) source.get("forumId")).longValue();
        Integer topicType = (Integer) source.get("topicType");

        ForumType forumType = ForumType.from(forumId);

        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("forumId", forumId);
        attributes.put("forumName", forumType == null ? "" : forumType.getName());
        attributes.put("topicType", topicType);
        attributes.put("topicTypeValue", Topics.TopicType.from(topicType).getName());
        attributes.put("imageSingle", source.get("imageSingle"));
        attributes.put("imageRotation", source.get("imageRotation"));
        attributes.put("creator", source.get("creator"));
        attributes.put("created", source.get("created"));
        attributes.put("modified", source.get("modified"));

        //
        if (consumer != null) {
            consumer.accept(attributes);
        }

        // brand
        attributes.put("brand", source.get("brand"));

        // attach
        attributes.put("rotations", source.get("rotations"));
        attributes.put("images", source.get("images"));

        // post
        List<Map<String, Object>> items = (List<Map<String, Object>>) source.get("items");
        if (!items.isEmpty()) {
            item(forumId, source, attributes, items.get(0));
        }

        // similarities
        List<Map<String, Object>> similarities = new ArrayList<>();
        for (int i = 1; i < items.size(); i++) {
            Map<String, Object> similarity = new LinkedHashMap<>();
            item(forumId, source, similarity, items.get(i));
            similarities.add(similarity);
        }
        attributes.put("similarities", similarities);
        return attributes;
    }

    @SuppressWarnings("unchecked")
    private static void item(Long forumId, Map<String, Object> source, Map<String, Object> attributes, Map<String, Object> item) {
        // origin
        Integer seedId = (Integer) item.get("seedId");
        if (seedId != null) {
            Seeds seed = Services.findOne(Seeds.class, seedId.longValue());
            Seeds.putSource(attributes, seed, from -> {
                from.put("origin", item.get("origin"));
                from.put("rating", item.get("rating"));
                from.put("reviews", item.get("reviews"));
            });
        }

        // content
        Integer postType = (Integer) source.get("postType");
        Map<String, Object> post = new LinkedHashMap<>();
        post.put("postType", postType);
        post.put("postTypeValue", Posts.PostType.from(postType).getName());
        post.put("title", Multilingual.getValue((List<Map<String, Object>>) item.get("titles"), Multilingual.DEFAULT_LANGUAGES));
        post.put("description", Multilingual.getValue((List<Map<String, Object>>) item.get("descriptions"), Multilingual.DEFAULT_LANGUAGES));
        post.put("categories", Multilingual.getList((List<Map<String, Object>>) source.get("categories"), Multilingual.DEFAULT_LANGUAGES));
        Object tags = source.get("tags");
        if (tags != null) {
            post.put("tags", tags);
        }
        attributes.put("post", post);

        //
        if (Topics.ForumType.PRODUCT.getIndex() == forumId) {
            if (SecurityUtils.getSubject().isAuthenticated()) {
                Map<String, Object> product = new LinkedHashMap<>();

                String priceUnit = (String) item.get("priceUnit");
                Currency currency = Currency.from(priceUnit);

                product.put("price", item.get("price"));
                product.put("priceUnit", priceUnit);
                product.put("priceSign", currency == null ? null : currency.getSign());
                product.put("saleRank", item.get("saleRank"));

                attributes.put("product", product);
            }
        } else if (Topics.document(forumId)) {
            Map<String, Object> document = new LinkedHashMap<>();
            document.put("postDate", item.get("postDate"));
            document.put("author", item.get("author"));
            attributes.put("document", document);
        }
        if (Topics.ForumType.MPSUPPORT.getIndex() == forumId ||
        		Topics.ForumType.ANCHOR.getIndex() == forumId ||
        		Topics.ForumType.MPNEWS.getIndex() == forumId) {
        	Map<String, Object> mp = new LinkedHashMap<>();
        	mp.put("type", item.get("type"));
        	mp.put("upTimes", item.get("upTimes"));
        	mp.put("mpCategoryId", item.get("mpCategoryId"));
        	attributes.put("mp", mp);
        }
    }

    /**
     * <p>设置主题附件</p>
     *
     * @param attachments 按附件类型进行分组的附件集合
     */
    public void attachments(Map<Attachments.ContentType, List<Attachments>> attachments) {
        List<Attachments> rotations = null;
        List<Attachments> images = null;
        List<Attachments> audios = null;
        List<Attachments> videos = null;
        if (!attachments.isEmpty()) {
            rotations = attachments.get(Attachments.ContentType.ROTATIONS);
            images = attachments.get(Attachments.ContentType.IMAGES);
            audios = attachments.get(Attachments.ContentType.AUDIOS);
            videos = attachments.get(Attachments.ContentType.VIDEOS);

            if (images != null) {
                int size = images.size();
                if (size > 0) {
                    setImage(images.get(0).getFilename());
                }
                if (size == 1) {
                    setImageSingle(true);
                }
            }
        }
        setImageRotation(!CollectionUtils.isEmpty(rotations));
        setRotations(rotations == null ? Collections.EMPTY_LIST : rotations);
        setImages(images == null ? Collections.EMPTY_LIST : images);
        setAudios(audios == null ? Collections.EMPTY_LIST : audios);
        setVideos(videos == null ? Collections.EMPTY_LIST : videos);
    }

    /**
     * <p>转换成简单文本</p>
     *
     * @return a simple classifiable text
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (product != null && product.getBrand() != null) {
            sb.append(product.getBrand());
            sb.append(", ");
        }

        if (post != null) {
            sb.append(post.toString());
        } else {
            sb.append(title);
        }

        return sb.toString();
    }

    public static boolean document(Long forumId) {
        return forumId != null &&
                (ForumType.NEWS.getIndex() == forumId ||
                        ForumType.EVALUATION.getIndex() == forumId ||
                        ForumType.VIDEOS.getIndex() == forumId) ||
                        ForumType.ANCHOR.getIndex() == forumId ||
                        ForumType.MPSUPPORT.getIndex() == forumId ||
                        ForumType.MPNEWS.getIndex() == forumId;
    }

    public boolean document() {
        return document(forumId);
    }

    /**
     * @return
     */
    public Integer getThumbs() {
        if (thumbsUp != null && thumbsDown != null) {
            return thumbsUp - thumbsDown;
        }
        return null;
    }

    public String getTopicTypeValue() {
        TopicType topicType = TopicType.from(this.topicType);
        return topicType == null ? null : topicType.getName();
    }

    public String getForumName() {
        ForumType forumType = ForumType.from(forumId);
        return forumType == null ? null : forumType.getName();
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    @JsonIgnore
    @Override
    public String getDocId() {
        if (docId != null) {
            return docId;
        }
        return getId() == null ? null : getId().toString();
    }

    public void item() {
        items = new ArrayList<>();
        items.add(this);
        if (!CollectionUtils.isEmpty(similarities)) {
            items.addAll(similarities);
        }
    }

    public TopicsBrand getTopicsBrand() {
        return topicsBrand;
    }

    public void setTopicsBrand(TopicsBrand topicsBrand) {
        this.topicsBrand = topicsBrand;
    }

    public List<Topics> items() {
        return items;
    }

    public List<Attachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachments> attachments) {
        this.attachments = attachments;
    }

    public List<Topics> getSimilarities() {
        return similarities;
    }

    public void setSimilarities(List<Topics> similarities) {
        this.similarities = similarities;
    }

    /**
     * @return 版面ID
     */
    public Long getForumId() {
        return forumId;
    }

    /**
     * @param forumId 版面ID
     */
    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }

    /**
     * <p>人工标注的类别ID</p>
     *
     * @return category ID
     */
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return 标题HASH
     */
    public Long getTitleHash() {
        return titleHash;
    }

    /**
     * @param titleHash 标题HASH
     */
    public void setTitleHash(Long titleHash) {
        this.titleHash = titleHash;
    }

    /**
     * @return 主题类型{0:正常, 1:置顶, 2:公告}
     */
    public Integer getTopicType() {
        return topicType;
    }

    /**
     * @param topicType 主题类型{0:正常, 1:置顶, 2:公告}
     */
    public void setTopicType(Integer topicType) {
        this.topicType = topicType;
    }

    /**
     * @return 主题检阅次数
     */
    public Integer getReviews() {
        return reviews;
    }

    /**
     * @param reviews 主题检阅次数
     */
    public void setReviews(Integer reviews) {
        this.reviews = reviews;
    }

    /**
     * @return 主题回复次数
     */
    public Integer getReplies() {
        return replies;
    }

    /**
     * @param replies 主题回复次数
     */
    public void setReplies(Integer replies) {
        this.replies = replies;
    }

    /**
     * @return up次数
     */
    public Integer getThumbsUp() {
        return thumbsUp;
    }

    /**
     * @param thumbsUp up次数
     */
    public void setThumbsUp(Integer thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    /**
     * @return down次数
     */
    public Integer getThumbsDown() {
        return thumbsDown;
    }

    /**
     * @param thumbsDown down次数
     */
    public void setThumbsDown(Integer thumbsDown) {
        this.thumbsDown = thumbsDown;
    }


    /**
     * <p>助推, 增加文档被搜索到的排名</p>
     *
     * @return double
     */
    public Double getBoost() {
        return boost;
    }

    public void setBoost(Double boost) {
        this.boost = boost;
    }

    /**
     * <p>是否作为训练数据</p>
     *
     * @return 0:否, 1:是
     */
    public Integer getTrainSample() {
        return trainSample;
    }

    public void setTrainSample(Integer trainSample) {
        this.trainSample = trainSample;
    }

    /**
     * @return 是否锁定此主题{0:否,1:是}, 锁定的主题不能被回复
     */
    public Integer getLocked() {
        return locked;
    }

    /**
     * @param locked 是否锁定此主题{0:否,1:是}, 锁定的主题不能被回复
     */
    public void setLocked(Integer locked) {
        this.locked = locked;
    }

    /**
     * @return 是否审核此主题{0:否, 1:是}
     */
    public Integer getModerated() {
        return moderated;
    }

    /**
     * @param moderated 是否审核此主题{0:否, 1:是}
     */
    public void setModerated(Integer moderated) {
        this.moderated = moderated;
    }

    public Posts getPost() {
        return post;
    }

    public void setPost(Posts post) {
        this.post = post;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Attachments> getRotations() {
        return rotations;
    }

    public void setRotations(List<Attachments> rotations) {
        this.rotations = rotations;
    }

    public List<Attachments> getImages() {
        return images;
    }

    public void setImages(List<Attachments> images) {
        this.images = images;
    }

    public List<Attachments> getAudios() {
        return audios;
    }

    public void setAudios(List<Attachments> audios) {
        this.audios = audios;
    }

    public List<Attachments> getVideos() {
        return videos;
    }

    public void setVideos(List<Attachments> videos) {
        this.videos = videos;
    }

    public TopicsFetch getFrom() {
        return from;
    }

    public void setFrom(TopicsFetch from) {
        this.from = from;
    }

    public TopicsProduct getProduct() {
        return product;
    }

    public void setProduct(TopicsProduct product) {
        this.product = product;
    }

    public TopicsDocument getDocument() {
        return document;
    }

    public void setDocument(TopicsDocument document) {
        this.document = document;
    }

    public boolean isImageSingle() {
        return imageSingle;
    }

    public void setImageSingle(boolean imageSingle) {
        this.imageSingle = imageSingle;
    }

    public String getOfficialAccounts() {
        return officialAccounts;
    }

    public void setOfficialAccounts(String officialAccounts) {
        this.officialAccounts = officialAccounts;
    }

    public boolean isImageRotation() {
        return imageRotation;
    }

    public void setImageRotation(boolean imageRotation) {
        this.imageRotation = imageRotation;
    }



    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }


    public Long getSeedId() {
        return seedId;
    }

    public void setSeedId(Long seedId) {
        this.seedId = seedId;
    }

    public TopicsTune getTune() {
        return tune;
    }

    public void setTune(TopicsTune tune) {
        this.tune = tune;
    }

    public TopicsMp getMp() {
		return mp;
	}

	public void setMp(TopicsMp mp) {
		this.mp = mp;
	}

	public List<MpDownloads> getMpdownloads() {
		return mpdownloads;
	}

	public void setMpdownloads(List<MpDownloads> mpdownloads) {
		this.mpdownloads = mpdownloads;
	}

	public List<TopicsMpdownloads> getTopicsMpdownloads() {
		return topicsMpdownloads;
	}

	public void setTopicsMpdownloads(List<TopicsMpdownloads> topicsMpdownloads) {
		this.topicsMpdownloads = topicsMpdownloads;
	}

	public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public enum TopicType {

        NORMAL(0, "正常"),
        STICKY(1, "置顶"),
        NOTICE(2, "公告");

        private static Map<Integer, TopicType> map = new HashMap<>();

        static {
            for (TopicType topicType : TopicType.values()) {
                map.put(topicType.getIndex(), topicType);
            }
        }

        public static TopicType from(Integer index) {
            return map.get(index);
        }

        private final int index;
        private final String name;

        TopicType(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

    }

    public enum ForumType {

        PRODUCT(1, "产品"),

        BRAND(2, "品牌"),

        NEWS(3, "新闻"),

        EVALUATION(4, "评测"),

        VIDEOS(5, "视频"),

        ANCHOR(6,"美频娱乐新闻"),
        
        MPSUPPORT(7,"美频技术支持"),
    	
    	MPNEWS(8,"美频行业新闻");

        private static Map<Long, ForumType> map = new HashMap<>(ForumType.values().length);

        static {
            for (ForumType forumType : ForumType.values()) {
                map.put(forumType.index, forumType);
            }
        }

        public static ForumType from(Long index) {
            return map.get(index);
        }

        public static String getName(long index) {
            return map.get(index).getName();
        }

        private final long index;
        private final String name;

        ForumType(long index, String name) {
            this.index = index;
            this.name = name;
        }

        public long getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

    }

}
