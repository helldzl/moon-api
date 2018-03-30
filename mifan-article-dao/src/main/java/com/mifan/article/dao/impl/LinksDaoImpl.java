package com.mifan.article.dao.impl;

import com.mifan.article.dao.LinksDao;
import com.mifan.article.domain.Links;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Repository
public class LinksDaoImpl extends AbstractBaseDao<Links> implements LinksDao {
}
