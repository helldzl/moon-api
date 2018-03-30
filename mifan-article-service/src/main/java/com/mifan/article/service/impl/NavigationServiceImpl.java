package com.mifan.article.service.impl;

import com.mifan.article.dao.NavigationDao;
import com.mifan.article.domain.ElasticQueryBuilder;
import com.mifan.article.domain.Navigation;
import com.mifan.article.service.NavigationService;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-23
 */
@Service
public class NavigationServiceImpl extends AbstractBaseService<Navigation, NavigationDao> implements NavigationService {

    @Override
    public Navigation findOne(Long id) {
        Navigation navigation;
        if (id <= 0L) {
            navigation = new Navigation(0L);
            navigation.setElasticQueryBuilderId(0L);
        } else {
            navigation = super.findOne(id, Navigation.DEFAULT_FIELDS);
        }

        if (navigation != null) {
            ElasticQueryBuilder queryBuilder = Services.findOne(ElasticQueryBuilder.class, navigation.getElasticQueryBuilderId());
            navigation.setElasticQueryBuilder(queryBuilder);
        }
        return navigation;
    }

    @Override
    public Page<Navigation> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        List<Navigation> navigate = Navigation.navigate(super.findAll(criterion,
                Pages.builder()
                        .sort(Pages
                                .sortBuilder()
                                .add(Navigation.PARENT_ID, true)
                                .add(Navigation.DISPLAY_ORDER, true)
                                .build())
                        .page(1)
                        .size(200)
                        .build(),
                Navigation.DEFAULT_FIELDS)
                .getContent());
        return new PageImpl<>(navigate, null, navigate.size());
    }

    @Override
    public Navigation queryForObject(Long id, Iterable<? extends Field> fields) {
        return super.queryForObject(id, Navigation.DEFAULT_FIELDS);
    }

}
