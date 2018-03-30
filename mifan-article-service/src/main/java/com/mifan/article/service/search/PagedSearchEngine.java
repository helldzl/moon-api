package com.mifan.article.service.search;

import com.mifan.article.service.search.builder.BuilderConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.elasticsearch.ClusteringSearchEngine;
import org.moonframework.elasticsearch.Searchable;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/7/7
 */
public class PagedSearchEngine extends ClusteringSearchEngine {

    private static Log logger = LogFactory.getLog(PagedSearchEngine.class);
    private static Date start;
    private static final String LAST_MODIFIED = "modified";
    private List<Field> fields = Fields.builder()
            .add(BaseEntity.ID)
            .add(BaseEntity.ENABLED)
            .build();

    static {
        Calendar instance = Calendar.getInstance();
        instance.set(2000, Calendar.JANUARY, 1);
        start = instance.getTime();
    }

    private SearchDocument searchDocument;

    public PagedSearchEngine(Client client, SearchDocument searchDocument) {
        super(client);
        this.searchDocument = searchDocument;
    }

    public <T extends BaseEntity & Searchable> void index(Class<T> classType) {
        List<BuilderConsumer<XContentBuilder, T>> builders = searchDocument.get(classType);
        builders.forEach(bc -> {
            bc.init();
            Date modified = find(bc.getIndex(), bc.getTid());
            Date now = new Date();

            Services.doList(classType, modified, now, fields, null, page -> doIndex(page, bc));
            update(bc.getIndex(), bc.getTid(), now);
        });
    }

    public <T extends BaseEntity & Searchable> void index(Class<T> classType, String... ids) {
        if (ids == null || ids.length == 0) {
            return;
        }

        List<BuilderConsumer<XContentBuilder, T>> builders = searchDocument.get(classType);
        builders.forEach(bc -> {
            bc.init();
            Page<T> page = Services.findAll(classType,
                    Restrictions.in(BaseEntity.ID, ids),
                    Pages.builder().page(1).size(ids.length).build(),
                    fields,
                    false);
            if (page.hasContent()) {
                doIndex(page, bc);
            }
        });
    }

    private <T extends BaseEntity & Searchable> void doIndex(Page<T> page, BuilderConsumer<XContentBuilder, T> bc) {
        Map<Boolean, List<T>> group = page.getContent().stream().collect(Collectors.partitioningBy(o -> o.getEnabled() == 1));
        List<T> enable = group.get(true);
        List<T> disable = group.get(false);
        List<T> result = new ArrayList<>();

        if (!CollectionUtils.isEmpty(enable)) {
            result.addAll(bc.list(enable));
        }

        if (!CollectionUtils.isEmpty(disable)) {
            result.addAll(disable);
        }

        doIndex(bc.getIndex(), bc.getType(), result, bc);
    }

    private <T extends BaseEntity & Searchable> void doIndex(String index, String type, List<T> list, BiConsumer<? super XContentBuilder, ? super T> consumer) {
        try {
            if (CollectionUtils.isEmpty(list)) {
                return;
            }

            BulkResponse bulkResponse = index(index, type, list, consumer);
            if (bulkResponse.hasFailures()) {
                // process failures by iterating through each bulk response item
                if (logger.isErrorEnabled()) {
                    logger.error(bulkResponse.buildFailureMessage());
                }
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("do index success");
                }
            }
        } catch (Exception e) {
            if (logger.isInfoEnabled()) {
                logger.error("do index error", e);
            }
        }
    }

    private Date find(String index, String id) {
        SearchResponse response = client.prepareSearch(index)
                .setTypes(LAST_MODIFIED)
                .setQuery(QueryBuilders.idsQuery().ids(id))
                .execute()
                .actionGet();
        SearchHits hits = response.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
        LastModified lastModified = null;
        if (iterator.hasNext()) {
            SearchHit hit = iterator.next();
            lastModified = BeanUtils.copyProperties(hit.getSource(), LastModified.class);
        }
        if (lastModified == null) {
            return start;
        }
        return lastModified.getDate();
    }

    private void update(String index, String id, Date date) {
        try {
            BulkResponse response = client.prepareBulk()
                    .add(client.prepareIndex(index, LAST_MODIFIED, id)
                            .setSource(jsonBuilder()
                                    .startObject()
                                    .field("date", date)
                                    .endObject()))
                    .execute().actionGet();
            response.hasFailures();
        } catch (IOException e) {
            logger.error("error", e);
        }
    }

    public static class LastModified {

        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

}
