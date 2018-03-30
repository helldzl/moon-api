package com.mifan.article.service.impl;

import com.mifan.article.dao.ForumCategoriesDao;
import com.mifan.article.domain.*;
import com.mifan.article.service.ForumCategoriesService;
import com.mifan.article.service.TopicsService;
import com.mifan.article.service.WordDictionaryService;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.*;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mifan.article.domain.support.Multilingual.Language;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-10-31
 */
@Service
public class ForumCategoriesServiceImpl extends AbstractBaseService<ForumCategories, ForumCategoriesDao> implements ForumCategoriesService {

    @Autowired
    @Qualifier("wordDictionaryServiceImpl")
    private WordDictionaryService wordDictionaryService;

    @Autowired
    private TopicsService topicsService;

    /**
     * <p>
     * 规则:
     * <ol>
     * <li>判断parentID是否存在</li>
     * <li>判断forumID是否存在</li>
     * <li>保存信息, 基于validate验证约束相关的注解</li>
     * <li>更新信息, 判断是否是根节点 parent_id = 0的</li>
     * <li>如果是根节点, 只更新root_id, path字段</li>
     * <li>否则更新root_id, path, depth. 以及父节点的leaf字段</li>
     * </ol>
     * </p>
     *
     * @param entity entity
     * @param <S>    S
     * @return effect rows
     */
    @Override
    public <S extends ForumCategories> int save(S entity) {
        Long parentId = entity.getParentId();
        Long forumId = entity.getForumId();

        // check if exists [parent]
        if (parentId != null && parentId > 0L && super.count(Restrictions.and(
                Restrictions.eq(BaseEntity.ENABLED, 1),
                Restrictions.eq(BaseEntity.ID, parentId),
                Restrictions.eq(ForumCategories.FORUM_ID, forumId))) == 0) {
            throw new IllegalStateException("父节点不存在或已经被禁用");
        }

        // check if exists [forum]
        if (Topics.ForumType.from(forumId) == null) {
            throw new IllegalStateException("版面不存在或已经被禁用");
        }

        // save entity
        int n = super.save(entity);
        if (n <= 0) {
            return n;
        }

        // update other information
        Long id = entity.getId();
        ForumCategories update = new ForumCategories(id);
        if (parentId == null || parentId == 0L) {
            // if is head node
            update.setRootId(id);
            update.setPath(String.valueOf(id));
        } else {
            // if is tail node
            ForumCategories parent = super.findOne(parentId, Fields.builder()
                    .add(ForumCategories.ID)
                    .add(ForumCategories.ROOT_ID)
                    .add(ForumCategories.PATH)
                    .add(ForumCategories.DEPTH)
                    .build());

            update.setRootId(parent.getRootId());
            update.setPath(parent.getPath() + "." + id);
            update.setDepth(parent.getDepth() + 1);

            // update parent leaf field
            parent = new ForumCategories(parentId);
            parent.setLeaf(0);
            super.update(parent, Restrictions.eq(ForumCategories.LEAF, 1));
        }
        super.update(update);
        return n;
    }

    @Override
    public int remove(Long id) {
        checkException(id);
        int n = super.remove(id);
        if (n > 0) {
            ForumCategories entity = super.findOne(id, Fields.builder().add(ForumCategories.PARENT_ID).build());
            if (entity.getParentId() != 0L && !super.exists(
                    Restrictions.and(
                            Restrictions.eq(ForumCategories.ENABLED, 1),
                            Restrictions.eq(ForumCategories.PARENT_ID, entity.getParentId())))) {
                entity = new ForumCategories(entity.getParentId());
                entity.setLeaf(1);
                super.update(entity);
            }
        }
        return n;
    }

