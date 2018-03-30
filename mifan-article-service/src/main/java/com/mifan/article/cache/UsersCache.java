package com.mifan.article.cache;

import com.mifan.article.service.impl.AbstractRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/9
 */
@Component
public class UsersCache extends AbstractRedisService {

    private static final String REDIS_USER_HIDE = "user:hide:%s";
    private static final String REDIS_USER_COMPARE = "user:compare:%s";

    private SetOperations<String, String> opsForSet;

    @Autowired
    public UsersCache(RedisTemplate<String, String> template) {
        super(template);
        this.opsForSet = template.opsForSet();
    }

    public Long add(Type type, Long userId, Long topicId) {
        return opsForSet.add(type.key(userId), topicId.toString());
    }

    public Long remove(Type type, Long userId, Long topicId) {
        return opsForSet.remove(type.key(userId), topicId.toString());
    }

    public Long remove(Type type, Long userId, Iterable<Long> ids) {
        long n = 0;
        for (Long id : ids) {
            n += opsForSet.remove(type.key(userId), id.toString());
        }
        return n;
    }

    public Boolean isMember(Type type, Long userId, Long topicId) {
        return opsForSet.isMember(type.key(userId), topicId.toString());
    }

    public Set<String> members(Type type, Long userId) {
        return opsForSet.members(type.key(userId));
    }

    public enum Type {

        /**
         * 用户比较的
         */
        LIKE("compared") {
            @Override
            public String key(Long userId) {
                return String.format(REDIS_USER_COMPARE, userId);
            }
        },

        /**
         * 用户隐藏的
         */
        HIDE("hided") {
            @Override
            public String key(Long userId) {
                return String.format(REDIS_USER_HIDE, userId);
            }
        };

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public abstract String key(Long userId);

        public String getName() {
            return name;
        }
    }

}
