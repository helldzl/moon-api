package com.mifan.user.dao;

import com.mifan.user.domain.Authorities;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.repository.BaseDao;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/3
 */
public interface AuthoritiesDao extends BaseDao<Authorities> {

    Iterable<? extends Field> FIELDS_DEFAULT = Fields.builder().add(Authorities.ID, Authorities.ID, Authorities.TABLE_NAME).add(Authorities.NAME, Authorities.NAME, Authorities.TABLE_NAME).add(Authorities.SITE_ID, Authorities.SITE_ID, Authorities.TABLE_NAME).build();

    /**
     * <p>根据角色ID找其包含的权限或资源集合</p>
     *
     * @param roleId roleId
     * @return
     */
    List<Authorities> findAuthorities(Long roleId);

    /**
     * <p>根据角色ID找其包含的权限或资源集合</p>
     *
     * @param roleId roleId
     * @param fields fields
     * @return
     */
    List<Authorities> findAuthorities(Long roleId, Iterable<? extends Field> fields);

    /**
     * <p>根据角色查找所有权限</p>
     *
     * @param roleIds roleIds
     * @return 权限列表
     */
    List<Authorities> findAuthoritiesByRoles(Iterable<Long> roleIds);

}