    @Override
    public int remove(Long id, Criterion criterion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends ForumCategories> int update(S entity) {
        checkException(entity);
        return super.update(entity);
    }

    @Override
    public <S extends ForumCategories> int update(S entity, Criterion criterion) {
        checkException(entity);
        return super.update(entity, criterion);
    }

    /**
     * @param criterion criterion
     * @param fields    fields
     * @return 返回带有节点信息的集合
     */
    @Override
    public List<ForumCategories> findAll(Criterion criterion, Iterable<? extends Field> fields) {
        List<ForumCategories> list = super.findAll(criterion, ForumCategories.DEFAULT_FIELDS);

        // 找到所有父类节点
        Set<Long> parents = ForumCategories.parentIds(list);

        // 没有数据, 直接返回
        if (parents.isEmpty()) {
            return list;
        }

        tree(parents, list);

        return list;
    }

    private void tree(Set<Long> parents, List<ForumCategories> list) {
        if (CollectionUtils.isEmpty(parents)) {
            return;
        }
        // 查询所有父节点数据
        Map<Long, ForumCategories> map = super.findAll(Restrictions.in(ForumCategories.ID, parents.toArray()), ForumCategories.DEFAULT_FIELDS)
                .stream()
                .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        ForumCategories.tree(map, list);
    }

    @Override
    public ForumCategories findOne(Long id, Iterable<? extends Field> fields) {
        ForumCategories category = super.queryForObject(id, ForumCategories.DEFAULT_FIELDS);
        if (category != null) {
            Include include = Restrictions.get(Include.class);

            // parent
            if (include != null && include.contains(ForumCategories.NODE_PARENT)) {
                tree(category.parentIds(), Collections.singletonList(category));
            }

            // children
            if (include != null && include.contains(ForumCategories.NODE_CHILDREN)) {
                Page<ForumCategories> children = super.findAll(Restrictions.and(
                        Restrictions.eq(ForumCategories.PARENT_ID, category.getId()),
                        Restrictions.eq(ForumCategories.ENABLED, 1),
                        Restrictions.gt(ForumCategories.DISPLAY_ORDER, -1)),
                        Pages.builder().page(1).size(200)
                                .sort(Pages.sortBuilder()
                                        .add(ForumCategories.DISPLAY_ORDER, false)
                                        .build()).build(),
                        ForumCategories.DEFAULT_FIELDS);
                category.setChildren(children.getContent());
            }

            setTitleChinese(category);
        }
        return category;
    }

    @Override
    public ForumCategories queryForObject(Long id, Iterable<? extends Field> fields) {
        return findOne(id, fields);
    }

    @Override
    public ForumCategories findOneMp(Long id, boolean child, boolean childTopics) {
        ForumCategories category = super.queryForObject(id, ForumCategories.DEFAULT_FIELDS);
        if (category != null) {
            if (category.getLeaf() == 1) {
                List<Topics> topics = Services.findAll(Topics.class, Restrictions.and(
                        Restrictions.eq(Topics.CATEGORY_ID, id),
                        Restrictions.eq(Topics.ENABLED, 1)),
                        Fields.builder().add(Topics.ID).add(Topics.TITLE).add(Topics.CATEGORY_ID).build());
                category.setTopics(topics);
                if (!CollectionUtils.isEmpty(topics)) {
                    Map<Long, Topics> topicMap = topics.stream().collect(Collectors.toMap(Topics::getId, t -> t));
                    Set<Long> topicIds = topics.stream().map(Topics::getId).collect(Collectors.toSet());
                    //tune
                    Services.findAll(TopicsTune.class, Restrictions.in(TopicsTune.TOPIC_ID, topicIds.toArray()))
                            .forEach(tt -> topicMap.get(tt.getTopicId()).setShortTitle(tt.getName()));
                    //fetch
                    Services.findAll(TopicsFetch.class,
                            Restrictions.in(TopicsFetch.TOPIC_ID, topicIds.toArray()),
                            Pages.builder().page(1).size(topicIds.size()).build(),
                            false)
                            .forEach(topicsFetch -> topicMap.get(topicsFetch.getTopicId()).setFrom(topicsFetch));
                    //images
                    Map<Long, Map<Attachments.ContentType, List<Attachments>>> attachments = topicsService.findAttachment(topicIds);
                    if (!attachments.isEmpty()) {
                        topics.forEach(t -> {
                            t.attachments(attachments.get(t.getId()));
                        });
                    }

                }
            } else {
                ForumCategories find = new ForumCategories();
                find.setParentId(id);
                find.setEnabled(1);
                List<ForumCategories> children = super.findAll(find, ForumCategories.DEFAULT_FIELDS);
                category.setChildren(children);
                if (!CollectionUtils.isEmpty(children) && child) {
                    if (childTopics) {
                        List<ForumCategories> leafs = children.stream().filter(fc -> fc.getLeaf() == 1).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(leafs)) {
                            List<Topics> topics = Services.findAll(Topics.class,
                                    Restrictions.and(
                                            Restrictions.in(Topics.CATEGORY_ID, leafs.stream().map(ForumCategories::getId).collect(Collectors.toList()).toArray()),
                                            Restrictions.eq(Topics.ENABLED, 1)),
                                    Fields.builder().add(Topics.ID).add(Topics.TITLE).add(Topics.CATEGORY_ID).build());
                            if (!CollectionUtils.isEmpty(topics)) {
                                Map<Long, List<Topics>> leafsMap = topics.stream().collect(Collectors.groupingBy(Topics::getCategoryId));
                                leafs.forEach(fc -> fc.setTopics(leafsMap.get(fc.getId())));

                                Map<Long, Topics> topicMap = topics.stream().collect(Collectors.toMap(Topics::getId, t -> t));
                                Services.findAll(TopicsTune.class, Restrictions.in(TopicsTune.TOPIC_ID,
                                        topics.stream().map(Topics::getId).collect(Collectors.toList()).toArray())).
                                        forEach(tt -> topicMap.get(tt.getTopicId()).setShortTitle(tt.getName()));
                            }
                        }
                    }

                    List<ForumCategories> noLeafs = children.stream().filter(fc -> fc.getLeaf() != 1).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(noLeafs)) {
                        List<ForumCategories> noLeafChildren = super.findAll(Restrictions.and(
                                Restrictions.in(ForumCategories.PARENT_ID, noLeafs.stream().map(BaseEntity::getId).collect(Collectors.toList()).toArray()),
                                Restrictions.eq(ForumCategories.ENABLED, 1),
                                Restrictions.gt(ForumCategories.DISPLAY_ORDER, -1)),
                                Pages.builder().page(1).size(200)
                                        .sort(Pages.sortBuilder()
                                                // 前缀匹配索引 parent_id_enabled_display_order_idx
                                                .add(ForumCategories.PARENT_ID, false)
                                                .add(ForumCategories.ENABLED, false)
                                                .add(ForumCategories.DISPLAY_ORDER, false)
                                                .build()).build(),
                                ForumCategories.DEFAULT_FIELDS).getContent();
                        Map<Long, List<ForumCategories>> noLeafsMap = noLeafChildren.stream().collect(Collectors.groupingBy(ForumCategories::getParentId));
                        noLeafs.forEach(fc -> fc.setChildren(noLeafsMap.get(fc.getId())));
//                        setTitleChinese(noLeafChildren);
                    }
                }
                //setTitleChinese(children);
            }
            //setTitleChinese(category);
        }
        return category;
    }
    /*@Override
    public Page<ForumCategories> findMpCategories(Long parentId,int page,int size){
        ForumCategories find = new ForumCategories();
        find.setParentId(parentId);
        find.setEnabled(1);
        PageRequest pageRequest =  Pages.builder().page(page).size(size).sort(Pages.sortBuilder().add(ForumCategories.DISPLAY_ORDER, true).build()).build();
        Page<ForumCategories> result = super.findAll(find, pageRequest);
        List<ForumCategories> list = result.getContent();
        if (result.hasContent()) {
            List<ForumCategories> leafs = list.stream().filter(fc -> fc.getLeaf() == 1).collect(Collectors.toList());
            Map<Long,List<Topics>> leafsMap = Services.findAll(Topics.class,
                    Restrictions.and(
                            Restrictions.in(Topics.CATEGORY_ID,leafs.stream().map(ForumCategories::getId).collect(Collectors.toList()).toArray()),
                            Restrictions.eq(Topics.ENABLED, 1)),
                    Fields.builder().add(Topics.ID).add(Topics.TITLE).add(Topics.CATEGORY_ID).build()).
                    stream().collect(Collectors.groupingBy(Topics::getCategoryId));
            leafs.forEach(fc -> fc.setTopics(leafsMap.get(fc.getId())));
            
            List<ForumCategories> noLeafs = list.stream().filter(fc -> fc.getLeaf() != 1).collect(Collectors.toList());
            Map<Long, List<ForumCategories>> noLeafsMap = super.findAll(Restrictions.and(
                    Restrictions.in(ForumCategories.PARENT_ID, noLeafs.stream().map(BaseEntity::getId).collect(Collectors.toList()).toArray()),
                    Restrictions.eq(ForumCategories.ENABLED, 1),
                    Restrictions.gt(ForumCategories.DISPLAY_ORDER, -1)),
                    Pages.builder().page(1).size(200)
                            .sort(Pages.sortBuilder()
                                    // 前缀匹配索引 parent_id_enabled_display_order_idx
                                    .add(ForumCategories.PARENT_ID, false)
                                    .add(ForumCategories.ENABLED, false)
                                    .add(ForumCategories.DISPLAY_ORDER, false)
                                    .build()).build(),
                    ForumCategories.DEFAULT_FIELDS)
                    .getContent()
                    .stream()
                    .collect(Collectors.groupingBy(ForumCategories::getParentId));
            noLeafs.forEach(category -> category.setChildren(noLeafsMap.get(category.getId())));
        }
        setTitleChinese(list);
        return result;
    }*/

    @Override
    public Page<ForumCategories> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Page<ForumCategories> page = super.findAll(criterion, pageable, fields);
        List<ForumCategories> list = page.getContent();
        if (page.hasContent()) {

            Include include = Restrictions.get(Include.class);

            // parent
            if (include != null && include.contains(ForumCategories.NODE_PARENT)) {
                tree(ForumCategories.parentIds(list), list);
            }

            // children
            if (include != null && include.contains(ForumCategories.NODE_CHILDREN)) {
                Map<Long, List<ForumCategories>> map = super.findAll(Restrictions.and(
                        Restrictions.in(ForumCategories.PARENT_ID, list.stream().map(BaseEntity::getId).collect(Collectors.toSet()).toArray()),
                        Restrictions.eq(ForumCategories.ENABLED, 1),
                        Restrictions.gt(ForumCategories.DISPLAY_ORDER, -1)),
                        Pages.builder().page(1).size(200)
                                .sort(Pages.sortBuilder()
                                        // 前缀匹配索引 parent_id_enabled_display_order_idx
                                        .add(ForumCategories.PARENT_ID, false)
                                        .add(ForumCategories.ENABLED, false)
                                        .add(ForumCategories.DISPLAY_ORDER, false)
                                        .build()).build(),
                        ForumCategories.DEFAULT_FIELDS)
                        .getContent()
                        .stream()
                        .collect(Collectors.groupingBy(ForumCategories::getParentId));

                page.forEach(category -> category.setChildren(map.get(category.getId())));
            }

            setTitleChinese(list);
        }
        return page;

    }

    private void checkException(ForumCategories entity) {
        Long id = entity.getId();
        Integer enabled = entity.getEnabled();
        if (id != null && enabled != null && enabled == 0) {
            checkException(id);
        }
    }

    private void checkException(Long id) throws IllegalStateException {
        // check can remove
        if (super.count(Restrictions.and(
                Restrictions.eq(ForumCategories.ENABLED, 1),
                Restrictions.eq(ForumCategories.PARENT_ID, id))) > 0) {
            throw new IllegalStateException("不能直接删除非叶子节点, 请先删除该节点下的叶子节点");
        }
    }

    private void setTitleChinese(ForumCategories entity) {
        setTitleChinese(Collections.singletonList(entity));
    }

    private void setTitleChinese(List<ForumCategories> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        Set<ForumCategories> all = new HashSet<>();

        // add all children
        children(all, list);

        // add all parent
        for (ForumCategories category : list) {
            ForumCategories parent;
            while ((parent = category.getParent()) != null) {
                all.add(parent);
                category = parent;
            }
        }

        // Use Cache
        all.forEach(category -> {
            String value = Language.getValue(wordDictionaryService.dictionary(), category.getTitle());
            category.setTitleChinese(value == null ? category.getTitle() : value);
        });
    }

    private static void children(Set<ForumCategories> set, List<ForumCategories> list) {
        list.stream().peek(set::add).filter(category -> !CollectionUtils.isEmpty(category.getChildren())).forEach(category -> children(set, category.getChildren()));
    }

    private static ForumCategories getInstance(long id) {
        return new ForumCategories(id);
    }

    public static void main(String[] args) {
        List<ForumCategories> list = new ArrayList<>();

        ForumCategories i4 = getInstance(4);
        i4.setChildren(Arrays.asList(
                getInstance(41),
                getInstance(42)
        ));

        ForumCategories i5 = getInstance(5);
        i5.setChildren(Arrays.asList(
                getInstance(51),
                getInstance(52)
        ));

        ForumCategories instance = getInstance(2);
        instance.setParent(getInstance(1));
        instance.setChildren(Arrays.asList(

                i4,
                getInstance(3),
                i5));
        list.add(instance);

        Set<ForumCategories> set = new LinkedHashSet<>();
        children(set, list);
        set.forEach(categories -> System.out.println(categories.getId()));
    }

}
