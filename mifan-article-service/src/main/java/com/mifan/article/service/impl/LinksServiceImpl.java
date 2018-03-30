package com.mifan.article.service.impl;

import com.mifan.article.dao.LinksDao;
import com.mifan.article.domain.Links;
import com.mifan.article.service.LinksService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class LinksServiceImpl extends AbstractBaseService<Links, LinksDao> implements LinksService {
    @Override
    public <S extends Links> int save(S entity) {
        if (entity.getParentId() == null) {
            entity.setParentId(0L);
        }
        return super.save(entity);
    }

    /*@Override
    public Page<Links> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields){
        Page<Links> pages = super.findAll(criterion, pageable, fields);
        return pages;
    }*/
}
