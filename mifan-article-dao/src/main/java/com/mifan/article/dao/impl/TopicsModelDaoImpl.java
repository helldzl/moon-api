package com.mifan.article.dao.impl;

import com.mifan.article.dao.TopicsModelDao;
import com.mifan.article.domain.TopicsModel;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-11-08
 */
@Repository
public class TopicsModelDaoImpl extends AbstractBaseDao<TopicsModel> implements TopicsModelDao {
}
