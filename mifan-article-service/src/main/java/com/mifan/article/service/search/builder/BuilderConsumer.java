package com.mifan.article.service.search.builder;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.moonframework.elasticsearch.Searchable;
import org.moonframework.model.mybatis.domain.BaseEntity;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/11/23
 */
public interface BuilderConsumer<T extends XContentBuilder, U extends BaseEntity & Searchable> extends BiConsumer<T, U> {

    void init();

    List<U> list(List<U> list);

    String getIndex();

    String getType();

    String getTitleFieldName();

    String getContentFieldName();

    String[] getIncludeListFields();

    String[] getMLTFields();

    String getTid();

}
