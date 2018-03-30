package com.mifan.award.service.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mifan.award.domain.Prize;
import com.mifan.award.domain.Record;
import com.mifan.award.service.CodeService;
import com.mifan.award.service.PrizeService;
import com.mifan.award.service.RecordService;
import com.mifan.award.service.contants.AwardConstants;
import com.mifan.award.service.enums.PrizeState;
import com.mifan.award.service.utils.HttpClientUtil;
import org.moonframework.core.toolkit.idworker.IdWorkerUtil;
import org.moonframework.model.mongodb.domain.Pages;
import org.moonframework.validation.InvalidException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("prizeStateListener")
public class PrizeStateListener {
    private static Log logger = LogFactory.getLog(PrizeStateListener.class);

    @Autowired
    private PrizeService prizeService;
    @Autowired
    private RecordService recordService;
    @Autowired
    private CodeService codeService;

    @Value("${application.scheduler.enable.prize:false}")
    private boolean enablePrize;

    @Scheduled(cron = "0/5 * * * * ?")
    public void listenPrizeCreateState() {
        if (!enablePrize) {
            return;
        }
        Prize findPrize = new Prize();
        findPrize.setState(PrizeState.CREATE.getState());
        List<Prize> findPrizeList = prizeService.findAll(Example.of(findPrize));
        for (Prize prize : findPrizeList) {
            if (codeService.length(prize.getId()) == 0L) {
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

    @Scheduled(cron = "0/55 * * * * ?")
    public void listenPrizeComputingState() {
        if (!enablePrize) {
            return;
        }

        Prize findPrize = new Prize();
        findPrize.setState(PrizeState.COMPUTING.getState());
        List<Prize> findPrizeList = prizeService.findAll(Example.of(findPrize));
        for (Prize prize : findPrizeList) {
            // 修改Prize状态为计算中
            updatePrizeToCompleteState(prize.getId());
        }
        logger.debug("========Prize Computing State Listening End========");
    }

    public boolean checkPrizeIsFinished(String id) {
        // 根据id查找prize信息
        Prize findPrize = prizeService.findOne(id);
        // 如果找不到返回false
        if (findPrize == null) {
            return false;
        }

        // 根据prize查找已经存在的record
        Record findRecord = new Record();
        findRecord.setPrizeId(findPrize.getId());
        List<Record> prizeRecordList = recordService.findAll(Example.of(findRecord));

        if(CollectionUtils.isEmpty(prizeRecordList)) {
            codeService.add(id);
            return false;
        }

        // 根据record记录得到已经发出去的code列表
        List prizeCodeList = new ArrayList<String>();
        for (Record prizeRecord : prizeRecordList) {
            prizeCodeList.addAll(prizeRecord.getDrawCode());
        }
        // 如果code总数没有超过prize的购买次数false并且
        if(findPrize.getBuyTimes() > prizeCodeList.size()) {
            // 自动补充redis的code
            codeService.reload(id,prizeCodeList);
            return false;
        }
        return true;
    }

    public void finishPrize(String id) {
        updatePrizeToComputingState(id);
        updatePrizeToCompleteState(id);
        updatePrizeToCreateState(id);
    }

    private void updatePrizeToComputingState(String id) {
        Prize findPrize = prizeService.findOne(id);
        if (findPrize != null) {
            String result = HttpClientUtil.getHttpClient(AwardConstants.CODE_HTTP_API);
            JSONObject jsonResult = (JSONObject) JSONObject.parse(result);
            String lottery = jsonResult.getString("data");
            JSONArray jsonLottery = (JSONArray) JSONObject.parse(lottery);

            findPrize.setLotteryCode(jsonLottery.getJSONObject(0)
                    .getString("opencode").replace(",", ""));
            findPrize.setLotteryPeriod(jsonLottery.getJSONObject(0)
                    .getString("expect"));
            findPrize.setLotteryTime(new Date(Long.parseLong(jsonLottery.getJSONObject(0)
                    .getString("opentimestamp") + "000")));
            findPrize.setFinishTime(new Date());
            findPrize.setState(PrizeState.COMPUTING.getState());

            prizeService.save(findPrize);
        }
    }

    private void updatePrizeToCompleteState(String id) {
        Prize findPrize = prizeService.findOne(id);
        if (findPrize != null) {
            // 获取本次夺宝截止时间之前的指定数目的抽奖时间用于计算夺宝号码
            Sort sort = null;
            Pages.SortBuilder sortBuilder = Pages.sortBuilder();
            sort = sortBuilder.add("createTime",false).build();
            PageRequest pageRequest = Pages.builder()
                    .page(1)
                    .size(AwardConstants.AWARD_RESULT_RECORD_SIZE)
                    .sort(sort)
                    .build();
            List<Record> findRecordList = recordService.findByCreateTimeLessThan
                    (findPrize.getFinishTime(),pageRequest).getContent();

            // 计算夺宝中奖号码
            Long recordTimeTotal = 0L;
            String recordNum;
            for (Record resultRecord : findRecordList) {
                SimpleDateFormat HHmmss = new SimpleDateFormat("HHmmss");
                SimpleDateFormat sss = new SimpleDateFormat("SSS");
                recordNum = HHmmss.format(resultRecord.getCreateTime().getTime())
                        + sss.format(resultRecord.getCreateTime().getTime());
                recordTimeTotal += Long.parseLong(recordNum);
            }
            Long computeTotal = recordTimeTotal + Long.parseLong(findPrize.getLotteryCode());
            Long remainder = computeTotal % findPrize.getBuyTimes();
            String code = String.valueOf(remainder + AwardConstants.CODE_NUM_PREFIX);

            // 获取获奖者参与本次夺宝的记录信息
            Record findAwardRecord = new Record();
            findAwardRecord.setPrizeId(findPrize.getId());
            List<Record> awardRecordList = recordService.findAll(Example.of(findAwardRecord));
            String userId = "";
            Date userTime = null;
            List userCodeList = new ArrayList<String>();
            for (Record awardRecord : awardRecordList) {
                if(awardRecord.getDrawCode().contains(code)){
                    userId = awardRecord.getUserId();
                    userTime = awardRecord.getCreateTime();

                    // 获取获奖者参与本次夺宝的相关记录
                    Record findUserRecord = new Record();
                    findUserRecord.setPrizeId(findPrize.getId());
                    findUserRecord.setUserId(userId);
                    for (Record userRecord : recordService.findAll(Example.of(findUserRecord))) {
                        userCodeList.addAll(userRecord.getDrawCode());
                    }
                }

            }

            // 保存中奖相关信息
            findPrize.setRecordTimeTotal(String.valueOf(recordTimeTotal));
            findPrize.setCode(code);
            findPrize.setUserId(userId);
            findPrize.setUserCodes(userCodeList);
            findPrize.setUserTime(userTime);
            findPrize.setState(PrizeState.COMPLETE.getState());
            prizeService.save(findPrize);
        } else {
            throw InvalidException.builder().setMessage("Data.notfound").build();
        }
    }
    private void updatePrizeToCreateState(String id) {
        Prize findPrize = prizeService.findOne(id);
        if (findPrize != null) {
            String saveId = IdWorkerUtil.nextMongoId();
            findPrize.setId(saveId);
            findPrize.setState(PrizeState.CREATE.getState());
            findPrize.setCreateTime(new Date());

            findPrize.setLotteryCode(null);
            findPrize.setLotteryPeriod(null);
            findPrize.setLotteryTime(null);
            findPrize.setFinishTime(null);

            findPrize.setRecordTimeTotal(null);
            findPrize.setCode(null);
            findPrize.setUserId(null);
            findPrize.setUserCodes(null);
            findPrize.setUserTime(null);

            prizeService.save(findPrize);
            codeService.add(saveId);
        } else {
            throw InvalidException.builder().setMessage("Data.notfound").build();
        }
    }
}
