package com.mifan.article.cache;

import com.mifan.article.domain.Topics;
import com.mifan.article.service.impl.AbstractRedisService;
import org.moonframework.model.mybatis.domain.Pair;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/8
 */
@Component
public class TopicsCache extends AbstractRedisService {

    public static final String MEMBER_REVIEWS = "reviews";
    public static final String MEMBER_REPLIES = "replies";
    public static final String MEMBER_THUMBS_UP = "thumbsUp";
    public static final String MEMBER_THUMBS_DOWN = "thumbsDown";
    public static final String MEMBER_THUMBS = "thumbs";

    //
    private HashOperations<String, Object, Object> opsForHash;
    private SetOperations<String, String> opsForSet;
    private ZSetOperations<String, String> opsForZSet;

    /**
     * <p>Hash</p>
     * <p>Key : topic:{topicId}</p>
     * <p>Value : key-value对, 缓存主题的统计数</p>
     * <ul>
     * <li>reviews:浏览数</li>
     * <li>replies:回复数</li>
     * <li>thumbs:点赞数/投票数</li>
     * </ul>
     */
    private static final String REDIS_TOPIC = "topic:%s";

    /**
     * <p>Set</p>
     * <p>Key : topic:liked:{topicId}</p>
     * <p>Value : [true|false]:{userId}</p>
     * <p>每个主题已经参与过投票的用户集合</p>
     */
    private static final String REDIS_TOPIC_LIKED = "topic:liked:%s";

    /**
     * <p>Set</p>
     * <p>Key : topic:favorite:{topicId}</p>
     * <p>Value : {userId}</p>
     * <p>每个主题被哪些用户收藏的集合</p>
     */
    private static final String REDIS_TOPIC_FAVORITE = "topic:favorite:%s";

    /**
     * <p>Sorted Set</p>
     * <p>Value : {topicId}</p>
     * <p>根据投票计算的热门文章</p>
     */
    private static final String REDIS_TOPIC_SCORE = "topic:score:";

    private static final int VOTE_SCORE = 432;

    @Autowired
    public TopicsCache(RedisTemplate<String, String> template) {
        super(template);
        this.opsForHash = template.opsForHash();
        this.opsForSet = template.opsForSet();
        this.opsForZSet = template.opsForZSet();
    }

    @Override
    public Page<String> page(String key, int page, int size) {
        return super.page(REDIS_TOPIC_SCORE, page, size);
    }

    public void put(Long topicId, Statistics statistics, Integer value) {
        opsForHash.put(String.format(REDIS_TOPIC, topicId), statistics.getName(), value.toString());
    }

    public Long increment(Long topicId, Statistics statistics) {
        return increment(topicId, statistics, 1L);
    }

    public Long increment(Long topicId, Statistics statistics, long delta) {
        Long n = opsForHash.increment(String.format(REDIS_TOPIC, topicId), statistics.getName(), delta);
        if (statistics.getScore() != 0) {
            increment(REDIS_TOPIC_SCORE, topicId.toString(), delta * statistics.getScore(), ONE_WEEK_IN_SECONDS);
        }
        return n;
    }

    public void addFavorite(Long userId, Long topicId) {
        String key = String.format(REDIS_TOPIC_FAVORITE, topicId);
        opsForSet.add(key, userId.toString());
    }

    public void removeFavorite(Long userId, Long topicId) {
        String key = String.format(REDIS_TOPIC_FAVORITE, topicId);
        opsForSet.remove(key, userId.toString());
    }

    public void addLike(Long userId, Long topicId, boolean up) {
        String liked = String.format(REDIS_TOPIC_LIKED, topicId);
        if (opsForSet.add(liked, String.format("%s:%s", up, userId)) != 0L) {
            opsForSet.remove(liked, String.format("%s:%s", !up, userId));
        }
    }

    public Long removeLiked(Long userId, Long topicId) {
        return opsForSet.remove(String.format(REDIS_TOPIC_LIKED, topicId),
                String.format("%s:%s", true, userId),
                String.format("%s:%s", false, userId));
    }

    /**
     * <p>用户对某一篇文章是否喜欢</p>
     *
     * @param userId  user ID
     * @param topicId topic ID
     * @return liked
     */
    public int isLiked(Long userId, Long topicId) {
        if (userId == null) {
            return 0;
        }
        String key = String.format(REDIS_TOPIC_LIKED, topicId);
        if (opsForSet.isMember(key, String.format("%s:%s", true, userId))) {
            return 1;
        } else if (opsForSet.isMember(key, String.format("%s:%s", false, userId))) {
            return -1;
        } else {
            return 0;
        }
    }

    public int isFavorite(Long userId, Long topicId) {
        if (userId == null) {
            return 0;
        }
        String key = String.format(REDIS_TOPIC_FAVORITE, topicId);
        return opsForSet.isMember(key, userId.toString()) ? 1 : 0;
    }

    /**
     * <p>此方法要在事务中执行才能保证数据一致性, 通过update获得行锁</p>
     *
     * @param id topic ID
     */
    public void reviews(Long id) {
        if (update(id, Topics.REVIEWS) > 0) {
            // 评分从第二次后开始累计
            if (!cache(id)) {
                increment(id, Statistics.REVIEWS);
            }
        }
    }

