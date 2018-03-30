package com.mifan.user.dao;

import com.mifan.user.domain.AuthOperations;
import org.moonframework.model.mybatis.repository.BaseDao;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/3
 */
public interface AuthOperationsDao extends BaseDao<AuthOperations> {

    /**
     * <p>根据权限查找详细信息</p>
     *
     * @param authorityIds 权限ID集合
     * @return
     */
    List<AuthOperations> findByAuthorities(Iterable<Long> authorityIds);

}
