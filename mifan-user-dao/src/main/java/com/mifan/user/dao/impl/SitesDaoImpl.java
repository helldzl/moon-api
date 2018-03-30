package com.mifan.user.dao.impl;

import com.mifan.user.dao.SitesDao;
import com.mifan.user.domain.Sites;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/2
 */
@Repository
public class SitesDaoImpl extends AbstractBaseDao<Sites> implements SitesDao {

    @Override
    public int delete(Sites entity) {
        // 禁止删除
        return 0;
    }

    @Override
    public int delete(Long id) {
        // 禁止删除
        return 0;
    }

    @Override
    public int[] delete(Iterable<Long> ids) {
        // 禁止删除
        return null;
    }
}
