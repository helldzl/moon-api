package com.mifan.article.service.impl;

import com.mifan.article.dao.PostsDao;
import com.mifan.article.domain.Moderation;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.PostsText;
import com.mifan.article.domain.Topics;
import com.mifan.article.service.ModerationService;
import com.mifan.article.service.PostsService;
import com.mifan.article.service.TopicsService;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class PostsServiceImpl extends AbstractBaseService<Posts, PostsDao> implements PostsService {

    @Autowired
    private ModerationService moderationService;

    @Autowired
    private TopicsService topicsService;

    @Override
    public Posts queryForObject(Long id, Iterable<? extends Field> fields) {
        Posts posts = super.findOne(id, fields);
        if (posts != null) {
            PostsText text = Services.findOne(PostsText.class, id);
            posts.text(text);

            Topics topics = topicsService.queryForObject(posts.getTopicId(), null, false, false);
            if (topics != null) {
                posts.setForumId(topics.getForumId());
                if (topics.getPost().getParent() != null) {
                    posts.setParent(topics.getPost().getParent());
                } else {
                    posts.setParent(topics.getPost());
                }
            }
            Moderation mode = new Moderation();
            mode.setPostId(posts.getId());
            mode = moderationService.findOne(mode);
            if (mode != null) {
                posts.setModeration(mode);
            }
        }
        return posts;
    }

    @Override
    public Posts queryForObject(Long id) {
        Posts posts = super.findOne(id);
        if (posts != null) {
            PostsText text = Services.findOne(PostsText.class, id);
            posts.text(text);
            if (posts.getParentId() != 0) {
                Posts parent = this.queryForObject(posts.getParentId());
                posts.setParent(parent);
            }
        }
        return posts;
    }

    @Override
    public Posts findOne(Long id) {
        Posts posts = super.findOne(id);
        if (posts != null) {
            PostsText text = Services.findOne(PostsText.class, id);
            posts.text(text, false);
        }
        return posts;
    }

    @Override
    public Posts findOne(Posts entity) {
        Posts posts = super.findOne(entity);
        if (posts != null) {
            PostsText text = Services.findOne(PostsText.class, posts.getId());
            posts.text(text, false);
        }
        return posts;
    }

    @Override
    public Page<Posts> findPageHumanTranslate(Posts entity, int page, int size) {
        PageRequest pageRequest = Pages.builder().page(page).size(size).sort(Pages.sortBuilder().add(Posts.CREATED, false).build()).build();

        Page<Posts> postspage = baseDao.findPageHumanTranslate(entity, pageRequest);
        return postspage;
    }

    @Override
    public Page<Posts> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Page<Posts> page = super.findAll(criterion, pageable, fields);
        if (page.hasContent()) {
            Set<Long> ids = new HashSet<Long>();
            page.getContent().forEach(p -> ids.add(p.getId()));
            List<PostsText> postsList = Services.findAll(PostsText.class, ids, Fields.builder().add(PostsText.ID).add(PostsText.TITLE).build());
            Map<Long, PostsText> textMap = postsList.stream().collect(Collectors.toMap(BaseEntity::getId, text -> text));
            page.getContent().forEach(p -> p.setTitle(textMap.get(p.getId()).getTitle()));
        }
        return page;
    }

    @Override
    public List<Posts> getAllPosts(Long topicId, Integer enabled) {
        Posts find = new Posts();
        find.setTopicId(topicId);
        find.setEnabled(enabled);
        List<Posts> list = this.findAll(find);
        if (list.size() > 0) {
            Set<Long> ids = new HashSet<Long>();
            list.forEach(p -> ids.add(p.getId()));
            List<PostsText> postsList = Services.findAll(PostsText.class, ids, Fields.builder().add(PostsText.ID).add(PostsText.TITLE).build());
            Map<Long, PostsText> textMap = postsList.stream().collect(Collectors.toMap(BaseEntity::getId, text -> text));
            list.forEach(p -> p.setTitle(textMap.get(p.getId()).getTitle()));
        }
        return list;
    }

    /**
     * <ol>
     * <li>parentId=0, creator=0, topic_id 是爬取下来的, 只有一条记录</li>
     * <li>parentId=0, creator!=0, topic_id 是用户创建的, 也是单条的。</li>
     * <li>parentId<>0, creator=0, topic_id 是机器翻译的, 也是一条记录</li>
     * <li>parentId<>0, creator!=0, topic_id 是用户翻译的可以多条, 因为要保存翻译的历史记录.</li>
     * </ol>
     */
    @Override
    public <S extends Posts> int save(S entity) {
        int n;
        if (entity.getParentId() == null) {
            entity.setParentId(0L);
        }
        if (entity.getCreator() == null) {
            entity.setCreator(0L);
        }

        // 爬取 机翻 原创 都只能有一条
        Posts.PostType postType = Posts.PostType.from(entity.getPostType());
        if (Posts.PostType.CRAWLER == postType ||
                Posts.PostType.MACHINE_TRANSLATION == postType ||
                Posts.PostType.ORIGINAL == postType) {
            // TODO 增加修改版本号, 此处操作不是线程安全的
            Posts one = new Posts();
            one.setTopicId(entity.getTopicId());
            one.setEnabled(1);
            one.setParentId(entity.getParentId());
            one.setCreator(entity.getCreator());

            one = super.findOne(one, Fields.builder().add(BaseEntity.ID).build());
            if (one == null) {
                n = super.save(entity);
            } else {
                entity.setId(one.getId());
                n = super.update(entity);
            }
        } else {
            entity.setEnabled(0);//未审核的翻译enabled=0
            PostsText parent = Services.findOne(PostsText.class, entity.getParentId(), Fields.builder().add(PostsText.CATEGORY).add(PostsText.TAG).build());
            if (parent != null) {//精翻的时候要把父文档的分类和标签复制过来
                entity.getPostsText().setCategory(parent.getCategory());
                entity.getPostsText().setTag(parent.getTag());
            }
            n = super.save(entity);
            if (entity.getState() != null) {//当state不为null时再去操作Moderation类
                Moderation mode = new Moderation();
                mode.setPostId(entity.getId());
                mode.setState(entity.getState());
                mode.setModifier(entity.getModifier());
                mode.setCreator(entity.getCreator());
                Services.save(Moderation.class, mode);//当前只有翻译需要审核，所以保存审核数据
            }
        }

        PostsText text = entity.getPostsText();
        if (text != null) {
            text.setId(entity.getId());
            if (entity.getFeatures() != null) {
                String feature = BeanUtils.writeValueAsString(entity.getFeatures());
                text.setFeature(feature);
            }
            Services.save(PostsText.class, text);
        }
        return n;
    }

    @Override
    public int deleteHumanTranslate(Long id) {
        Moderation mode = new Moderation();
        mode.setPostId(id);
        mode = moderationService.findOne(mode);
        int n = 0;
        if (mode != null) {
            Moderation update = new Moderation();
            update.setId(mode.getId());
            update.setState(9);
            n += moderationService.update(update);
        }
        return n;
    }

    @Override
    public int enabled(Long id) {//场景：审核翻译时，如果通过审核，需要update该posts使enabled=1
        Posts posts = new Posts();
        posts.setId(id);
        posts.setEnabled(1);
        return super.update(posts);
    }

    @Override
    public <S extends Posts> int update(S entity) {//场景：1.用户更新自己的翻译或者文章 
        Posts one = this.findOne(entity.getId());
        Posts.PostType postType = Posts.PostType.from(one.getPostType());
        if (Posts.PostType.HUMAN_TRANSLATION == postType && entity.getState() != null) {//当前只有翻译需要审核;当state不为null时再去操作Moderation类
            Moderation mode = new Moderation();
            mode.setState(entity.getState());
            mode.setPostId(entity.getId());
            mode.setModifier(entity.getModifier());
            int index = moderationService.updateByPostId(mode);//判断当前状态及更新
            if (index == 0) {
                throw new IllegalStateException("该数据不能修改");
            }

            entity.setEnabled(0);
        }

        int n = super.update(entity);
        PostsText text = entity.getPostsText();
        if (text != null) {
            text.setId(entity.getId());
            if (entity.getFeatures() != null) {
                String feature = BeanUtils.writeValueAsString(entity.getFeatures());
                text.setFeature(feature);
            }
            n += Services.save(PostsText.class, text);
        }

        return n;
    }
}
