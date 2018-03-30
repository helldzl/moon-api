package com.mifan.article.dao.impl;

import com.mifan.article.dao.ForumsDao;
import com.mifan.article.domain.Forums;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Repository
public class ForumsDaoImpl extends AbstractBaseDao<Forums> implements ForumsDao {
}