    /**
     * @param topicId topic ID
     * @param userId  user ID
     * @return map
     */
    public Map<String, Object> attributes(Long topicId, Long userId) {
        Map<String, Object> map = attributes(topicId);
        map.put("liked", isLiked(userId, topicId));                 // 是否投过票
        map.put("favorite", isFavorite(userId, topicId));           // 是否收藏过
        return map;
    }

    /**
     * <p>获得主题的相关统计信息</p>
     *
     * @param topicId topic ID
     * @return map
     */
    public Map<String, Object> attributes(Long topicId) {
        Map<Object, Object> map = opsForHash.entries(String.format(REDIS_TOPIC, topicId));
        int reviews = 0;
        int replies = 0;
        int thumbsUp = 0;
        int thumbsDown = 0;
        if (!CollectionUtils.isEmpty(map)) {
            reviews = get(map, MEMBER_REVIEWS);
            replies = get(map, MEMBER_REPLIES);
            thumbsUp = get(map, MEMBER_THUMBS_UP);
            thumbsDown = get(map, MEMBER_THUMBS_DOWN);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(MEMBER_REVIEWS, reviews);
        result.put(MEMBER_REPLIES, replies);
        result.put(MEMBER_THUMBS_UP, thumbsUp);
        result.put(MEMBER_THUMBS_DOWN, thumbsDown);
        result.put(MEMBER_THUMBS, thumbsUp - thumbsDown);
        return result;
    }


    /**
     * @param userId  user ID
     * @param topicId topid ID
     * @param up      up
     * @return boolean
     */
//    public boolean toggle(Long userId, Long topicId, boolean up) {
//        // TODO 清楚缓存后导致的数据不一致问题
//        String liked = String.format(REDIS_TOPIC_LIKED, topicId);
//        String topic = String.format(REDIS_TOPIC, topicId);
//
//        int likes = 0;
//        int score = 0;
//
//        if (opsForSet.add(liked, String.format("%s:%s", up, userId)) != 0L) {
//            Pair.PairBuilder builder = Pair.builder();
//
//            // 如果条件为TRUE, 说明是第一次添加(ON|OFF)投票
//            likes += up ? 1 : -1;
//            score += up ? VOTE_SCORE : -VOTE_SCORE;
//            String field = up ? MEMBER_THUMBS_UP : MEMBER_THUMBS_DOWN;
//            opsForHash.increment(topic, field, 1);
//
//            builder.add(up ? Topics.THUMBS_UP : Topics.THUMBS_DOWN, 1);
//
//            // 只在第一次添加(ON|OFF)投票时, 尝试删除之前参与的相反的(!ON|!OFF)投票
//            if (opsForSet.remove(liked, String.format("%s:%s", !up, userId)) != 0L) {
//                // 如果条件为TRUE, 说明之前投过相反的票
//                likes += up ? 1 : -1;
//                score += up ? VOTE_SCORE : -VOTE_SCORE;
//                field = !up ? MEMBER_THUMBS_UP : MEMBER_THUMBS_DOWN;
//                opsForHash.increment(topic, field, -1);
//
//                builder.add(!up ? Topics.THUMBS_UP : Topics.THUMBS_DOWN, -1);
//            }
//
//            // 更新数据库
//            update(topicId, builder.build());
//
//            // 更新缓存
//            opsForHash.increment(topic, MEMBER_THUMBS, likes);
//            opsForZSet.incrementScore(REDIS_TOPIC_SCORE, topicId.toString(), score);
//            return true;
//        }
//        return false;
//    }

    /**
     * @param topicId topic ID
     * @return boolean
     */
    private boolean cache(Long topicId) {
        // 如果是第一次添加缓存, 初始化各个统计相关数据
        String key = String.format(REDIS_TOPIC, topicId);
        if (opsForHash.putIfAbsent(key, "init", "")) {
            // 只有获取的当前行锁的事务才能执行查询, 所以这里是线程安全的
            Topics topic = Services.findOne(Topics.class, topicId, Topics.STATISTICS_FIELDS);

            Map<String, Object> map = new HashMap<>(16);
            map.put(MEMBER_REPLIES, String.valueOf(topic.getReplies()));
            map.put(MEMBER_REVIEWS, String.valueOf(topic.getReviews()));
            map.put(MEMBER_THUMBS_UP, String.valueOf(topic.getThumbsUp()));
            map.put(MEMBER_THUMBS_DOWN, String.valueOf(topic.getThumbsDown()));
            map.put(MEMBER_THUMBS, String.valueOf(topic.getThumbsUp() - topic.getThumbsDown()));
            opsForHash.putAll(key, map);
            return true;
        }
        return false;
    }

    /**
     * @param map map
     * @param key key
     * @return int
     */
    private Integer get(Map<Object, Object> map, String key) {
        String value = (String) map.get(key);
        if (value != null) {
            return Integer.valueOf(value);
        }
        return 0;
    }

    private int update(Long id, String field) {
        return update(id, Pair.builder().add(field, 1).build());
    }

    private int update(Long id, List<Pair> pairs) {
        return Services.update(Topics.class, id, pairs, Collections.emptyList());
    }

    public enum Statistics {

        REVIEWS(MEMBER_REVIEWS, VOTE_SCORE),
        REPLIES(MEMBER_REPLIES, VOTE_SCORE),
        THUMBS_UP(MEMBER_THUMBS_UP, VOTE_SCORE),
        THUMBS_DOWN(MEMBER_THUMBS_DOWN, -VOTE_SCORE);

        private final String name;
        private final int score;

        Statistics(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }

}
