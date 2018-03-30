package com.mifan.user.dao;

import com.mifan.user.domain.UsersRoles;
import org.moonframework.model.mybatis.repository.BaseDao;

import java.util.List;

public interface UsersRolesDao extends BaseDao<UsersRoles> {

    /**
     * <p>根据用户ID和站点ID找所有关联</p>
     *
     * @param siteId 站点ID
     * @param userId 用户ID
     * @return Roles
     */
    List<UsersRoles> findAll(Long siteId, Long userId);

}

