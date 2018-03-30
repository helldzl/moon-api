package com.mifan.user.dao.impl;

import com.mifan.user.dao.GroupsDao;
import com.mifan.user.domain.Groups;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/5
 */
@Repository
public class GroupsDaoImpl extends AbstractBaseDao<Groups> implements GroupsDao {

    @Override
    public List<Groups> findGroups(Long siteId, Long userId) {
        return findGroups(siteId, userId, null);
    }

    @Override
    public List<Groups> findGroups(Long siteId, Long userId, Iterable<? extends Field> fields) {
        if (fields == null) {
            fields = FIELDS_DEFAULT;
        }
        Map<String, Object> map = new HashMap<>(3);
        map.put("siteId", siteId);
        map.put("userId", userId);
        map.put("fields", fields);
        return session.selectList("com.mifan.user.domain.Groups.findGroups", map);
    }

}

