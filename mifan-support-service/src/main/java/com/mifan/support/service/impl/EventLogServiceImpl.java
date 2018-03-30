/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.service.impl.EventLogServiceImpl
 *
 * @description:TODO
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月21日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.support.service.impl;

import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

import com.mifan.support.dao.EventLogDao;
import com.mifan.support.domain.EventLog;
import com.mifan.support.service.EventLogService;

/**
 * @author ZYW
 *
 */
@Service
public class EventLogServiceImpl extends AbstractBaseService<EventLog,EventLogDao> implements EventLogService{
    
    @Override
    public int save(EventLog eventLog) {
        /*if(eventDicService.exists(eventLog.getEventCode())){
            eventLog.setCreated(new Date());
            isTrue(baseDao.save(eventLog) > 0, "Error.eventlog.save");
            return 1;
        }
        return 0;*/
        return super.save(eventLog);
    }
}
