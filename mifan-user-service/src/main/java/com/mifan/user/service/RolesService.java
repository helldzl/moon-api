package com.mifan.user.service;

import com.mifan.user.domain.Roles;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.service.BaseService;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/2
 */
public interface RolesService extends BaseService<Roles> {

    /**
     * <p>根据群组ID找其包含的角色集合</p>
     *
     * @param groupId groupId
     * @return List
     */
    List<Roles> findRoles(Long groupId);

    /**
     * <p>根据群组ID找其包含的角色集合</p>
     *
     * @param groupId groupId
     * @param fields  fields
     * @return List
     */
    List<Roles> findRoles(Long groupId, Iterable<? extends Field> fields);

    /**
     * <p>根据用户找其所在站点下的角色集合</p>
     *
     * @param siteId 站点ID
     * @param userId 用户ID
     * @return Roles
     */
    List<Roles> findRolesByUser(Long siteId, Long userId);

    /**
     * <p>根据用户找其所在站点下的角色集合</p>
     *
     * @param siteId 站点ID
     * @param userId 用户ID
     * @param fields fields
     * @return Roles
     */
    List<Roles> findRolesByUser(Long siteId, Long userId, Iterable<? extends Field> fields);

    /**
     * <p>添加角色关联的权限</p>
     * <p>添加新增加的的, 删除没有的</p>
     *
     * @param roleId       角色ID
     * @param authorityIds 权限ID集合
     */
    int addAuthorities(Long roleId, List<Long> authorityIds);

}
