package com.mifan.user.dao.impl;

import com.mifan.user.dao.UserAddressesDao;
import com.mifan.user.domain.UserAddresses;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/31
 */
@Repository
public class UserAddressesDaoImpl extends AbstractBaseDao<UserAddresses> implements UserAddressesDao {
}
