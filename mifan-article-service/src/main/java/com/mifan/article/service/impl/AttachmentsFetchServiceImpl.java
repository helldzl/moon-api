package com.mifan.article.service.impl;

import com.mifan.article.dao.AttachmentsFetchDao;
import com.mifan.article.domain.AttachmentsFetch;
import com.mifan.article.service.AttachmentsFetchService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class AttachmentsFetchServiceImpl extends AbstractBaseService<AttachmentsFetch, AttachmentsFetchDao> implements AttachmentsFetchService {
}
