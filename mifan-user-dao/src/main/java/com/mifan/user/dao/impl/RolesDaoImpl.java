package com.mifan.user.dao.impl;

import com.mifan.user.dao.RolesDao;
import com.mifan.user.domain.Roles;
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
public class RolesDaoImpl extends AbstractBaseDao<Roles> implements RolesDao {

    @Override
    public List<Roles> findRolesByUser(Long siteId, Long userId) {
        return findRolesByUser(siteId, userId, null);
    }

    @Override
    public List<Roles> findRolesByUser(Long siteId, Long userId, Iterable<? extends Field> fields) {
        if (fields == null) {
            fields = FIELDS_DEFAULT;
        }
        Map<String, Object> map = new HashMap<>(3);
        map.put("siteId", siteId);
        map.put("userId", userId);
        map.put("fields", fields);
        return session.selectList("com.mifan.user.domain.Roles.findRolesByUser", map);
    }

    @Override
    public List<Roles> findRolesByGroup(Long siteId, Long userId) {
        return findRolesByGroup(siteId, userId, null);
    }

    @Override
    public List<Roles> findRolesByGroup(Long siteId, Long userId, Iterable<? extends Field> fields) {
        if (fields == null) {
            fields = FIELDS_DEFAULT;
        }
        Map<String, Object> map = new HashMap<>(3);
        map.put("siteId", siteId);
        map.put("userId", userId);
        map.put("fields", fields);
        return session.selectList("com.mifan.user.domain.Roles.findRolesByGroup", map);
    }

    @Override
    public List<Roles> findRoles(Long groupId) {
        return findRoles(groupId, null);
    }

    @Override
    public List<Roles> findRoles(Long groupId, Iterable<? extends Field> fields) {
        if (fields == null) {
            fields = FIELDS_DEFAULT;
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("groupId", groupId);
        map.put("fields", fields);
        return session.selectList("com.mifan.user.domain.Roles.findRoles", map);
    }
}

