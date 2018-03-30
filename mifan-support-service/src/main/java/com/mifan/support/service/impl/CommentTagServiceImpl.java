/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.service.impl.CommentTagServiceImpl
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

import com.mifan.support.dao.CommentTagDao;
import com.mifan.support.domain.CommentTag;
import com.mifan.support.service.CommentTagService;

/**
 * @author ZYW
 *
 */
@Service
public class CommentTagServiceImpl extends AbstractBaseService<CommentTag, CommentTagDao> implements CommentTagService {

}
