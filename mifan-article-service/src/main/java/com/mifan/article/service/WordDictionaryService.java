package com.mifan.article.service;

import com.mifan.article.domain.WordDictionary;
import org.moonframework.model.mybatis.service.BaseService;

import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-10-31
 */
public interface WordDictionaryService extends BaseService<WordDictionary> {

    Map<String, String> dictionary();

}
