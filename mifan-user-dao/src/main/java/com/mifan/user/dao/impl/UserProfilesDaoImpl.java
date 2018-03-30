package com.mifan.user.dao.impl;

import com.mifan.user.dao.UserProfilesDao;
import com.mifan.user.domain.UserProfiles;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/11/27
 */
@Repository
public class UserProfilesDaoImpl extends AbstractBaseDao<UserProfiles> implements UserProfilesDao {
}
