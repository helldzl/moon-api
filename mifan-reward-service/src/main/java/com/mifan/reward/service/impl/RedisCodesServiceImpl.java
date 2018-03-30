package com.mifan.reward.service.impl;

import com.mifan.reward.domain.Prizes;
import com.mifan.reward.service.PrizesService;
import com.mifan.reward.service.RedisCodesService;
import com.mifan.reward.service.contants.RewardContants;
import org.moonframework.model.mybatis.domain.Fields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("redisCodesService")
public class RedisCodesServiceImpl implements RedisCodesService {

    @Autowired
    private PrizesService prizesService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean reload(Long prizeId, List<String> existingCodeList) {

        if (CollectionUtils.isEmpty(existingCodeList)) {
            return this.add(prizeId);
        }

        Prizes findPrize = prizesService.queryForObject(prizeId, null);
        if (findPrize == null) {
            return false;
        }

        int codeTotal = findPrize.getGoodInfo().getBuyTimes();
        List<String> codeList = new ArrayList<>();
        for (int i = 0; i < codeTotal; i++) {
            codeList.add(String.valueOf(RewardContants.CODE_NUM_PREFIX + i));
        }
        codeList.removeAll(existingCodeList);
        Collections.shuffle(codeList);

        redisTemplate.opsForList().leftPushAll(RewardContants.CODE_REDIS_PREFIX + prizeId, codeList);
        return true;
    }

    @Override
    public boolean add(Long prizeId) {

        Prizes findPrize = prizesService.queryForObject(prizeId, Fields.builder().build());
        if (findPrize == null) {
            return false;
        }

        int codeTotal = findPrize.getGoodInfo().getBuyTimes();
        List<String> codeList = new ArrayList<>();
        for (int i = 0; i < codeTotal; i++) {
            codeList.add(String.valueOf(RewardContants.CODE_NUM_PREFIX + i));
        }


        redisTemplate.opsForList().leftPushAll(RewardContants.CODE_REDIS_PREFIX + String.valueOf(prizeId), codeList);
        return true;
    }

    @Override
    public boolean remove(Long prizeId) {
        if (StringUtils.isEmpty(prizeId)) {
            return false;
        }
        redisTemplate.delete(RewardContants.CODE_REDIS_PREFIX + String.valueOf(prizeId));
        return true;
    }

    @Override
    public Long getOne(Long prizeId) {
        return Long.getLong(redisTemplate.opsForList().leftPop(RewardContants.CODE_REDIS_PREFIX + String.valueOf(prizeId)));
    }

    @Override
    public int length(Long prizeId) {
        return redisTemplate.opsForList().size(RewardContants.CODE_REDIS_PREFIX + String.valueOf(prizeId)).intValue();
    }
}

