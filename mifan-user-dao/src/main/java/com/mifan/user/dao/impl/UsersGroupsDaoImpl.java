package com.mifan.user.dao.impl;

import com.mifan.user.dao.UsersGroupsDao;
import com.mifan.user.domain.UsersGroups;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UsersGroupsDaoImpl extends AbstractBaseDao<UsersGroups> implements UsersGroupsDao {

    @Override
    public List<UsersGroups> findAll(Long siteId, Long userId) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("siteId", siteId);
        map.put("userId", userId);
        return session.selectList("com.mifan.user.domain.UsersGroups.findUsersGroups", map);
    }
}

