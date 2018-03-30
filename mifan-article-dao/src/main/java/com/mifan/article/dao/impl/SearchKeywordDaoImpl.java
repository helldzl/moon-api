package com.mifan.article.dao.impl;

import com.mifan.article.dao.SearchKeywordDao;
import com.mifan.article.domain.SearchKeyword;

import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-01-18
 */
@Repository
public class SearchKeywordDaoImpl extends AbstractBaseDao<SearchKeyword> implements SearchKeywordDao {
}
