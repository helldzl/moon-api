package com.mifan.user.service.impl;

import com.mifan.user.dao.RolesAuthoritiesDao;
import com.mifan.user.dao.RolesDao;
import com.mifan.user.dao.UsersRolesDao;
import com.mifan.user.domain.Roles;
import com.mifan.user.domain.RolesAuthorities;
import com.mifan.user.domain.UsersRoles;
import com.mifan.user.service.RolesService;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.util.EntityClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/2
 */
@Service
public class RolesServiceImpl extends AbstractBaseService<Roles, RolesDao> implements RolesService {

    @Autowired
    private RolesAuthoritiesDao rolesAuthoritiesDao;

    @Autowired
    private UsersRolesDao usersRolesDao;

    @Override
    public int delete(Long id) {
        return remove(id);
    }

    /**
     * <p>移除角色</p>
     * <p>判断是否存在关联关系</p>
     *
     * @param id must not be {@literal null}.
     * @return int
     */
    @Override
    public int remove(Long id) {
        UsersRoles usersRoles = new UsersRoles();
        usersRoles.setRoleId(id);

        RolesAuthorities rolesAuthorities = new RolesAuthorities();
        rolesAuthorities.setRoleId(id);

        if (!usersRolesDao.exists(usersRoles) && !rolesAuthoritiesDao.exists(rolesAuthorities)) {
            return baseDao.remove(id);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public List<Roles> findRoles(Long groupId) {
        return baseDao.findRoles(groupId);
    }

    @Override
    public List<Roles> findRoles(Long groupId, Iterable<? extends Field> fields) {
        return baseDao.findRoles(groupId, fields);
    }

    @Override
    public List<Roles> findRolesByUser(Long siteId, Long userId) {
        return baseDao.findRolesByUser(siteId, userId);
    }

    @Override
    public List<Roles> findRolesByUser(Long siteId, Long userId, Iterable<? extends Field> fields) {
        return baseDao.findRolesByUser(siteId, userId, fields);
    }


    @Override
    public int addAuthorities(Long roleId, List<Long> authorityIds) {
        try {
            // 首先根据角色查找拥有的所有权限
            RolesAuthorities rolesAuthorities = new RolesAuthorities();
            rolesAuthorities.setRoleId(roleId);
            List<RolesAuthorities> primary = rolesAuthoritiesDao.findAll(rolesAuthorities);

            //
            List<RolesAuthorities> secondary = new ArrayList<>();
            authorityIds.forEach(authorityId -> secondary.add(new RolesAuthorities(roleId, authorityId)));

            // execute
            EntityClassifier<RolesAuthorities> classifier = new EntityClassifier<>(primary, secondary, "authorityId");
            int[] n1 = rolesAuthoritiesDao.delete(classifier.getDeletes());
            int[] n2 = rolesAuthoritiesDao.save(classifier.getInserts());
            return result(n1) | result(n2);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
