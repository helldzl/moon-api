package com.mifan.reward.service.impl;

import com.mifan.reward.dao.PrizesDao;
import com.mifan.reward.domain.*;
import com.mifan.reward.domain.support.RewardUsers;
import com.mifan.reward.domain.support.UserProfiles;
import com.mifan.reward.service.PrizesService;
import com.mifan.reward.service.RedisCodesService;
import com.mifan.reward.service.contants.RewardContants;
import com.mifan.reward.service.enums.EntityState;
import com.mifan.reward.service.enums.PrizesState;
import com.mifan.reward.service.feign.FeignBaseServiceAdapter;
import org.moonframework.core.toolkit.idworker.IdWorkerUtil;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.QueryFieldOperator;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.*;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author DXZ
 * @version 1.0
 * @since 2017-08-31
 */
@Service
public class PrizesServiceImpl extends FeignBaseServiceAdapter<Prizes, PrizesDao> implements PrizesService {

    @Autowired
    private RedisCodesService redisCodesService;

    @Override
    public Prizes findOne(Criterion criterion, Iterable<? extends Field> fields) {
        Prizes prize =  super.findOne(criterion, fields);
        if (prize == null) {
            return null;
        }
        prize.setExistingTimes(redisCodesService.length(prize.getId()));
        return prize;
    }


    @Override
    public Prizes queryForObject(Long id, Iterable<? extends Field> fields) {
        Prizes prize =  super.queryForObject(id, fields);
        if (prize == null) {
            return null;
        }

        prize.setGoodInfo(
                Services.queryForObject(
                        Goods.class,
                        prize.getGoodId(),
                        null));

        prize.setExistingTimes(redisCodesService.length(prize.getId()));

        if(prize.getState().equals(PrizesState.COMPLETE.getState())) {
            prize.setPrizeInfo(
                    Services.findOne(
                            PrizeInfo.class,
                            Restrictions.and(
                                    Restrictions.eq(PrizeInfo.ID, id),
                                    Restrictions.eq(PrizeInfo.ENABLED, EntityState.ENABLED.getState())
                            )
                    )
            );

            addRewardInfo(prize.getPrizeInfo());
        }

        return prize;
    }

    @Override
    public Page<Prizes> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Page<Prizes> prizesPage = super.findAll(criterion, pageable, fields);

        if (!prizesPage.hasContent()) {
            return prizesPage;
        }

        Map<Long, Prizes> prizesMap = prizesPage.getContent().stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        Set<Long> prizeIds = prizesMap.keySet();
        Set<Long> goodsIds = prizesPage.getContent().stream().map(Prizes::getGoodId).collect(Collectors.toSet());

        Services.findAll(PrizeInfo.class, Restrictions.in(BaseEntity.ID, prizeIds.toArray()))
                .forEach(info -> prizesMap.get(info.getId()).setPrizeInfo(info));

        Page<Goods> goodsPage = Services.findAll(Goods.class,
                Restrictions.in(Goods.ID, goodsIds.toArray()),
                pageable,
                Goods.DEFAULT_FIELDS);
        Map<Long, Goods> goodsMap = goodsPage.getContent().stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

        prizesMap.forEach((prizeId,prize) -> {
            prize.setGoodInfo(goodsMap.get(prize.getGoodId()));
            prize.setExistingTimes(redisCodesService.length(prize.getId()));
        });

        return prizesPage;
    }

    @Override
    public <S extends Prizes> int saveOrUpdate(S entity) {

        if (StringUtils.isEmpty(entity.getPeriod())) {
            entity.setPeriod(IdWorkerUtil.nextMongoId());
        }
        if (entity.getCreator() == null) {
            entity.setCreator(0L);
        }

        int n = super.saveOrUpdate(entity);

        PrizeInfo info = entity.getPrizeInfo();
        info.setId(entity.getId());
        Services.saveOrUpdate(PrizeInfo.class, info);

        return n;
    }

    private void addRewardInfo(PrizeInfo prizeInfo) {

        addRewardRecordInfo(prizeInfo);
        addRewardUserInfo(prizeInfo);
        addRewardUserRecordInfo(prizeInfo);
    }

    private void addRewardRecordInfo(PrizeInfo prizeInfo) {
        prizeInfo.setResultRecordInfo(Services.findAll(
                Records.class,
                Restrictions.and(
                        Restrictions.eq(Records.ENABLED, EntityState.ENABLED.getState()),
                        Restrictions.lt(Records.CREATED,prizeInfo.getFinishTime())
                        ),
                Pages.builder()
                        .page(1)
                        .size(RewardContants.REWARD_RESULT_RECORD_SIZE)
                        .sort(Pages.sortBuilder().add(Records.CREATED, false).build())
                        .build()
        ).getContent());
    }

    private void addRewardUserInfo(PrizeInfo prizeInfo) {
        prizeInfo.setLuckUserInfo(getUserInfo(prizeInfo.getLuckUserId()));
    }

    private void addRewardUserRecordInfo(PrizeInfo prizeInfo) {
        if(prizeInfo.getLuckUserInfo() != null) {
            prizeInfo.getLuckUserInfo().setCodes(
                    Services.findAll(
                            Codes.class,
                            Restrictions.and(
                                    Restrictions.eq(Codes.ENABLED, EntityState.ENABLED.getState()),
                                    Restrictions.eq(Codes.USER_ID,prizeInfo.getLuckUserId()),
                                    Restrictions.eq(Codes.PRIZE_ID,prizeInfo.getId())
                            )
                    )
            );
        }
    }
}
