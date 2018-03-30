package com.mifan.article.service.impl;

import com.mifan.article.cache.UsersCache;
import com.mifan.article.dao.UsersTopicsHideDao;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.UsersTopicsHide;
import com.mifan.article.service.UsersTopicsHideService;
import org.apache.shiro.authz.UnauthenticatedException;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.utils.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class UsersTopicsHideServiceImpl extends AbstractBaseService<UsersTopicsHide, UsersTopicsHideDao> implements UsersTopicsHideService {

    private static final int MAX_SIZE = 2048;

    @Autowired
    private UsersCache usersCache;

    /**
     * <p>POST, PATCH 语义</p>
     *
     * @param insert insert
     * @param update update
     * @param <S>    S
     * @return int
     */
    @Override
    public <S extends UsersTopicsHide> int saveOrUpdate(S insert, S update) {
        if (!Services.exists(Topics.class, insert.getTopicId())) {
            return 0;
        }

        UsersTopicsHide unique = new UsersTopicsHide();
        unique.setUserId(insert.getUserId());
        unique.setTopicId(insert.getTopicId());
        return Services.saveOrUpdate(UsersTopicsHide.class, unique, insert, update, action ->
                cache(Services.Action.INSERT == action, userId -> usersCache.add(UsersCache.Type.HIDE, userId, insert.getTopicId())));
    }

    /**
     * <p>DELETE 语义</p>
     *
     * @param ids ids
     * @return int
     */
    @Override
    public int remove(Iterable<Long> ids) {
        return Services.remove(UsersTopicsHide.class, ids, UsersTopicsHide.TOPIC_ID, hide ->
                cache(true, userId -> usersCache.remove(UsersCache.Type.HIDE, userId, hide.getTopicId())));
    }

    @Override
    public Set<String> findByUserId(Long userId) {
        init(userId);
        Set<String> result = usersCache.members(UsersCache.Type.HIDE, userId);
        result.remove("0");
        return result;
    }

    private void cache(boolean changed, Consumer<Long> consumer) {
        if (changed) {
            Long userId = SecurityContextUtils.currentUserId();
            if (userId == null) {
                throw new UnauthenticatedException();
            }
            init(userId);
            consumer.accept(userId);
        }
    }

    /**
     * <p>线程安全的初始化数据</p>
     *
     * @param userId userId
     * @return boolean
     */
    private boolean init(Long userId) {
        if (usersCache.add(UsersCache.Type.HIDE, userId, 0L) == 1L) {
            Page<UsersTopicsHide> page;
            Pageable pageable = Pages.builder().page(1).size(128).build();
            Criterion criterion = Restrictions.and(Restrictions.eq(UsersTopicsHide.USER_ID, userId), Restrictions.eq(BaseEntity.ENABLED, 1));
            List<Field> fields = Fields.builder().add(UsersTopicsHide.TOPIC_ID).build();
            int n = 0;
            do {
                page = findAll(criterion, pageable, fields);
                for (UsersTopicsHide usersTopicsHide : page) {
                    Long topicId = usersTopicsHide.getTopicId();
                    usersCache.add(UsersCache.Type.HIDE, userId, topicId);
                    n++;
                }
            } while (page.hasNext() && (pageable = pageable.next()) != null && n < MAX_SIZE);
            return true;
        } else {
            return false;
        }
    }

}
