/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.sku.service.CommentConfService
 *
 * @description:TODO
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年5月22日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.support.service;

import org.moonframework.model.mybatis.service.BaseService;

import com.mifan.support.domain.CommentConf;

/**
 * @author ZYW
 *
 */
public interface CommentConfService extends BaseService<CommentConf> {
    /**
     * 根据id获取评论配置详细信息，包括标签信息
     * @param id
     * @return
     */
    CommentConf details(Long id);
}
