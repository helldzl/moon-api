/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.sku.dao.EventLogDao
 *
 * @description:事件日志
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
package com.mifan.support.dao;

import java.util.List;

import org.moonframework.model.mybatis.repository.BaseDao;

import com.mifan.support.domain.EventLog;

/**
 * @author ZYW
 *
 */
public interface EventLogDao extends BaseDao<EventLog>{
    List<EventLog> findPageViews(String startTime,String endTime,int size);
}
