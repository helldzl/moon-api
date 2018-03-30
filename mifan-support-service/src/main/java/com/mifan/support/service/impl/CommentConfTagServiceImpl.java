/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.service.impl.CommentConfTagServiceImpl
 *
 * @description:
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
package com.mifan.support.service.impl;

import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

import com.mifan.support.dao.CommentConfTagDao;
import com.mifan.support.domain.CommentConfTag;
import com.mifan.support.service.CommentConfTagService;

/**
 * @author ZYW
 *
 */
@Service
public class CommentConfTagServiceImpl extends AbstractBaseService<CommentConfTag, CommentConfTagDao> implements CommentConfTagService {

}
