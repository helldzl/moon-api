package com.mifan.user.dao.impl;

import com.mifan.user.dao.UsersRolesDao;
import com.mifan.user.domain.UsersRoles;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UsersRolesDaoImpl extends AbstractBaseDao<UsersRoles> implements UsersRolesDao {

    @Override
    public List<UsersRoles> findAll(Long siteId, Long userId) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("siteId", siteId);
        map.put("userId", userId);
        return session.selectList("com.mifan.user.domain.UsersRoles.findUsersRoles", map);
    }
}

