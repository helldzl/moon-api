package com.mifan.reward.service.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mifan.reward.domain.Codes;
import com.mifan.reward.domain.Prizes;
import com.mifan.reward.domain.Records;
import com.mifan.reward.service.PrizesService;
import com.mifan.reward.service.RecordsService;
import com.mifan.reward.service.RedisCodesService;
import com.mifan.reward.service.contants.RewardContants;
import com.mifan.reward.service.enums.PrizesState;
import com.mifan.reward.service.utils.HttpClientUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moonframework.core.toolkit.idworker.IdWorkerUtil;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.validation.InvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("rewardPrizesStateListener")
public class RewardPrizesStateListener {
    private static Log logger = LogFactory.getLog(RewardPrizesStateListener.class);

    @Autowired
    private PrizesService prizesService;
    @Autowired
    private RecordsService recordsService;
    @Autowired
    private RedisCodesService redisCodesService;

    @Value("${application.scheduler.enable.prize:false}")
    private boolean enablePrize = false;

    //@Scheduled(cron = "0/5 * * * * ?")
    public void listenPrizeCreateState() {
        if (!enablePrize) {
            return;
        }
        List<Prizes> findPrizeList = Services.findAll(Prizes.class,
                Restrictions.and(
                        Restrictions.eq(Prizes.STATE, PrizesState.CREATE.getState()),
                        Restrictions.eq(Prizes.ENABLED, 1)));

        for (Prizes prize : findPrizeList) {
            if (redisCodesService.length(prize.getId()) == 0L) {
                if (checkPrizeIsFinished(prize.getId())) {
                    // 修改Prize状态为计算中
                    updatePrizeToComputingState(prize.getId());
                    // 创建新的prize继续抽奖
                    updatePrizeToCreateState(prize.getId());

                } else {
                    logger.error("========State Listening End. ERROR redis result does not equal to database result========");
                }
            } else {
                logger.debug("========Prize Create State. No Prize Need Computing========");
            }
        }
    }

    //@Scheduled(cron = "0/55 * * * * ?")
    public void listenPrizeComputingState() {
        if (!enablePrize) {
            return;
        }
        List<Prizes> findPrizeList = Services.findAll(Prizes.class,
                Restrictions.and(
                        Restrictions.eq(Prizes.STATE, PrizesState.COMPUTING.getState()),
                        Restrictions.eq(Prizes.ENABLED, 1)));

        for (Prizes prize : findPrizeList) {
            // 修改Prize状态为计算中
            updatePrizeToCompleteState(prize.getId());
        }
        logger.debug("========Prize Computing State Listening End========");
    }

    public boolean checkPrizeIsFinished(Long id) {
        // 根据id查找prize信息
        Prizes findPrize = Services.queryForObject(Prizes.class, id, null);
        // 如果找不到返回false
        if (findPrize == null) {
            return false;
        }

        // 根据prize查找已经存在的record
        List<Records> prizeRecordList = Services.findAll(Records.class,
                Restrictions.and(
                        Restrictions.eq(Records.PRIZE_ID, findPrize.getId()),
                        Restrictions.eq(Records.ENABLED, 1)));

        if (CollectionUtils.isEmpty(prizeRecordList)) {
            redisCodesService.add(findPrize.getId());
            return false;
        }

        // 根据record记录得到已经发出去的code列表
        //List<Long> prizeCodeList = new ArrayList<>();

        Set<Long> recordsIds = prizeRecordList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        List<String> prizeCodeList = Services.findAll(Codes.class,
                Restrictions.and(
                        Restrictions.in(Codes.RECORD_ID, recordsIds.toArray()),
                        Restrictions.eq(Codes.ENABLED, 1)))
                .stream()
                .map(code -> String.valueOf(code.getCode()))
                .collect(Collectors.toList());
        //.collect(Collectors.groupingBy(Codes::getRecordId, mapping(Codes::getCode, toList())))
        //.forEach((recordId, codeInfos) -> prizeCodeList.addAll(codeInfos));

        //for (Record prizeRecord : prizeRecordList) {
        //   prizeCodeList.addAll(prizeRecord.getDrawCode());
        //}
        // 如果code总数没有超过prize的购买次数false并且
        if (findPrize.getGoodInfo().getBuyTimes() > prizeCodeList.size()) {
            // 自动补充redis的code
            redisCodesService.reload(findPrize.getId(), prizeCodeList);
            return false;
        }

        return true;
    }

