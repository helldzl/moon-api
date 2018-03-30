package com.mifan.article.service.impl;

import com.mifan.article.dao.VotesOptionDao;
import com.mifan.article.domain.VotesOption;
import com.mifan.article.service.VotesOptionService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-05
 */
@Service
public class VotesOptionServiceImpl extends AbstractBaseService<VotesOption, VotesOptionDao> implements VotesOptionService {
}
