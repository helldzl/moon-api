package com.mifan.article.service.impl;

import com.mifan.article.dao.UsersTopicsCompareDao;
import com.mifan.article.domain.Folders;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.UsersTopicsCompare;
import com.mifan.article.service.UsersTopicsCompareService;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-12-12
 */
@Service
public class UsersTopicsCompareServiceImpl extends AbstractBaseService<UsersTopicsCompare, UsersTopicsCompareDao> implements UsersTopicsCompareService {

    @Override
    public <S extends UsersTopicsCompare> int saveOrUpdate(S insert, S update) {
        Long userId = insert.getUserId();
        Long folderId = insert.getFolderId();
        Long topicId = insert.getTopicId();
        List<Long> topicIds = insert.getTopicIds();

        notNull(userId);
        notNull(folderId);
        Set<Long> ids = new HashSet<>();
        if (topicId != null) {
            ids.add(topicId);
        } else if (!CollectionUtils.isEmpty(topicIds)) {
            ids.addAll(topicIds);
        } else {
            throw new IllegalArgumentException("主题ID不能为空");
        }

        Set<Long> set = Services.findAll(Topics.class, Restrictions.in(BaseEntity.ID, ids.toArray()), Fields.builder().add(BaseEntity.ID).build()).stream().map(Topics::getId).collect(Collectors.toSet());
        if (!set.containsAll(ids)) {
            ids.removeAll(set);
            throw new IllegalArgumentException(String.format("不存在的主题ID: %s", ids.toString()));
        }

        if (!Services.exists(Folders.class, Restrictions.and(
                Restrictions.eq(Folders.ID, folderId),
                Restrictions.eq(Folders.FOLDER_TYPE, Folders.FolderType.COMPARE.getIndex()),
                Restrictions.eq(Folders.CREATOR, userId),
                Restrictions.eq(Folders.ENABLED, 1)))) {
            throw new IllegalArgumentException(String.format("不存在的目录ID: %s", folderId));
        }

        int n = 0;
        try {
            for (Long id : ids) {
                UsersTopicsCompare unique = new UsersTopicsCompare();
                unique.setUserId(userId);
                unique.setFolderId(folderId);
                unique.setTopicId(id);

                UsersTopicsCompare clone = unique.clone();
                clone.setEnabled(1);

                n += Services.saveOrUpdate(UsersTopicsCompare.class, unique, clone, clone, action -> {
                    switch (action) {
                        case INSERT:
                            Folders folder = new Folders(folderId);
                            folder.setIncrement(1);
                            Services.update(Folders.class, folder);
                            break;
                        default:
                    }
                });
            }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        return n;
    }

    @Override
    public int remove(Iterable<Long> ids) {
        ids.forEach(this::remove);
        return 1;
    }

    @Override
    public int remove(Iterable<Long> ids, Criterion criterion) {
        ids.forEach(id -> remove(id, criterion));
        return 1;
    }

    @Override
    public int remove(Long id) {
        return remove(id, null);
    }

    @Override
    public int remove(Long id, Criterion criterion) {
        UsersTopicsCompare compare = super.findOne(id, Fields.builder().add(UsersTopicsCompare.FOLDER_ID).build());
        if (!Services.exists(Folders.class, Restrictions.and(
                Restrictions.eq(Folders.ID, compare.getFolderId()),
                Restrictions.eq(Folders.FOLDER_TYPE, Folders.FolderType.COMPARE.getIndex())))) {
            return 0;
        }

        if (criterion == null) {
            criterion = Restrictions.eq(BaseEntity.ENABLED, 1);
        }

        // 只能删除特定目录类型的数据
        int n = super.remove(id, criterion);
        if (n > 0) {
            Folders folder = new Folders(compare.getFolderId());
            folder.setIncrement(-1);
            Services.update(Folders.class, folder);
        }
        return n;
    }

    @Override
    public Page<UsersTopicsCompare> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Page<UsersTopicsCompare> page = super.findAll(criterion, pageable, fields);
        if (page.hasContent()) {
            Map<Long, Topics> map = Services.findAll(
                    Topics.class,
                    page.getContent().stream().map(UsersTopicsCompare::getTopicId).collect(Collectors.toSet()),
                    Fields.builder()
                            .add(Topics.ID)
                            .add(Topics.TITLE)
                            .build()).stream().collect(Collectors.toMap(Topics::getId, Function.identity()));
            for (UsersTopicsCompare compare : page) {
                compare.setTopic(map.get(compare.getTopicId()));
            }
        }
        return page;
    }

}
