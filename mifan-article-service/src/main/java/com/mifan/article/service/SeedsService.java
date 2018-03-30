package com.mifan.article.service;

import com.mifan.article.domain.Seeds;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public interface SeedsService extends BaseService<Seeds> {
    Page<Seeds> getQuestionTemplate(int page, int size);

    Page<Seeds> getNotWholeTemplate(int page, int size);
}
