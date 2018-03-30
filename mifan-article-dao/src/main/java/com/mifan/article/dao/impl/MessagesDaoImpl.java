package com.mifan.article.dao.impl;

import com.mifan.article.dao.MessagesDao;
import com.mifan.article.domain.Messages;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
@Repository
public class MessagesDaoImpl extends AbstractBaseDao<Messages> implements MessagesDao {
}
