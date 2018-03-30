package com.mifan.user.dao.impl;

import com.mifan.user.dao.AuthOperationsDao;
import com.mifan.user.domain.AuthOperations;
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
public class AuthOperationsDaoImpl extends AbstractBaseDao<AuthOperations> implements AuthOperationsDao {

    @Override
    public List<AuthOperations> findByAuthorities(Iterable<Long> authorityIds) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("authorityIds", authorityIds);
        return session.selectList("com.mifan.user.domain.AuthOperations.findByAuthorities", map);
    }
}
