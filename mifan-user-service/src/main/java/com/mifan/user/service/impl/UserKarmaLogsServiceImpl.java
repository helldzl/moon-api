package com.mifan.user.service.impl;

import com.mifan.user.dao.UserKarmaLogsDao;
import com.mifan.user.domain.UserKarmaLogs;
import com.mifan.user.service.UserKarmaLogsService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/5/18
 */
@Service
public class UserKarmaLogsServiceImpl extends AbstractBaseService<UserKarmaLogs, UserKarmaLogsDao> implements UserKarmaLogsService {

    @Override
    public <S extends UserKarmaLogs> int save(S entity) {
        if (entity.getEvent().doService(entity) > 0) {
            return super.save(entity);
        }
        return 0;
    }

}
