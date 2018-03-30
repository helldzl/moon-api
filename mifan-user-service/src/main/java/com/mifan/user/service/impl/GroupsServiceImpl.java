package com.mifan.user.service.impl;

import com.mifan.user.dao.GroupsDao;
import com.mifan.user.dao.GroupsRolesDao;
import com.mifan.user.dao.UsersGroupsDao;
import com.mifan.user.domain.Groups;
import com.mifan.user.domain.GroupsRoles;
import com.mifan.user.domain.UsersGroups;
import com.mifan.user.service.GroupsService;
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
 * @since 2015/12/3
 */
@Service
public class GroupsServiceImpl extends AbstractBaseService<Groups, GroupsDao> implements GroupsService {

    @Autowired
    private UsersGroupsDao usersGroupsDao;

    @Autowired
    private GroupsRolesDao groupsRolesDao;

    @Override
    public int delete(Long id) {
        return remove(id);
    }

    @Override
    public int addRoles(Long groupId, List<Long> roleIds) {
        try {
            // 根据groupId查找用户组下所有角色
            GroupsRoles groupsRoles = new GroupsRoles();
            groupsRoles.setGroupId(groupId);
            List<GroupsRoles> primary = groupsRolesDao.findAll(groupsRoles);

            //
            List<GroupsRoles> secondary = new ArrayList<>();
            roleIds.forEach(roleId -> secondary.add(new GroupsRoles(groupId, roleId)));

            // execute
            EntityClassifier<GroupsRoles> classifier = new EntityClassifier<>(primary, secondary, "roleId");
            int[] n1 = groupsRolesDao.delete(classifier.getDeletes());
            int[] n2 = groupsRolesDao.save(classifier.getInserts());
            return result(n1) | result(n2);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int remove(Long id) {
        UsersGroups usersGroups = new UsersGroups();
        usersGroups.setGroupId(id);

        GroupsRoles groupsRoles = new GroupsRoles();
        groupsRoles.setGroupId(id);

        if (!usersGroupsDao.exists(usersGroups) && !groupsRolesDao.exists(groupsRoles)) {
            return baseDao.remove(id);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public List<Groups> findGroups(Long siteId, Long userId) {
        return baseDao.findGroups(siteId, userId);
    }

    @Override
    public List<Groups> findGroups(Long siteId, Long userId, Iterable<? extends Field> fields) {
        return baseDao.findGroups(siteId, userId, fields);
    }
}

