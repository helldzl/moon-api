package com.mifan.user.service;

import com.mifan.user.domain.Groups;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.service.BaseService;

import java.util.List;

public interface GroupsService extends BaseService<Groups> {

    /**
     * @param siteId siteId
     * @param userId userId
     * @return Groups
     */
    List<Groups> findGroups(Long siteId, Long userId);

    /**
     * @param siteId siteId
     * @param userId userId
     * @param fields fields
     * @return Groups
     */
    List<Groups> findGroups(Long siteId, Long userId, Iterable<? extends Field> fields);

    /**
     * <p>添加用户组与角色关联关系</p>
     *
     * @param groupId 用户组ID
     * @param roleIds 角色ID集合
     * @return 0:ERROR 1:SUCCESS
     */
    int addRoles(Long groupId, List<Long> roleIds);
}

