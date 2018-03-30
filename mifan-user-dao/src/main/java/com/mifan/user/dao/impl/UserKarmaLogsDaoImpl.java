package com.mifan.user.dao.impl;

import com.mifan.user.dao.UserKarmaLogsDao;
import com.mifan.user.domain.UserKarmaLogs;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/5/18
 */
@Repository
public class UserKarmaLogsDaoImpl extends AbstractBaseDao<UserKarmaLogs> implements UserKarmaLogsDao {
}
