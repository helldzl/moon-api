/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.dao.impl.EventDicDaoImpl
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

import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

import com.mifan.support.dao.EventDicDao;
import com.mifan.support.domain.EventDic;

/**
 * @author ZYW
 *
 */
@Repository
public class EventDicDaoImpl extends AbstractBaseDao<EventDic> implements EventDicDao{

}
