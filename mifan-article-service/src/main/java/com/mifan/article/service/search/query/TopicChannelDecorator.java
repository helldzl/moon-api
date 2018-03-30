package com.mifan.article.service.search.query;

import com.mifan.article.cache.TopicsCache;
import com.mifan.article.domain.Channels;
import com.mifan.article.domain.ElasticQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.moonframework.core.support.OperateComponent;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/27
 */
public class TopicChannelDecorator extends TopicOperateDecorator {

    private Long channelId;
    private TopicsCache topicsCache;
    private int page;
    private int size;
    private QueryBuilder queryBuilder;
    private Result result;
    private boolean decorated;

    public TopicChannelDecorator(OperateComponent<QueryBuilder> operate, TopicsCache topicsCache, Long channelId, int page, int size) {
        super(operate);
        this.topicsCache = topicsCache;
        this.channelId = channelId;
        this.page = page;
        this.size = size;
    }

    @Override
    public QueryBuilder operation() {
        if (queryBuilder == null) {
            queryBuilder = queryBuilder();
        }
        return queryBuilder;
    }

    private QueryBuilder queryBuilder() {
        BoolQueryBuilder boolQueryBuilder = boolQueryBuilder();

        // [channel]
        Channels channel = Services.findOne(Channels.class, channelId);
        Channels.ChannelType channelType = Channels.ChannelType.from(channel.getChannelType());
        Long targetId = channel.getTargetId();

        // [switch]
        switch (channelType) {
            case FORUM:
                return boolQueryBuilder.must(QueryBuilders.termQuery("forumId", targetId));
            case LABEL:
                ElasticQueryBuilder elasticQueryBuilder = Services.findOne(ElasticQueryBuilder.class, targetId);
                if (elasticQueryBuilder == null) {
                    return boolQueryBuilder;
                }

                TopicBoostingDecorator decorator = new TopicBoostingDecorator(
                        new TopicFunctionScoreDecorator(
                                getOperate(),
                                elasticQueryBuilder),
                        elasticQueryBuilder);

                decorated = true;
                return decorator.operation();
            case USER:
                return boolQueryBuilder.must(QueryBuilders.termQuery("creator", targetId));
            case FROM:
                List<Long> targets = channel.getTargets();
                if (CollectionUtils.isEmpty(targets)) {
                    return boolQueryBuilder;
                }

                return boolQueryBuilder.must(QueryBuilders.termsQuery("items.seedId", targets));
            case RANK:
                Page<String> result = topicsCache.page(null, page, size);
                this.result = new Result(result.getContent(), result.getTotalElements());
                return boolQueryBuilder.must(QueryBuilders.idsQuery().ids(result.getContent()));
            default:
                return boolQueryBuilder;
        }
    }

    public Result getResult() {
        return result;
    }

    public boolean isDecorated() {
        return decorated;
    }

    public static class Result {

        private List<String> ids;
        private long total;

        private Result(List<String> ids, long total) {
            this.ids = ids;
            this.total = total;
        }

        public List<String> getIds() {
            return ids;
        }

        public long getTotal() {
            return total;
        }
    }

}
