/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.dao.impl.CommentTagDaoImpl
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
package com.mifan.support.dao.impl;

import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

import com.mifan.support.dao.CommentTagDao;
import com.mifan.support.domain.CommentTag;

/**
 * @author ZYW
 *
 */
@Repository
public class CommentTagDaoImpl extends AbstractBaseDao<CommentTag> implements CommentTagDao {

}
