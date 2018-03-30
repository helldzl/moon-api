package com.mifan.article.dao.impl;

import com.mifan.article.dao.SeedsDao;
import com.mifan.article.domain.Seeds;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Repository
public class SeedsDaoImpl extends AbstractBaseDao<Seeds> implements SeedsDao {
    //取得在种子规定时间内没有抓取任何数据的种子，查看是否模板已经改变
    @Override
    public Page<Seeds> getQuestionTemplate(Pageable pageable, Iterable<? extends Field> fields) {
        String getNotSpiderTemplate = entityClass.getName() + "." + "getNotSpiderTemplate";
        Map<String, Object> map = pageMap(pageable);
        map.put("fields", fields);
        List<Seeds> seedses = session.selectList(getNotSpiderTemplate, map);
        return new PageImpl<>(seedses, pageable, getNotSpiderTemplateCount());
    }

    @Override
    public Page<Seeds> getNotWholeTemplate(Pageable pageable, Iterable<? extends Field> fields) {
        String getNotWholeTemplate = entityClass.getName() + "." + "getNotWholeTemplate";
        Map<String, Object> map = pageMap(pageable);
        map.put("fields", fields);
        List<Seeds> seedses = session.selectList(getNotWholeTemplate, map);
        return new PageImpl<>(seedses, pageable, getNotWholeTemplateCount());
    }

    private Long getNotSpiderTemplateCount() {
        String getNotSpiderTemplateCount = entityClass.getName() + "." + "getNotSpiderTemplateCount";
        Long count = session.selectOne(getNotSpiderTemplateCount);
        return count;
    }

    private Long getNotWholeTemplateCount() {
        String getNotWholeTemplateCount = entityClass.getName() + "." + "getNotWholeTemplateCount";
        Long count = session.selectOne(getNotWholeTemplateCount);
        return count;
    }
}
