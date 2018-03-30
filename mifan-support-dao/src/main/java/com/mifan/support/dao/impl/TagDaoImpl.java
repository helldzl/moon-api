/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.dao.impl.TagDaoImpl
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
package com.mifan.support.dao.impl;

import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

import com.mifan.support.dao.TagDao;
import com.mifan.support.domain.Tag;

/**
 * @author ZYW
 *
 */
@Repository
public class TagDaoImpl extends AbstractBaseDao<Tag> implements TagDao {


}
