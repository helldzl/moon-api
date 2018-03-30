package com.mifan.user.dao;

import com.mifan.user.domain.Users;
import org.moonframework.model.mybatis.repository.BaseDao;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/11/26
 */
public interface UsersDao extends BaseDao<Users> {

    /**
     * <p>登录次数自增</p>
     *
     * @param id
     * @return
     */
    int increase(Long id);

}
