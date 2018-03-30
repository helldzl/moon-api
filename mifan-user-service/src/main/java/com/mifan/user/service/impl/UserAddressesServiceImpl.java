package com.mifan.user.service.impl;

import com.mifan.user.dao.UserAddressesDao;
import com.mifan.user.domain.UserAddresses;
import com.mifan.user.service.UserAddressesService;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/31
 */
@Service
public class UserAddressesServiceImpl extends AbstractBaseService<UserAddresses, UserAddressesDao> implements UserAddressesService {

    @Override
    public int save(UserAddresses entity) {
        long count = baseDao.count(Restrictions.and(
                Restrictions.eq(UserAddresses.USER_ID, entity.getUserId()),
                Restrictions.eq(BaseEntity.ENABLED, 1)));
        if (count >= 5) {
            throw new IllegalStateException("收货地址不能超过5个");
        } else if (count == 0) {
            entity.setPriority(1);
        }
        return super.save(entity);
    }

    @Override
    public int update(UserAddresses entity) {
        int update = super.update(entity);
        UserAddresses one = super.findOne(entity.getId());
        Integer priority;
        if (update > 0 && (priority = entity.getPriority()) != null && priority.compareTo(0) > 0) {
            baseDao.findAll(Restrictions.and(
                    Restrictions.eq(UserAddresses.USER_ID, one.getUserId()),
                    Restrictions.eq(BaseEntity.ENABLED, 1),
                    Restrictions.gt(UserAddresses.PRIORITY, 0),
                    Restrictions.ne(BaseEntity.ID, entity.getId())),
                    Fields.builder().add(BaseEntity.ID).build())
                    .forEach(address -> {
                        address.setPriority(0);
                        super.update(address);
                    });
        }
        return update;
    }

}
