package com.mifan.article.service.impl;

import com.mifan.article.dao.SearchKeywordDao;
import com.mifan.article.domain.SearchKeyword;
import com.mifan.article.service.SearchKeywordService;

import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-01-18
 */
@Service
public class SearchKeywordServiceImpl extends AbstractBaseService<SearchKeyword, SearchKeywordDao> implements SearchKeywordService {
}
