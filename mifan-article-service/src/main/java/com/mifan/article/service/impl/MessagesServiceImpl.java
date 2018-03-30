package com.mifan.article.service.impl;

import com.mifan.article.dao.MessagesDao;
import com.mifan.article.domain.Messages;
import com.mifan.article.service.MessagesService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
@Service
public class MessagesServiceImpl extends AbstractBaseService<Messages, MessagesDao> implements MessagesService {
}
