package com.mifan.article.service.impl;

import com.mifan.article.dao.ModerationDao;
import com.mifan.article.domain.HopeTranslateExtend;
import com.mifan.article.domain.Moderation;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.PostsText;
import com.mifan.article.domain.support.UserKarmaLogs;
import com.mifan.article.feign.user.UserKarmaLogsClient;
import com.mifan.article.service.HopeTranslateExtendService;
import com.mifan.article.service.ModerationService;
import com.mifan.article.service.PostsService;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
@Service
public class ModerationServiceImpl extends AbstractBaseService<Moderation, ModerationDao> implements ModerationService {

    @Autowired
    private PostsService postsService;

    @Autowired
    private HopeTranslateExtendService hopeTranslateExtendService;

    @Autowired
    private UserKarmaLogsClient userKarmaLogsClient;

    @Override
    public Page<Moderation> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Page<Moderation> page = super.findAll(criterion, pageable, fields);
        if (page.hasContent()) {
            Set<Long> postIds = new HashSet<Long>();
            page.getContent().forEach(m -> postIds.add(m.getPostId()));
            List<PostsText> texts = Services.findAll(PostsText.class, postIds, Fields.builder().add(PostsText.TITLE).add(PostsText.ID).build());
            Map<Long, PostsText> map = texts.stream().collect(Collectors.toMap(BaseEntity::getId, text -> text));
            page.getContent().forEach(m -> m.setTitle(map.get(m.getPostId()).getTitle()));
        }

        return page;
    }

    @Override
    public int updateByPostId(Moderation entity) {
        Date date = new Date();
        entity.setModified(date);
        return baseDao.updateByPostId(entity);
    }

    @Override
    public int examineHumanTranslate(Moderation entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("关键数据缺失");
        }
        int n = super.update(entity, Restrictions.eq(Moderation.STATE, 2));
        if (n == 0) {
            throw new IllegalStateException("审核失败");
        }
        Moderation mode = this.findOne(entity.getId(), Fields.builder().add(Moderation.POST_ID).build());
        Posts one = Services.findOne(Posts.class, mode.getPostId());
        if (entity.getState() == 3) {
            n += postsService.enabled(mode.getPostId());

            if (one.getParentId() != 0) {//审核通过后应该设置之前过审的翻译为被覆盖状态
                List<Posts> postsList = Services.findAll(Posts.class, Restrictions.and(Restrictions.eq(Posts.PARENT_ID, one.getParentId()), Restrictions.ne(Posts.CREATOR, 0)),
                        Fields.builder().add(Posts.ID).build());
                postsList = postsList.stream().filter(p -> !p.getId().equals(one.getId())).collect(Collectors.toList());//过滤掉当前被过审的Posts
                if (postsList.size() > 0) {
                    Set<Long> postIds = new HashSet<Long>();
                    postsList.forEach(p -> postIds.add(p.getId()));
                    List<Moderation> modeList = this.findAll(Restrictions.and(Restrictions.in(Moderation.POST_ID, postIds.toArray()), Restrictions.eq(Moderation.STATE, 3)),
                            Fields.builder().add(Moderation.ID).build());
                    for (Moderation update : modeList) {
                        update.setState(5);
                        super.update(update);
                    }

                }
            }

            Posts.PostType postType = Posts.PostType.from(one.getPostType());
            if (Posts.PostType.HUMAN_TRANSLATION == postType) {
                HopeTranslateExtend find = new HopeTranslateExtend();
                find.setTopicId(one.getTopicId());
                find.setState(HopeTranslateExtend.HopeState.ready.getIndex());
                find = hopeTranslateExtendService.findOne(find, Fields.builder().add(HopeTranslateExtend.ID).build());
                if (find != null) {
                    HopeTranslateExtend extend = new HopeTranslateExtend();
                    extend.setId(find.getId());
                    extend.setWho(one.getCreator());
                    extend.setState(1);
                    n += hopeTranslateExtendService.update(extend);
                }
            }
        }
        if (entity.getRiceNum() != null && entity.getRiceNum() != 0) {//赠送米粒
            UserKarmaLogs userKarmaLogs = new UserKarmaLogs();
            userKarmaLogs.setUserId(one.getCreator());
            userKarmaLogs.setAction(UserKarmaLogs.Event.TRANSLATE);
            userKarmaLogs.setScore(entity.getRiceNum());

            // REMOTE CALL
            ResponseEntity<Data<Map<String, Object>>> response = userKarmaLogsClient.doPost(Data.of(userKarmaLogs));
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("奖励米粒失败");
            }

//            int index = Services.save(UserKarmaLogs.class, userKarmaLogs);
//            if(index == 0)
//                throw new IllegalStateException("奖励米粒失败");
        }
        return n;
    }
}
