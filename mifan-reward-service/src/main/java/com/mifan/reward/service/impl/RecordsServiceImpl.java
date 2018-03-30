package com.mifan.reward.service.impl;

import com.mifan.reward.dao.RecordsDao;
import com.mifan.reward.domain.Codes;
import com.mifan.reward.domain.Goods;
import com.mifan.reward.domain.Prizes;
import com.mifan.reward.domain.Records;
import com.mifan.reward.domain.support.RewardUsers;
import com.mifan.reward.domain.support.UserKarmaLogs;
import com.mifan.reward.domain.support.UserProfiles;
import com.mifan.reward.service.RecordsService;
import com.mifan.reward.service.RedisCodesService;
import com.mifan.reward.service.enums.EntityState;
import com.mifan.reward.service.feign.FeignBaseServiceAdapter;
import com.mifan.reward.service.feign.user.UserKarmaLogsClient;
import com.mifan.reward.service.quartz.RewardPrizesStateListener;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.feign.Feigns;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * @author DXZ
 * @version 1.0
 * @since 2017-08-31
 */
@Service
public class RecordsServiceImpl extends FeignBaseServiceAdapter<Records, RecordsDao> implements RecordsService {

    @Autowired
    private RedisCodesService redisCodesService;

    @Autowired
    private UserKarmaLogsClient userKarmaLogsClient;
    @Autowired
    private RewardPrizesStateListener rewardPrizesStateListener;

    @Override
    public Records queryForObject(Long id, Iterable<? extends Field> fields) {

        // 基本信息
        Records record = super.queryForObject(id, fields);
        if (record == null) {
            return null;
        }
        // 号码列表
        addCodeInfo(record);
        // 奖品信息
        addPrizeInfo(record);
        // 用户信息
        addUserInfo(record);

        return record;

    }

    @Override
    public Page<Records> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Page<Records> recordsPage = super.findAll(criterion, pageable, fields);

        if (!recordsPage.hasContent()) {
            return recordsPage;
        }

        // TODO 优化此处代码
        List<Records> recordList = recordsPage.getContent();
        if (!CollectionUtils.isEmpty(recordList)) {
            for (Records record : recordList) {
                addCodeInfo(record);
                addPrizeInfo(record);
                addUserInfo(record);
            }
        }

        return recordsPage;
    }

    @Override
    public Page<Records> findAll(Criterion criterion, Pageable pageable) {
        Page<Records> recordsPage = super.findAll(criterion, pageable);

        if (!recordsPage.hasContent()) {
            return recordsPage;
        }

        Map<Long, Records> recordsMap = recordsPage.getContent().stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        Set<Long> recordsIds = recordsMap.keySet();
        Services.findAll(Codes.class,
                Restrictions.and(
                        Restrictions.in(Codes.RECORD_ID, recordsIds.toArray()),
                        Restrictions.eq(Codes.ENABLED, EntityState.ENABLED.getState())),
                Codes.DEFAULT_FIELDS)
                .stream()
                .collect(Collectors.groupingBy(Codes::getRecordId, mapping(Codes::getCode, toList())))
                .forEach((recordId, codeInfos) -> recordsMap.get(recordId).setCodeInfo(codeInfos));

        return recordsPage;
    }

    @Override
    public <S extends Records> int save(S entity) {
        Prizes findPrize = Services.findOne(Prizes.class,entity.getPrizeId());
        UserProfiles findUser = profiles(entity.getUserId());

        if (findPrize == null || findUser == null) {
            return -404;
        }
        Goods findGood = Services.findOne(Goods.class,findPrize.getGoodId());

        if (entity.getBuyTimes() < 0) {
            return -4060;
        }

        if (entity.getBuyTimes() == 0) {
            entity.setBuyTimes(1);
        }

        // 判断用户积分是否充足
        Integer decreaseScore = entity.getBuyTimes() * findGood.getBuyUnit();
        if (findUser.getUserKarma() < decreaseScore) {
            return -4061;
        }

        // 判断抽奖码是否够用
        if (redisCodesService.length(entity.getPrizeId()) < entity.getBuyTimes()) {
            return -4062;
        }
        int n = super.save(entity);

        Codes code = new Codes();
        code.setPrizeId(entity.getPrizeId());
        code.setRecordId(entity.getId());
        code.setUserId(entity.getUserId());
        for (int i = 0; i < entity.getBuyTimes(); i++) {
            code.setCode(redisCodesService.getOne(entity.getPrizeId()));
            n += Services.save(Codes.class, code);
        }

        if (entity.getId() != null) {
            UserKarmaLogs userKarmaLogs = new UserKarmaLogs();
            userKarmaLogs.setUserId(entity.getUserId());
            userKarmaLogs.setAction(UserKarmaLogs.Event.AWARD);
            userKarmaLogs.setScore(-decreaseScore);

            // REMOTE CALL
            ResponseEntity<Data<Map<String, Object>>> response = userKarmaLogsClient.doPost(Data.of(userKarmaLogs));
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("扣除米粒失败");
            }

            // 判断本次抽奖是否结束
            if (redisCodesService.length(entity.getPrizeId()) == 0
                    && rewardPrizesStateListener.checkPrizeIsFinished(entity.getPrizeId())) {
                rewardPrizesStateListener.finishPrize(entity.getPrizeId());
            }
        }
        return n;
    }

    private void addCodeInfo(Records record) {
        record.setCodeInfo(Services.findAll(
                Codes.class,
                Restrictions.and(
                        Restrictions.eq(Codes.RECORD_ID, record.getId()),
                        Restrictions.eq(Codes.ENABLED, EntityState.ENABLED.getState())),
                Fields.builder().add(Codes.CODE).build())
                .stream()
                .map(Codes::getCode)
                .collect(toList()));
    }

    private void addPrizeInfo(Records record) {
        record.setPrizeInfo(Services.queryForObject(
                Prizes.class,
                record.getPrizeId(),
                null));
    }

    private void addUserInfo(Records record) {
        record.setUserInfo(getUserInfo(record.getUserId()));
    }

}
