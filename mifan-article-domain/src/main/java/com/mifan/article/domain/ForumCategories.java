package com.mifan.article.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-10-31
 */
public class ForumCategories extends BaseEntity {

    public static final String NODE_PARENT = "parent";
    public static final String NODE_CHILDREN = "children";

    public static final String TABLE_NAME = "forum_categories";
    public static final String FORUM_ID = "forum_id";
    public static final String ROOT_ID = "root_id";
    public static final String PARENT_ID = "parent_id";
    public static final String TITLE = "title";
    public static final String FILENAME = "filename";
    public static final String PATH = "path";
    public static final String DEPTH = "depth";
    public static final String LEAF = "leaf";
    public static final String DISPLAY_ORDER = "display_order";
    private static final long serialVersionUID = -1427404758217440154L;
    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(ForumCategories.ID)
            .add(ForumCategories.FORUM_ID)
            .add(ForumCategories.ROOT_ID)
            .add(ForumCategories.PARENT_ID)
            .add(ForumCategories.TITLE)
            .add(ForumCategories.FILENAME)
            .add(ForumCategories.PATH)
            .add(ForumCategories.DEPTH)
            .add(ForumCategories.LEAF)
            .add(ForumCategories.DISPLAY_ORDER)
            .add(ForumCategories.ENABLED)
            .build();
    /**
     * 不支持修改forumID
     */
    @NotNull(groups = {Post.class}, message = "{NotNull.ForumCategories.forumId}")
    @Null(groups = {Patch.class}, message = "{Null.ForumCategories.forumId}")
    private Long forumId;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.ForumCategories.rootId}")
    private Long rootId;

    /**
     * 暂时不支持parentID的修改, 这个操作涉及大量的节点depth, path, rootID变更
     */
    @NotNull(groups = {Post.class}, message = "{NotNull.ForumCategories.parentId}")
    @Null(groups = {Patch.class}, message = "{Null.ForumCategories.parentId}")
    private Long parentId;

    @NotNull(groups = {Post.class, Patch.class}, message = "{NotNull.ForumCategories.title}")
    @Size(groups = {Post.class, Patch.class}, message = "{Size.ForumCategories.title}", min = 1, max = 200)
    private String title;

    @Size(groups = {Post.class, Patch.class}, message = "{Size.ForumCategories.filename}", min = 0, max = 200)
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String filename;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.ForumCategories.path}")
    private String path;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.ForumCategories.depth}")
    private Integer depth;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.ForumCategories.leaf}")
    private Integer leaf;

    private Integer displayOrder;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String titleChinese;

    private ForumCategories parent;

    private List<ForumCategories> children = Collections.EMPTY_LIST;
    
    private List<Topics> topics = Collections.EMPTY_LIST;

    public ForumCategories() {
    }

    public ForumCategories(Long id) {
        super(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof ForumCategories && (((ForumCategories) obj).getId().equals(this.getId())));
    }

    @Override
    public int hashCode() {
        if (getId() == null) {
            return super.hashCode();
        }
        return getId().hashCode();
    }

    /**
     * <p>返回所有父节点ID集合, 不包括其自身</p>
     *
     * @param list list
     * @return set
     */
    public static Set<Long> parentIds(List<ForumCategories> list) {
        Set<Long> parents = new HashSet<>();
        for (ForumCategories categories : list) {
            parents.addAll(categories.parentIds());
        }
        return parents;
    }

    /**
     * <p>构建树形结构</p>
     *
     * @param parents parents
     * @param list    list
     */
    public static void tree(Map<Long, ForumCategories> parents, List<ForumCategories> list) {
        for (ForumCategories category : list) {
            ForumCategories parent;
            while ((parent = parents.get(category.getParentId())) != null) {
                category.setParent(parent);
                category = parent;
            }
        }
    }

    /**
     * <p>获取当前节点的所有父节点ID</p>
     *
     * @return set
     */
    public Set<Long> parentIds() {
        String[] array = getPath().split("\\.");
        return Arrays.stream(array).limit(array.length - 1).map(Long::valueOf).collect(Collectors.toSet());
    }

    // get and set method

    /**
     * <p>返回根节点信息</p>
     *
     * @return ForumCategories
     */
    public ForumCategories rootNode() {
        if (parent != null) {
            return parent.rootNode();
        } else {
            return this;
        }
    }

    public List<String> toList() {
        return toList(List::add);
    }

    public <T> List<T> toList(BiConsumer<List<T>, String> consumer) {
        List<T> result;
        if (parent == null) {
            result = new ArrayList<>();
        } else {
            result = parent.toList(consumer);
        }
        consumer.accept(result, title);
        return result;
    }

    //

    /**
     * @return 版块ID
     */
    public Long getForumId() {
        return forumId;
    }

    /**
     * @param forumId 版块ID
     */
    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }

    /**
     * @return 根节点ID
     */
    public Long getRootId() {
        return rootId;
    }

    /**
     * @param rootId 根节点ID
     */
    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }

    /**
     * @return 父节点ID
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId 父节点ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
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
     * @return 附件名称
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename 附件名称
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return 深度
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     * @param depth 深度
     */
    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    /**
     * @return 是否是叶子节点
     */
    public Integer getLeaf() {
        return leaf;
    }

    /**
     * @param leaf 是否是叶子节点
     */
    public void setLeaf(Integer leaf) {
        this.leaf = leaf;
    }

    /**
     * @return 显示顺序
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /**
     * @param displayOrder 显示顺序
     */
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getTitleChinese() {
        return titleChinese;
    }

    public void setTitleChinese(String titleChinese) {
        this.titleChinese = titleChinese;
    }

    public ForumCategories getParent() {
        return parent;
    }

    public void setParent(ForumCategories parent) {
        this.parent = parent;
    }

    public List<ForumCategories> getChildren() {
        return children;
    }

    public void setChildren(List<ForumCategories> children) {
        this.children = children;
    }

    public List<Topics> getTopics() {
        return topics;
    }

    public void setTopics(List<Topics> topics) {
        this.topics = topics;
    }
    
}
