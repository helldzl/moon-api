package com.mifan.article.service.impl;

import com.mifan.article.dao.ForumsDao;
import com.mifan.article.domain.Forums;
import com.mifan.article.service.ForumsService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class ForumsServiceImpl extends AbstractBaseService<Forums, ForumsDao> implements ForumsService {
}
