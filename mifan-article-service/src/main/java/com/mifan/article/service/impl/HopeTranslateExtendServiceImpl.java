package com.mifan.article.service.impl;

import com.mifan.article.dao.HopeTranslateExtendDao;
import com.mifan.article.domain.HopeTranslate;
import com.mifan.article.domain.HopeTranslateExtend;
import com.mifan.article.domain.Topics;
import com.mifan.article.service.HopeTranslateExtendService;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pair;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-09-01
 */
@Service
public class HopeTranslateExtendServiceImpl extends AbstractBaseService<HopeTranslateExtend, HopeTranslateExtendDao> implements HopeTranslateExtendService {

    @Override
    public HopeTranslateExtend queryForObject(Long id, Iterable<? extends Field> fields) {
        HopeTranslateExtend extend = super.queryForObject(id, fields);
        if (extend != null) {
            Topics topics = Services.findOne(Topics.class, extend.getTopicId(), Fields.builder().add(Topics.TITLE).add(Topics.ID).build());
            if (topics != null) {
                extend.setTitle(topics.getTitle());
            }
            HopeTranslate find = new HopeTranslate();
            find.setExtendId(id);
            List<HopeTranslate> hopeList = Services.findAll(HopeTranslate.class, find);
            extend.setHopes(hopeList);
        }

        return extend;
    }

    @Override
    public Page<HopeTranslateExtend> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Page<HopeTranslateExtend> page = super.findAll(criterion, pageable, fields);
        if (page.hasContent()) {
            List<HopeTranslateExtend> extendList = page.getContent();
            Set<Long> topicIds = new HashSet<Long>();
            extendList.forEach(e -> topicIds.add(e.getTopicId()));
            List<Topics> topics = Services.findAll(Topics.class, topicIds, Fields.builder().add(Topics.TITLE).add(Topics.ID).build());
            Map<Long, Topics> topicMap = topics.stream().collect(Collectors.toMap(BaseEntity::getId, topic -> topic));
            extendList.forEach(e -> e.setTitle(topicMap.get(e.getTopicId()).getTitle()));
        }
        return page;
    }

    @Override
    public int save(HopeTranslateExtend entity) {
        // id must null
        HopeTranslateExtend find = new HopeTranslateExtend();
        find.setTopicId(entity.getTopicId());
        find.setState(HopeTranslateExtend.HopeState.ready.getIndex());
        find = findOne(find);
        if (find == null) {
            Topics topic = new Topics(entity.getTopicId());
            topic.setModerated(1);

            // [record lock] get
            int update = Services.update(Topics.class, topic, Restrictions.eq(Topics.MODERATED, 0));
            if (update > 0) {
                entity.setState(HopeTranslateExtend.HopeState.ready.getIndex());
                entity.setUserCount(1);
                super.save(entity);
            } else {
                // [record lock] is already get by other thread
                throw new IllegalStateException("系统繁忙, 请稍后再提交");
            }
        } else {
            // count++
            update(find.getId(), Pair.builder()
                    .add(HopeTranslateExtend.USER_COUNT, 1)
                    .build(), null);
            entity.setId(find.getId());//该id会保存在用户希望翻译表中
        }

        return 1;
    }

    @Override
    public <S extends HopeTranslateExtend> int update(S entity) {
        // id not null
        int update = 0;
        if (entity.getState() != null && entity.getState() != HopeTranslateExtend.HopeState.ready.getIndex()) {
            update = update(entity, Restrictions.eq(HopeTranslateExtend.STATE, HopeTranslateExtend.HopeState.ready.getIndex()));
            if (update > 0) {
                //[record lock] release
                Topics topic = new Topics(entity.getTopicId());
                topic.setModerated(0);
                Services.update(Topics.class, topic);
            }
        }
        return update;

    }
}
