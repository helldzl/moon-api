package com.mifan.user.dao.impl;

import com.mifan.user.dao.AuthoritiesDao;
import com.mifan.user.domain.Authorities;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/3
 */
@Repository
public class AuthoritiesDaoImpl extends AbstractBaseDao<Authorities> implements AuthoritiesDao {


    @Override
    public List<Authorities> findAuthorities(Long roleId) {
        return findAuthorities(roleId, null);
    }

    @Override
    public List<Authorities> findAuthorities(Long roleId, Iterable<? extends Field> fields) {
        if (fields == null) {
            fields = FIELDS_DEFAULT;
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("roleId", roleId);
        map.put("fields", fields);
        return session.selectList("com.mifan.user.domain.Authorities.findAuthorities", map);
    }

    @Override
    public List<Authorities> findAuthoritiesByRoles(Iterable<Long> roleIds) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("roleIds", roleIds);
        return session.selectList("com.mifan.user.domain.Authorities.findAuthoritiesByRoles", map);
    }

}
