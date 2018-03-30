package com.mifan.user.dao.impl;

import com.mifan.user.dao.RolesAuthoritiesDao;
import com.mifan.user.domain.RolesAuthorities;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/2
 */
@Repository
public class RolesAuthoritiesDaoImpl extends AbstractBaseDao<RolesAuthorities> implements RolesAuthoritiesDao {
}
