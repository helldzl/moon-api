package com.mifan.user.service.impl;

import com.mifan.user.dao.UserInvitationsDao;
import com.mifan.user.domain.UserInvitations;
import com.mifan.user.domain.UserProfiles;
import com.mifan.user.service.UserInvitationsService;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/31
 */
@Service
public class UserInvitationsServiceImpl extends AbstractBaseService<UserInvitations, UserInvitationsDao> implements UserInvitationsService {

    @Override
    public Page<UserInvitations> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Page<UserInvitations> page = super.findAll(criterion, pageable, fields);
        if (page.hasContent()) {
            Map<Long, UserProfiles> map = Services.findAll(UserProfiles.class,
                    Restrictions.in(UserProfiles.ID, page.getContent().stream().map(UserInvitations::getTargetId).collect(toSet()).toArray()),
                    Fields.builder()
                            .add(BaseEntity.ID)
                            .add(UserProfiles.NICKNAME)
                            .build())
                    .stream()
                    .collect(Collectors.toMap(BaseEntity::getId, profile -> profile));

            for (UserInvitations invitation : page) {
                invitation.setTarget(map.get(invitation.getTargetId()));
            }
        }
        return page;
    }

}
