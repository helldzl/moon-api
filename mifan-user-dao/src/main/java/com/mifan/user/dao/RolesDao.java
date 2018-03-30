package com.mifan.user.dao;

import com.mifan.user.domain.Roles;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.repository.BaseDao;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/5
 */
public interface RolesDao extends BaseDao<Roles> {

    Iterable<? extends Field> FIELDS_DEFAULT = Fields.builder().add(Roles.ID, Roles.ID, Roles.TABLE_NAME).add(Roles.NAME, Roles.NAME, Roles.TABLE_NAME).add(Roles.SITE_ID, Roles.SITE_ID, Roles.TABLE_NAME).build();

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
     * <p>根据用户找其所在站点下用户群组中的角色集合</p>
     *
     * @param siteId 站点ID
     * @param userId 用户ID
     * @return Roles
     */
    List<Roles> findRolesByGroup(Long siteId, Long userId);

    /**
     * <p>根据用户找其所在站点下用户群组中的角色集合</p>
     *
     * @param siteId 站点ID
     * @param userId 用户ID
     * @param fields fields
     * @return Roles
     */
    List<Roles> findRolesByGroup(Long siteId, Long userId, Iterable<? extends Field> fields);

    /**
     * <p>根据群组ID找其包含的角色集合</p>
     *
     * @param groupId groupId
     * @return
     */
    List<Roles> findRoles(Long groupId);

    /**
     * <p>根据群组ID找其包含的角色集合</p>
     *
     * @param groupId groupId
     * @param fields  fields
     * @return
     */
    List<Roles> findRoles(Long groupId, Iterable<? extends Field> fields);

}

