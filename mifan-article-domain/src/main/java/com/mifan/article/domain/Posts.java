package com.mifan.article.domain;

import com.mifan.article.domain.support.CompareBox;
import com.mifan.article.domain.support.HtmlUtils;
import com.mifan.article.domain.support.Multilingual;
import com.mifan.article.domain.support.ValidationGroups.*;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Post;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sound.midi.Patch;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class Posts extends BaseEntity implements Multilingual {

    public static final String TABLE_NAME = "posts";

    public static final String PARENT_ID = "parent_id";
    public static final String TOPIC_ID = "topic_id";
    public static final String PRIORITY = "priority";

    private static final long serialVersionUID = 7830407602572628923L;

    @NotNull(groups = {ProductPost.class, NewsPost.class, VideosPost.class, TranslationPost.class, Post.class}, message = "{NotNull.Posts.parentId}")
    @Min(groups = {TranslationPost.class}, message = "{Error.Posts.parentId}", value = 1)
    private Long parentId;
    @NotNull(groups = {TranslationPost.class, Post.class}, message = "{NotNull.Posts.topicId}")
    private Long topicId;
    private Integer priority;
    @NotNull(groups = {Post.class, ProductPost.class, NewsPost.class, VideosPost.class}, message = "{NotNull.Posts.language}")
    private Integer language;

    // redundancy fields
    private List<String> categories;
    private List<String> tags;
    private List<Map<String, String>> features;
    @NotBlank(groups = {Post.class, Patch.class, BrandPost.class, ProductPost.class, NewsPost.class, VideosPost.class, TranslationPost.class, TranslationPatch.class, TranslateTaskPatch.class, AuditorPatch.class,MpPost.class}, message = "{NotNull.Posts.title}")
    private String title;
    //    @NotBlank(groups = {BrandPost.class,ProductPost.class,NewsPost.class}, message = "{NotNull.Posts.description}")
    private String description;
    @NotBlank(groups = {AdminPost.class,ProductPost.class, NewsPost.class}, message = "{NotNull.Posts.content}")
    private String content;

    @NotNull(groups = {TranslationPost.class, TranslationPatch.class}, message = "{NotNull.Posts.state}")
    @Range(min = 1, max = 2, groups = {TranslationPost.class, TranslationPatch.class}, message = "{Error.Posts.state}")
    private Integer state;//1:草稿箱, 2:待审核, 3:审核通过, 4:审核失败, 5:被覆盖, 9:已删除

    private Boolean isAdmin;//为了让管理员和普通用户看到不同的精翻列表，添加这个筛选条件

    private Moderation moderation;//审核信息

    private Posts parent;

    private PostsText postsText;

    private String parentTitle;//根据父标题查询，用于精翻列表筛选条件

    private Long forumId;//文章类型，用于精翻列表筛选条件
    
    private String[] contentSegment;//文章段落

    //

    /**
     * 人工标注的类别:priority 1
     */
    private ForumCategories forumCategories;

    /**
     * 多语言自动分类:priority 2
     */
    private List<Map<String, String>> classification;

    /**
     * 双语词典
     */
    private Map<String, String> dictionary;

    public Posts() {
    }

    public Posts(Long id) {
        super(id);
    }

    public static List<String> fromString(String s) {
        if (s != null && !"".equals(s = s.trim())) {
            List<String> list = new ArrayList<>();
            for (String item : s.split(",")) {
                list.add(item.trim());
            }
            return list;
        }
        return Collections.emptyList();
    }

    public static List<CompareBox> merge(List<Map<String, Object>> items) {
        return merge(items, null);
    }

    /**
     * <p>map容器的key用来存放多个条目参数名称的并集, value用来存放每个条目某一个参数值的集合</p>
     *
     * @param items    每个条目的参数集合
     * @param function function
     * @return boxes
     */
    @SuppressWarnings("unchecked")
    public static List<CompareBox> merge(List<Map<String, Object>> items, Function<String, String> function) {
        List<CompareBox> result = new ArrayList<>();
        Map<String, List<String>> map = new TreeMap<>();
        items.stream().flatMap(item -> item.keySet().stream()).forEach(key -> map.computeIfAbsent(key, k -> new ArrayList()));
        map.forEach((name, array) -> {
            // add to same position
            items.forEach(item -> {
                String value = (String) item.get(name);
                array.add((value == null || "".equals(value = value.trim())) ? "-" : value);
            });

            // collapsible
            boolean collapsible = true;
            for (int i = 1; i < array.size(); i++) {
                if (!array.get(i - 1).equals(array.get(i))) {
                    collapsible = false;
                    break;
                }
            }

            // result
            String v = name;
            if (function != null) {
                v = function.apply(v);
            }
            array.add(0, v == null ? name : v);
            result.add(new CompareBox(array, collapsible));
        });
        return result;
    }

    /**
     * <p>转换成简单文本</p>
     *
     * @return a simple classifiable text
     */
    @Override
    public String toString() {
        return toString(this);
    }

    private String toString(Posts post) {
        Posts parent;
        while ((parent = post.getParent()) != null) {
            post = parent;
        }

        //
        PostsText postsText = post.getPostsText();
        if (postsText == null) {
            StringBuilder sb = new StringBuilder();
            if (post.getTitle() != null) {
                sb.append(post.getTitle().trim());
            }
            if (post.getDescription() != null) {
                sb.append(", ");
                sb.append(post.getDescription().trim());
            }
            if (post.getContent() != null) {
                sb.append(", ");
                HtmlUtils.compress(post.getContent(), true);
            }
            if (!CollectionUtils.isEmpty(post.getTags())) {
                for (String tag : post.getTags()) {
                    sb.append(", ");
                    sb.append(tag.trim());
                }
            }
            if (!CollectionUtils.isEmpty(post.getFeatures())) {
                sb.append(", ");
                for (Map<String, String> map : post.getFeatures()) {
                    sb.append(map.get("_name"));
                    sb.append(", ");
                    String value = HtmlUtils.compress(map.get("_value"), true);
                    if (!StringUtils.isEmpty(value)) {
                        sb.append(value);
                        sb.append(", ");
                    }
                }
            }
            return sb.toString();
        } else {
            return postsText.toString();
        }
    }

    /**
     * <p>设置机器学习的多语言自动分类</p>
     *
     * @param classification classification
     */
    public void classification(TopicsClassification classification) {
        if (classification != null
                // && classification.getEnabled() == 1
                && classification.getTargetVariable() != null) {
            this.classification = Multilingual.getList(classification.getTargetVariable());
        }
    }

    /**
     * <p>将获取自动分类委派给父节点</p>
     * <p>通常只有根节点有分类数据, 子节点需要将该请求委派给父节点</p>
     * <p>建立索引时会用到这个方法, 建立类别的中英文可搜索类别</p>
     * <p>约束</p>
     * <ol>
     * <li>索引的数据, 这个字段一般都不会为空</li>
     * </ol>
     * <p>优先级</p>
     * <ol>
     * <li>优先使用人工标注的类别, 然后</li>
     * <li>从多语言自动分类中, 获取当前语言的分类结果</li>
     * </ol>
     *
     * @return list
     */
    public List<Map<String, String>> classification() {
        if (parent != null) {
            return parent.classification();
        } else {
            if (forumCategories != null) {
                // 优先使用人工标注的类别
                return classification(forumCategories.toList());
            } else if (classification != null) {
                // 然后使用机器学习的类别
                return classification;
            } else if (categories != null) {
                // 最后使用原始分类
                return classification(categories);
            } else {
                return null;
            }
        }
    }

    public List<Map<String, String>> classification(List<String> list) {
        Language language = Language.from(this.language);
        if (language == null || language == Language.DEFAULT) {
            return null;
        } else {
            return list.stream().map(title -> language.bilingual(dictionary, title)).collect(Collectors.toList());
        }
    }

    public void setForumCategories(ForumCategories forumCategories) {
        this.forumCategories = forumCategories;

    }

    /**
     * <p>从多语言自动分类中, 获取当前语言的分类结果</p>
     *
     * @see #classification()
     */
    public void categories() {
        Language lan;
        List<Map<String, String>> classification;
        if ((lan = Language.from(language)) != null
                && (classification = classification()) != null) {
            setCategories(Multilingual.getList(classification, lan));
        }
    }

    /**
     * <p>转换</p>
     *
     * @param text text
     */
    public void text(PostsText text) {
        text(text, true);
    }

    public void text(PostsText text, boolean assignment) {
        if (text == null) {
            return;
        }
        if (getCategories() == null) {
            setCategories(fromString(text.getCategory()));
        }
        setTags(fromString(text.getTag()));
        setFeatures(Multilingual.getList(text.getFeature()));
        setTitle(text.getTitle());
        setDescription(text.getDescription());
        setContent(text.getContent());
        if (assignment) {
            this.postsText = text;
        }
    }

    public void setterText() {
        PostsText text = new PostsText();
        text.setTitle(title);
        text.setDescription(description);
        text.setContent(content);
        setPostsText(text);
    }

    // @JsonIgnore
    public Integer getPostType() {
        Long parentId = getParentId();
        Long creator = getCreator();
        if (parentId == null || creator == null) {
            return null;
        }
        if (parentId == 0L && creator == 0L) {
            return PostType.CRAWLER.getIndex();
        } else if (parentId == 0L) {
            return PostType.ORIGINAL.getIndex();
        } else if (creator == 0L) {
            return PostType.MACHINE_TRANSLATION.getIndex();
        } else {
            return PostType.HUMAN_TRANSLATION.getIndex();
        }
    }

    public String getPostTypeValue() {
        Integer postType = getPostType();
        if (postType == null) {
            return null;
        }
        return PostType.from(postType).getName();
    }

    /**
     * @return PARENT ID
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId PARENT ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return 主题ID
     */
    public Long getTopicId() {
        return topicId;
    }

    /**
     * @param topicId 主题ID
     */
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    /**
     * @return 优先级
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority 优先级
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return 语言 0:缺省, 1:中文, 2英文
     */
    public Integer getLanguage() {
        return language;
    }

    /**
     * @param language 语言 0:缺省, 1:中文, 2英文
     */
    public void setLanguage(Integer language) {
        this.language = language;
    }

    public String[] getContentSegment() {
        return contentSegment;
    }

    public void setContentSegment(String[] contentSegment) {
        this.contentSegment = contentSegment;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Map<String, String>> getFeatures() {
        return features;
    }

    public void setFeatures(List<Map<String, String>> features) {
        this.features = features;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Posts getParent() {
        return parent;
    }

    public void setParent(Posts parent) {
        this.parent = parent;
        categories();
    }

    /*@JsonIgnore*/
    public PostsText getPostsText() {
        return postsText;
    }

    public void setPostsText(PostsText postsText) {
        this.postsText = postsText;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public Moderation getModeration() {
        return moderation;
    }

    public void setModeration(Moderation moderation) {
        this.moderation = moderation;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Long getForumId() {
        return forumId;
    }

    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }

    public void setDictionary(Map<String, String> dictionary) {
        this.dictionary = dictionary;
    }

    public enum PostType {

        CRAWLER(1, "爬取"),
        MACHINE_TRANSLATION(2, "机翻"),
        HUMAN_TRANSLATION(3, "精翻"),
        ORIGINAL(4, "原创");

        private static Map<Integer, PostType> map = new HashMap<>();

        static {
            for (PostType postType : PostType.values()) {
                map.put(postType.getIndex(), postType);
            }
        }

        private final int index;
        private final String name;

        PostType(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public static PostType from(Integer index) {
            return map.get(index);
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }
}
