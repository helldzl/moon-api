package com.mifan.user.service.impl;

import com.mifan.user.dao.UserProfilesDao;
import com.mifan.user.domain.UserKarmaLogs;
import com.mifan.user.domain.UserProfiles;
import com.mifan.user.service.UserProfilesService;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/04/05
 */
@Service
public class UserProfilesServiceImpl extends AbstractBaseService<UserProfiles, UserProfilesDao> implements UserProfilesService {

    @Override
    public <S extends UserProfiles> int update(S entity) {
        boolean completed = super.findOne(entity.getId()).isCompleted();
        int n = super.update(entity);
        if (!completed) {
            if (super.findOne(entity.getId()).isCompleted()) {
                UserKarmaLogs userKarmaLogs = new UserKarmaLogs();
                userKarmaLogs.setUserId(entity.getId());
                userKarmaLogs.setAction(UserKarmaLogs.Event.INFORMATION);
                Services.save(UserKarmaLogs.class, userKarmaLogs);
            }
        }
        return n;
    }

    @Override
    public Page<UserProfiles> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        return super.findAll(criterion, pageable, fields == null ? UserProfiles.AVATAR_FIELDS : fields);
    }

    @Override
    public Page<UserProfiles> findAllAdmin(Criterion criterion, Pageable pageable) {
        Iterable<? extends Field> fields = Fields.builder()
                .add(UserProfiles.ID)
                .add(UserProfiles.GENDER)
                .add(UserProfiles.NICKNAME)
                .add(UserProfiles.MOBILE)
                .add(UserProfiles.USER_KARMA)
                .add(UserProfiles.USER_LASTVISIT)
                .add(UserProfiles.MODIFIER)
                .add(UserProfiles.CREATOR)
                .add(UserProfiles.MODIFIED)
                .add(UserProfiles.CREATED).build();
        return super.findAll(criterion, pageable, fields);
    }

    @Override
    public UserProfiles findOneAdmin(Long id) {
        Iterable<? extends Field> fields = Fields.builder()
                .add(UserProfiles.ID)
                .add(UserProfiles.GENDER)
                .add(UserProfiles.BIRTHDAY)
                .add(UserProfiles.NICKNAME)
                .add(UserProfiles.FULLNAME)
                .add(UserProfiles.MOBILE)
                .add(UserProfiles.EMAIL)
                .add(UserProfiles.USER_KARMA)
                .add(UserProfiles.USER_LASTVISIT)
                .add(UserProfiles.USER_AVATAR)
                .add(UserProfiles.CITY_ID)
                .add(UserProfiles.COMPANY)
                .add(UserProfiles.PROFESSION)
                .add(UserProfiles.SIGN_COUNT)
                .add(UserProfiles.SIGN_COUNT_CONTINUOS)
                .add(UserProfiles.SIGN_TIME)
                .add(UserProfiles.MODIFIER)
                .add(UserProfiles.CREATOR)
                .add(UserProfiles.MODIFIED)
                .add(UserProfiles.CREATED).build();
        return super.findOne(id, fields);
    }
}
