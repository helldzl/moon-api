package com.mifan.article.dao;

import com.mifan.article.domain.Seeds;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.repository.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public interface SeedsDao extends BaseDao<Seeds> {
    Page<Seeds> getQuestionTemplate(Pageable pageable, Iterable<? extends Field> fields);

    Page<Seeds> getNotWholeTemplate(Pageable pageable, Iterable<? extends Field> fields);
}
