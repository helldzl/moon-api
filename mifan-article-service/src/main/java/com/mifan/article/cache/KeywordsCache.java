package com.mifan.article.cache;

import com.mifan.article.domain.support.Word;
import com.mifan.article.service.impl.AbstractRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/22
 */
@Component
public class KeywordsCache extends AbstractRedisService {

    private static final String REDIS_KEYWORD_HOT = "topic:keyword:hot:";

    private static final int KEYWORD_SCORE = 86400 / 50;

    @Autowired
    public KeywordsCache(RedisTemplate<String, String> template) {
        super(template);
    }

    public List<Word> keywords(int size) {
        double min = currentTimeSeconds() - ONE_WEEK_IN_SECONDS;
        Set<ZSetOperations.TypedTuple<String>> set = template.opsForZSet().reverseRangeByScoreWithScores(REDIS_KEYWORD_HOT, min, Double.MAX_VALUE, 0, size);
        if (set.isEmpty()) {
            return Collections.emptyList();
        }

        List<Word> list = new ArrayList<>();
        DoubleSummaryStatistics statistics = set.stream().collect(Collectors.summarizingDouble(ZSetOperations.TypedTuple::getScore));
        double max = statistics.getMax();
        int index = 1;
        for (ZSetOperations.TypedTuple<String> tuple : set) {
            int value = (int) ((tuple.getScore() - min) / KEYWORD_SCORE);
            list.add(new Word(index++, tuple.getValue(), value, normalization(max, min, tuple.getScore())));
        }
        return list;
        // return super.reverseRangeByScore(REDIS_KEYWORD_HOT, size, ONE_WEEK_IN_SECONDS);
    }

    public void increment(String value) {
        super.increment(REDIS_KEYWORD_HOT, value, KEYWORD_SCORE, ONE_WEEK_IN_SECONDS);
    }

    private static double normalization(double max, double min, double x) {
        return (x - min) / (max - min);
    }

    private static double normalization(double x, double n) {
        return 1D - (1D / (x + 0.01D));
    }

}
