package com.mifan.article.service.impl;

import com.mifan.article.dao.HopeTranslateDao;
import com.mifan.article.domain.HopeTranslate;
import com.mifan.article.domain.HopeTranslateExtend;
import com.mifan.article.domain.Topics;
import com.mifan.article.service.HopeTranslateExtendService;
import com.mifan.article.service.HopeTranslateService;
import com.mifan.article.service.TopicsService;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pair;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
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
public class HopeTranslateServiceImpl extends AbstractBaseService<HopeTranslate, HopeTranslateDao> implements HopeTranslateService {

    @Autowired
    private HopeTranslateExtendService hopeTranslateExtendService;

    @Autowired
    private TopicsService topicsService;


    @Override
    public Page<HopeTranslate> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Page<HopeTranslate> page = super.findAll(criterion, pageable, fields);
        if (page.hasContent()) {
            Set<Long> extendIds = new HashSet<Long>();
            Set<Long> topicIds = new HashSet<Long>();
            page.getContent().forEach(h -> extendIds.add(h.getExtendId()));

            List<HopeTranslateExtend> extendList = hopeTranslateExtendService.findAll(extendIds, Fields.builder().add(HopeTranslateExtend.ID).
                    add(HopeTranslateExtend.TOPIC_ID).add(HopeTranslateExtend.FEED_BACK).add(HopeTranslateExtend.WHO).add(HopeTranslateExtend.STATE).build());
            Map<Long, HopeTranslateExtend> map = extendList.stream().collect(Collectors.toMap(BaseEntity::getId, extend -> extend));
            page.getContent().forEach(h -> h.setExtend(map.get(h.getExtendId())));

            extendList.forEach(e -> topicIds.add(e.getTopicId()));

            List<Topics> topics = topicsService.findAll(topicIds, Fields.builder().add(Topics.TITLE).add(Topics.ID).build());
            Map<Long, Topics> topicMap = topics.stream().collect(Collectors.toMap(BaseEntity::getId, topic -> topic));
            extendList.forEach(e -> e.setTitle(topicMap.get(e.getTopicId()).getTitle()));
        }
        return page;
    }

    @Override
    public int remove(Long id) {
        HopeTranslate find = new HopeTranslate(id);
        find.setEnabled(0);
        int update = update(find, Restrictions.eq(BaseEntity.ENABLED, 1));
        if (update > 0) {
            HopeTranslate one = this.findOne(id, Fields.builder().add(HopeTranslate.EXTEND_ID).build());
            Services.update(HopeTranslateExtend.class,
                    one.getExtendId(),
                    Pair.builder().add(HopeTranslateExtend.USER_COUNT, -1).build(),
                    null);
        }
        return update;
    }

    @Override
    public <S extends HopeTranslate> int save(S entity) {
//        entity.setExtendId(0l);
//        int n = super.save(entity);
        HopeTranslateExtend extend = entity.getExtend();
        int n = hopeTranslateExtendService.save(extend);

        /*HopeTranslate hope = new HopeTranslate();
        hope.setExtendId(extend.getId());
        hope.setUserId(entity.getUserId());
        hope.setEnabled(1);
        if(this.exists(hope))
            throw new IllegalStateException("请勿多次操作");*/

        entity.setExtendId(extend.getId());
        entity.setEnabled(1);
//        n += super.update(entity);
        n += super.saveOrUpdate(entity);

        return n;
    }

}
