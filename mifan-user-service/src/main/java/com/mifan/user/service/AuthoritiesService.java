package com.mifan.user.service;

import com.mifan.user.domain.Authorities;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.service.BaseService;

import java.util.List;
import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/3
 */
public interface AuthoritiesService extends BaseService<Authorities> {

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
     * <p>根据角色查找权限</p>
     *
     * @param roleIds roleIds
     * @return Authorities
     */
    Set<Authorities> findAuthoritiesByRoles(Set<Long> roleIds);

}
