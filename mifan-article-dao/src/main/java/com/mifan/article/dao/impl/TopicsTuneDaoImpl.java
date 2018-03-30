package com.mifan.article.dao.impl;

import com.mifan.article.dao.TopicsTuneDao;
import com.mifan.article.domain.TopicsTune;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-01-05
 */
@Repository
public class TopicsTuneDaoImpl extends AbstractBaseDao<TopicsTune> implements TopicsTuneDao {
}
