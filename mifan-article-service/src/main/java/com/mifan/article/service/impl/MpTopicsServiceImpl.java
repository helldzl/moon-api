/**
 * 2018-03-22 17:49:31
 */
package com.mifan.article.service.impl;

import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

import com.mifan.article.dao.TopicsDao;
import com.mifan.article.domain.Topics;
import com.mifan.article.service.MpTopicsService;

/**
 * @author ZYW
 *
 */
@Service
public class MpTopicsServiceImpl extends AbstractBaseService<Topics, TopicsDao> implements MpTopicsService {
    
}
