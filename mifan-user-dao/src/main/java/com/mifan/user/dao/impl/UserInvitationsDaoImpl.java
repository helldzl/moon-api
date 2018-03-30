package com.mifan.user.dao.impl;

import com.mifan.user.dao.UserInvitationsDao;
import com.mifan.user.domain.UserInvitations;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/31
 */
@Repository
public class UserInvitationsDaoImpl extends AbstractBaseDao<UserInvitations> implements UserInvitationsDao {
}
