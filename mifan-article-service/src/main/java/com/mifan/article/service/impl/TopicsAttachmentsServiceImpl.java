package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsAttachmentsDao;
import com.mifan.article.domain.TopicsAttachments;
import com.mifan.article.service.TopicsAttachmentsService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class TopicsAttachmentsServiceImpl extends AbstractBaseService<TopicsAttachments, TopicsAttachmentsDao> implements TopicsAttachmentsService {
}