    public void finishPrize(Long id) {
        updatePrizeToComputingState(id);
        updatePrizeToCompleteState(id);
        updatePrizeToCreateState(id);
    }

    private void updatePrizeToComputingState(Long id) {
        Prizes findPrize = Services.queryForObject(Prizes.class, id, null);

        if (findPrize != null) {
            String result = HttpClientUtil.getHttpClient(RewardContants.CODE_HTTP_API);
            JSONObject jsonResult = (JSONObject) JSONObject.parse(result);
            String lottery = jsonResult.getString("data");
            JSONArray jsonLottery = (JSONArray) JSONObject.parse(lottery);

            findPrize.getPrizeInfo().setLotteryCode(Integer.getInteger(jsonLottery.getJSONObject(0)
                    .getString("opencode").replace(",", "")));
            findPrize.getPrizeInfo().setLotteryPeriod(jsonLottery.getJSONObject(0)
                    .getLong("expect"));
            findPrize.getPrizeInfo().setLotteryTime(new Date(Long.parseLong(jsonLottery.getJSONObject(0)
                    .getString("opentimestamp") + "000")));
            findPrize.getPrizeInfo().setFinishTime(new Date());
            findPrize.setState(PrizesState.COMPUTING.getState());

            Services.saveOrUpdate(Prizes.class, findPrize);

        }

    }

    private void updatePrizeToCompleteState(Long id) {
        Prizes findPrize = Services.queryForObject(Prizes.class, id, null);

        if (findPrize != null) {
            // 获取本次夺宝截止时间之前的指定数目的抽奖时间用于计算夺宝号码
            List<Records> findRecordList = Services.findAll(Records.class,
                    Restrictions.and(
                            Restrictions.gt(Records.CREATED, findPrize.getPrizeInfo().getFinishTime()),
                            Restrictions.eq(Records.ENABLED, 1)),
                    Pages.builder()
                            .page(1)
                            .size(RewardContants.REWARD_RESULT_RECORD_SIZE)
                            .sort(Pages.sortBuilder().add(Records.CREATED, false).build())
                            .build(),
                    null
            ).getContent();

            // 计算夺宝中奖号码
            Long recordTimeTotal = 0L;
            String recordNum;
            SimpleDateFormat HHmmss = new SimpleDateFormat("HHmmss");
            SimpleDateFormat sss = new SimpleDateFormat("SSS");
            for (Records resultRecord : findRecordList) {
                recordNum = HHmmss.format(resultRecord.getCreated().getTime())
                        + sss.format(resultRecord.getCreated().getTime());
                recordTimeTotal += Long.parseLong(recordNum);
            }
            Long computeTotal = recordTimeTotal + findPrize.getPrizeInfo().getLotteryCode();
            Long remainder = computeTotal % findPrize.getGoodInfo().getBuyTimes();
            Long code = remainder + RewardContants.CODE_NUM_PREFIX;

            // 获取获奖者参与本次夺宝的记录信息
            List<Codes> awardCodesList = Services.findAll(Codes.class,
                    Restrictions.and(
                            Restrictions.eq(Codes.PRIZE_ID, findPrize.getId()),
                            Restrictions.eq(Codes.CODE, code),
                            Restrictions.eq(Codes.ENABLED, 1)));

            // 保存中奖相关信息
            if (!awardCodesList.isEmpty()) {
                findPrize.getPrizeInfo().setLuckUserId(awardCodesList.get(0).getUserId());
                findPrize.getPrizeInfo().setLuckCode(code);
                findPrize.getPrizeInfo().setRecordTimeTotal(recordTimeTotal);

            }
            findPrize.setState(PrizesState.COMPLETE.getState());
            Services.saveOrUpdate(Prizes.class, findPrize);

        } else {
            throw InvalidException.builder().setMessage("Data.notfound").build();
        }

    }

    private void updatePrizeToCreateState(Long id) {
        Prizes findPrize = Services.findOne(Prizes.class, id);

        if (findPrize != null) {
            Prizes savePrize = new Prizes();

            savePrize.setCategoryId(findPrize.getCategoryId());
            savePrize.setGoodId(findPrize.getGoodId());
            savePrize.setState(PrizesState.CREATE.getState());
            savePrize.setPeriod(IdWorkerUtil.nextMongoId());

            Services.saveOrUpdate(Prizes.class, savePrize);
            redisCodesService.add(findPrize.getId());

        } else {
            throw InvalidException.builder().setMessage("Data.notfound").build();
        }

    }
}
