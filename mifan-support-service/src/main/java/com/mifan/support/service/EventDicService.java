/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.sku.service.EventDicService
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
package com.mifan.support.service;

import org.moonframework.model.mybatis.service.BaseService;

import com.mifan.support.domain.EventDic;

/**
 * @author ZYW
 *
 */
public interface EventDicService extends BaseService<EventDic>{
    /**
     * 根据编码判断该编码是否存在
     * @param eventCode
     * @return
     */
    boolean exists(String eventCode);
}
