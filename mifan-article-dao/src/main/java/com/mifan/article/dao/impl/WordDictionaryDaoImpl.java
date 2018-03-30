package com.mifan.article.dao.impl;

import com.mifan.article.dao.WordDictionaryDao;
import com.mifan.article.domain.WordDictionary;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-10-31
 */
@Repository
public class WordDictionaryDaoImpl extends AbstractBaseDao<WordDictionary> implements WordDictionaryDao {
}
