package com.mifan.user.dao;

import com.mifan.user.domain.Groups;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.repository.BaseDao;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/5
 */
public interface GroupsDao extends BaseDao<Groups> {

    Iterable<? extends Field> FIELDS_DEFAULT = Fields.builder().add(Groups.ID, Groups.ID, Groups.TABLE_NAME).add(Groups.GROUP_NAME, Groups.GROUP_NAME, Groups.TABLE_NAME).build();

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

}

