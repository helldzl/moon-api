package com.mifan.award.service.impl;

import com.mifan.award.domain.Prize;
import com.mifan.award.service.CodeService;
import com.mifan.award.service.PrizeService;
import com.mifan.award.service.contants.AwardConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("codeService")
public class CodeServiceImpl implements CodeService {

    @Autowired
    private PrizeService prizeService;

    private StringRedisTemplate template;

    private ListOperations<String, String> listOps;

    private ValueOperations<String, String> ops;

    @Autowired
    public void setRedisTemplate(StringRedisTemplate template) {
        this.template = template;
        this.listOps = template.opsForList();
        this.ops = template.opsForValue();
    }

    @Override
    public boolean reload(String prizeId, List<String> existingCodeList) {

        if (CollectionUtils.isEmpty(existingCodeList)) {
            return this.add(prizeId);
        }

        if (StringUtils.isEmpty(prizeId)) {
            return false;
        }

        Prize findPrize = prizeService.findOne(prizeId);
        if (findPrize == null) {
            return false;
        }

        int codeTotal = findPrize.getBuyTimes();
        List<String> codeList = new ArrayList<>();
        for (int i = 0; i < codeTotal; i++) {
            codeList.add(Long.toString(AwardConstants.CODE_NUM_PREFIX + i));
        }
        codeList.removeAll(existingCodeList);
        Collections.shuffle(codeList);

        listOps.leftPushAll(AwardConstants.CODE_REDIS_PREFIX + prizeId, codeList);
        return true;
    }

    @Override
    public boolean add(String prizeId) {
        if (StringUtils.isEmpty(prizeId)) {
            return false;
        }
        Prize findPrize = prizeService.findOne(prizeId);
        if (findPrize == null) {
            return false;
        }
        int codeTotal = findPrize.getBuyTimes();
        List<String> codeList = new ArrayList<>();
        for (int i = 0; i < codeTotal; i++) {
            codeList.add(Long.toString(AwardConstants.CODE_NUM_PREFIX + i));
        }
        Collections.shuffle(codeList);
        listOps.leftPushAll(AwardConstants.CODE_REDIS_PREFIX + prizeId, codeList);
        return true;
    }

    @Override
    public boolean remove(String prizeId) {
        if (StringUtils.isEmpty(prizeId)) {
            return false;
        }
        template.delete(AwardConstants.CODE_REDIS_PREFIX + prizeId);
        return true;
    }

    @Override
    public String getOne(String prizeId) {
        if (StringUtils.isEmpty(prizeId)) {
            return null;
        }
        return listOps.leftPop(AwardConstants.CODE_REDIS_PREFIX + prizeId);
    }

    @Override
    public int length(String prizeId) {
        if (StringUtils.isEmpty(prizeId)) {
            return 0;
        }
        return listOps.size(AwardConstants.CODE_REDIS_PREFIX + prizeId).intValue();
    }
}
