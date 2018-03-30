/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.dao.impl.EventLogDaoImpl
 *
 * @description:
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
package com.mifan.support.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

import com.mifan.support.dao.EventLogDao;
import com.mifan.support.domain.EventLog;

/**
 * @author ZYW
 *
 */
@Repository
public class EventLogDaoImpl extends AbstractBaseDao<EventLog> implements EventLogDao{

    @Override
    public List<EventLog> findPageViews(String startTime, String endTime, int size) {
        String findPageViews = entityClass.getName() + "." + "findPageViews";
        Map<String, Object> map = new HashMap<String,Object>(16);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("size", size);
        List<EventLog> list = session.selectList(findPageViews, map);
        for(EventLog pv : list) {
            String regex = "^[0-9]+$";
            String urlLog = pv.getUrlLog();
            String topicId;
            if(urlLog.indexOf("/") > 0) {
                topicId = urlLog.substring(urlLog.lastIndexOf("/") + 1);
            }else {
                topicId = urlLog;
            }
            if(Pattern.matches(regex,topicId)) {
                pv.setTopicId(Long.valueOf(topicId));
            }
        }
        list = list.stream().filter(pv -> pv.getTopicId() != null).collect(Collectors.toList());
        return list;
    }

}
