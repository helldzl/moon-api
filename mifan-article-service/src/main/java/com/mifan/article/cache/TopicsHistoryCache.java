package com.mifan.article.cache;

import com.mifan.article.service.impl.AbstractRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/18
 */
@Component
public class TopicsHistoryCache extends AbstractRedisService {

    /**
     * topic:history:{token}
     */
    private static final String REDIS_TOPICS_HISTORY = "topic:history:%s";
    private static final String REDIS_LOGIN = "login:";
    private static final String REDIS_RECENT = "recent:";

    @Value("${application.scheduler.enable.history.cache:false}")
    private boolean enableHistoryCache;

    @Autowired
    public TopicsHistoryCache(RedisTemplate<String, String> template) {
        super(template);
    }

    /**
     * @param token    user ID
     * @param username username
     * @param forumId  forumId
     * @param id       topic ID
     */
    public void viewed(String token, String username, Long forumId, Long id) {
        if (token == null) {
            return;
        }
        HashOperations<String, Object, Object> opsForHash = template.opsForHash();
        ZSetOperations<String, String> opsForZSet = template.opsForZSet();

        long time = System.currentTimeMillis() / 1000L;
        opsForHash.put(REDIS_LOGIN, token, username == null ? "" : username);
        opsForZSet.add(REDIS_RECENT, token, time);
        if (id != null) {
            // 添加浏览记录, 保留最近的30个
            String viewed = String.format(REDIS_TOPICS_HISTORY, token);
            opsForZSet.add(viewed, id.toString(), time);
            opsForZSet.removeRange(viewed, 0, -31);
            //
            viewed = String.format(REDIS_TOPICS_HISTORY, forumId + ":" + token);
            opsForZSet.add(viewed, id.toString(), time);
            opsForZSet.removeRange(viewed, 0, -11);
        }
    }

    public Page<String> page(String key, Long forumId, int page, int size) {
        if (forumId == null) {
            return super.page(String.format(REDIS_TOPICS_HISTORY, key), page, size);
        } else {
            return super.page(String.format(REDIS_TOPICS_HISTORY, forumId + ":" + key), page, size);
        }
    }

    /**
     * <p>定时清除redis中的历史数据, 或者保存到数据仓库中用来进行数据分析</p>
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void clean() {
        if (!enableHistoryCache) {
            return;
        }
        logger.info("clean cache");
        HashOperations<String, Object, Object> opsForHash = template.opsForHash();
        ZSetOperations<String, String> opsForZSet = template.opsForZSet();
        long limit = 2000, size;
        while ((size = opsForZSet.zCard(REDIS_RECENT)) > limit) {
            Set<String> tokens = opsForZSet.range(REDIS_RECENT, 0, Math.min(size - limit, 100) - 1);
            Object[] array = tokens.toArray();

            opsForHash.delete(REDIS_LOGIN, array);
            opsForZSet.remove(REDIS_RECENT, array);

            // template.delete(tokens.stream().map(token -> String.format(REDIS_TOPICS_HISTORY, token)).collect(Collectors.toSet()));
            for (String token : tokens) {
                Set<String> set = new HashSet<>();
                set.add(String.format(REDIS_TOPICS_HISTORY, token));
                for (int i = 1; i <= 5; i++) {
                    set.add(String.format(REDIS_TOPICS_HISTORY, i + ":" + token));
                }
                template.delete(set);
            }
        }
    }

}
