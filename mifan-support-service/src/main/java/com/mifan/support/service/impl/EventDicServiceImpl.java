/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.service.impl.EventDicServiceImpl
 * @description:
 * @version:v0.0.1
 * @author:ZYW Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月21日 ZYW v0.0.1 create
 */
package com.mifan.support.service.impl;

import com.mifan.support.dao.EventDicDao;
import com.mifan.support.domain.EventDic;
import com.mifan.support.service.EventDicService;

import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author ZYW
 *
 */
@Service
public class EventDicServiceImpl extends AbstractBaseService<EventDic, EventDicDao> implements EventDicService {

    @Override
    public boolean exists(String eventCode) {
        EventDic dic = new EventDic();
        dic.setEventCode(eventCode);
        dic.setEnabled(1);
        return super.exists(dic);
//        dic = baseDao.findOne(dic, Fields.builder().add(EventDic.ID).add(EventDic.EVENT_CODE).build());
//
//        if(dic != null && dic.getId() != null){
//            return true;
//        }
//        return false;
    }
}
