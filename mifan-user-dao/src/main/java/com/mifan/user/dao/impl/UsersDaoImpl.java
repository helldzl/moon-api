package com.mifan.user.dao.impl;

import com.mifan.user.dao.UsersDao;
import com.mifan.user.domain.Users;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/11/26
 */
@Repository
public class UsersDaoImpl extends AbstractBaseDao<Users> implements UsersDao {

    @Override
    public int increase(Long id) {
        return session.update("com.mifan.user.domain.Users.increase", id);
    }

}
