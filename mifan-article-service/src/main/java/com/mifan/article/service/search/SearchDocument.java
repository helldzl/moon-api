package com.mifan.article.service.search;

import com.mifan.article.service.search.builder.BuilderConsumer;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.moonframework.elasticsearch.Searchable;
import org.moonframework.model.mybatis.domain.BaseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author quzile
 * @version 1.0
 * @since 2016/11/23
 */
public class SearchDocument {

    private Map<Class<?>, IndexBuilder<?>> map = new HashMap<>();

    public <T extends BaseEntity & Searchable> BuilderConsumer<XContentBuilder, T> get(Class<T> classType, IndexType type) {
        return map.get(classType).builder(type);
    }

    public <T extends BaseEntity & Searchable> List<BuilderConsumer<XContentBuilder, T>> get(Class<T> classType) {
        return map.get(classType).builders();
    }

    public <T extends BaseEntity & Searchable> void put(Class<T> classType, IndexBuilder<T> search) {
        map.put(classType, search);
    }

    public static class IndexBuilder<T extends BaseEntity & Searchable> extends HashMap<IndexType, BuilderConsumer<XContentBuilder, T>> {

        @SuppressWarnings("unchecked")
        public <E extends BaseEntity & Searchable> BuilderConsumer<XContentBuilder, E> builder(IndexType type) {
            return (BuilderConsumer<XContentBuilder, E>) get(type);
        }

        @SuppressWarnings("unchecked")
        public <E extends BaseEntity & Searchable> List<BuilderConsumer<XContentBuilder, E>> builders() {
            return entrySet().stream().map(entry -> (BuilderConsumer<XContentBuilder, E>) entry.getValue()).collect(Collectors.toList());
        }

    }

    public enum IndexType {
        LIST,
        DETAILS
    }

}
