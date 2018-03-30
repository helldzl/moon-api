package com.mifan.award.service.impl;

import com.mifan.award.domain.*;
import com.mifan.award.domain.support.UserKarmaLogs;
import com.mifan.award.domain.support.UserProfiles;
import com.mifan.award.domain.support.UserRecord;
import com.mifan.award.service.*;
import com.mifan.award.service.contants.AwardConstants;
import com.mifan.award.service.enums.IfShareState;
import com.mifan.award.service.enums.PrizeState;
import com.mifan.award.service.feign.user.UserKarmaLogsClient;
import com.mifan.award.service.feign.user.UserProfilesClient;
import com.mifan.award.service.quartz.PrizeStateListener;
import org.moonframework.model.mongodb.domain.Pages;
import org.moonframework.model.mongodb.enums.DeleteState;
import org.moonframework.web.feign.Feigns;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service("awardService")
public class AwardServiceImpl implements AwardService {

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private ShareService shareService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private PrizeStateListener prizeStateService;

    @Autowired
    private UserProfilesClient userProfilesClient;

    @Autowired
    private UserKarmaLogsClient userKarmaLogsClient;

    @Override
    public Prize queryForPrizeObject(String id) {
        Prize findEntity = prizeService.findOne(id);
        addInfoToPrize(findEntity);
        return findEntity;
    }

    @Override
    public Record queryForRecordObject(String id) {
        Record findEntity = recordService.findOne(id);
        addInfoToRecord(findEntity);
        return findEntity;
    }

    @Override
    public Notice queryForNoticeObject(String id) {
        Notice findEntity = noticeService.findOne(id);
        return findEntity;
    }

    @Override
    public Share queryForShareObject(String id) {
        Share findEntity = shareService.findOne(id);
        addInfoToShare(findEntity);
        return findEntity;
    }

    @Override
    public Page<Prize> queryForPrizeList(Example<Prize> example, Pageable pageable) {
        Page<Prize> result = prizeService.findAll(example, pageable);
        List<Prize> prizeList = result.getContent();
        if (!CollectionUtils.isEmpty(prizeList)) {
            for (Prize prize : prizeList) {
                addInfoToPrize(prize);
            }
        }
        return result;
    }

    @Override
    public Page<Record> queryForRecordList(Example<Record> example, Pageable pageable) {
        Page<Record> result = recordService.findAll(example, pageable);
        List<Record> recordList = result.getContent();
        if (!CollectionUtils.isEmpty(recordList)) {
            for (Record record : recordList) {
                addInfoToRecord(record);
            }
        }
        return result;
    }

    @Override
    public Page<Share> queryForShareList(Example<Share> example, Pageable pageable) {
        Page<Share> result = shareService.findAll(example, pageable);
        List<Share> shareList = result.getContent();
        if (!CollectionUtils.isEmpty(shareList)) {
            for (Share share : shareList) {
                addInfoToShare(share);
            }
        }
        return result;
    }

    @Override
    public Page<Category> queryForCategoryList(Example<Category> example, Pageable pageable) {
        Page<Category> result = categoryService.findAll(example, pageable);
        return result;
    }

    @Override
    public Page<Notice> queryForNoticeList(Example<Notice> example, Pageable pageable) {
        Page<Notice> result = noticeService.findAll(example, pageable);
        return result;
    }

    @Override
    public int saveRecord(Record saveRecord) {

        Prize findPrize = prizeService.findOne(saveRecord.getPrizeId());
        UserProfiles findUser = profiles(Long.parseLong(saveRecord.getUserId()));

        if (findPrize == null || findUser == null) {
            return -404;
        }
        //saveRecord.setPrizeId(findPrize.getId());
        //saveRecord.setUserId(String.valueOf(findUser.getId()));

        // 判断用户积分是否充足
        if (saveRecord.getPersonalTimes() < 0) {
            return -4060;
        }

        if (saveRecord.getPersonalTimes() == 0) {
            saveRecord.setPersonalTimes(1);
        }

        Integer decreaseScore = saveRecord.getPersonalTimes() * findPrize.getBuyUnit();
        if (findUser.getUserKarma() < decreaseScore) {
            return -4061;
        }

        // 判断抽奖码是否够用
        List<String> drawCodeList = new ArrayList<>();
        if (codeService.length(saveRecord.getPrizeId()) >= saveRecord.getPersonalTimes()) {
            for (int i = 1; i <= saveRecord.getPersonalTimes(); i++) {
                drawCodeList.add(codeService.getOne(saveRecord.getPrizeId()));
            }
            saveRecord.setDrawCode(drawCodeList);
        } else {
            return -4062;
        }
        saveRecord.setCreateId(saveRecord.getUserId());
        saveRecord = recordService.insert(saveRecord);

        if (saveRecord.getId() != null) {
            UserKarmaLogs userKarmaLogs = new UserKarmaLogs();
            userKarmaLogs.setUserId(Long.parseLong(saveRecord.getUserId()));
            userKarmaLogs.setAction(UserKarmaLogs.Event.AWARD);
            userKarmaLogs.setScore(-decreaseScore);

            // REMOTE CALL
            ResponseEntity<Data<Map<String, Object>>> response = userKarmaLogsClient.doPost(Data.of(userKarmaLogs));
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("奖励米粒失败");
            }

            // userKarmaLogsService.save(userKarmaLogs);

            // 判断本次抽奖是否结束
            if (codeService.length(saveRecord.getPrizeId()) == 0
                    && prizeStateService.checkPrizeIsFinished(saveRecord.getPrizeId())) {
                prizeStateService.finishPrize(saveRecord.getPrizeId());
            }
        }

        return 1;
    }

    @Override
    public int saveShare(Share saveshare) {
        Prize findPrize = prizeService.findOne(saveshare.getPrizeId());
        if (findPrize == null) {
            return -404;
        }
        saveshare = shareService.insert(saveshare);
        return 1;
    }

    @Override
    public Page<UserRecord> queryForUserPrizeRecordList(Example<Record> example, Integer prizeState, Pageable pageable) {

        long total = 0;
        List<UserRecord> userRecordList = new ArrayList<>();
        List<UserRecord> resultRecordList = new ArrayList<>();
        List<Record> recordList = recordService.findAll(example, pageable.getSort());

        if (!CollectionUtils.isEmpty(recordList)) {
            List<String> allPrizeIdList = new ArrayList<>();
            for (Record record : recordList) {
                if (!allPrizeIdList.contains(record.getPrizeId())) {
                    allPrizeIdList.add(record.getPrizeId());
                }
            }

            for (String prizeId : allPrizeIdList) {
                UserRecord userRecord = new UserRecord();
                // 奖品信息赋值
                userRecord.setPrizeId(prizeId);
                Prize prize = prizeService.findOne(prizeId);
                if (prize != null) {
                    userRecord.setPrizeProductId(prize.getProductId());
                    if (prize.getHeadPicList() != null && (prize.getHeadPicList().size() > 0)) {
                        userRecord.setPrizeHeaderPic(prize.getHeadPicList().get(0).getImgURL());
                    }
                    userRecord.setPrizeBuyTimes(prize.getBuyTimes());
                    userRecord.setPrizeRestTimes(String.valueOf(codeService.length(prize.getId())));
                    userRecord.setPrizeTitle(prize.getTitle());
                    userRecord.setPrizeCreateTime(prize.getCreateTime());
                    userRecord.setPrizeFinishTime(prize.getFinishTime());
                    userRecord.setPrizeState(prize.getState());
                    userRecord.setPrizeCode(prize.getCode());
                    userRecord.setAwardUserId(prize.getUserId());
                    userRecord.setAwardUserCodes(prize.getUserCodes());
                    userRecord.setAwardUserTime(prize.getUserTime());
                }
                userRecordList.add(userRecord);
            }

            if (prizeState.equals(0)) {
                total = userRecordList.stream().count();
                resultRecordList = userRecordList.stream()
                        .skip(pageable.getOffset()).limit(pageable.getPageSize())
                        .collect(Collectors.toList());
            } else {
                total = userRecordList.stream()
                        .filter(userRecord -> userRecord.getPrizeState() == prizeState)
                        .count();
                resultRecordList = userRecordList.stream()
                        .filter(userRecord -> userRecord.getPrizeState() == prizeState)
                        .skip(pageable.getOffset()).limit(pageable.getPageSize())
                        .collect(Collectors.toList());

            }

            for (UserRecord userRecord : resultRecordList) {

                // 根据用户USER_ID + PRIZE_ID获取其抽奖记录
                Record findEntity = new Record();
                findEntity.setUserId(example.getProbe().getUserId());
                findEntity.setPrizeId(userRecord.getPrizeId());

                List<Record> prizeRecordList = recordService.findAll(Example.of(findEntity));
                if (prizeRecordList != null) {
                    // 记录信息赋值
                    int recordBuyTimes = 0;
                    for (Record record : prizeRecordList) {
                        recordBuyTimes += record.getDrawCode().size();
                    }
                    userRecord.setRecordBuyTimes(recordBuyTimes);
                    userRecord.setRecordList(prizeRecordList);
                }
            }

            // find user nickname
            Set<Long> userIds = resultRecordList.stream().map(UserRecord::getAwardUserId).map(Long::valueOf).collect(Collectors.toSet());
            if (!userIds.isEmpty()) {
//                Map<String, String> map = userProfilesService.findAll(
//                        Restrictions.in(BaseEntity.ID, userIds.toArray()),
//                        Fields.builder().add(BaseEntity.ID).add(UserProfiles.NICKNAME).build())
//                        .stream().collect(Collectors.toMap(o -> o.getId().toString(), UserProfiles::getNickname));
                Map<Long, String> map = profiles(userIds);
                // JOIN result
                for (UserRecord userRecord : userRecordList) {
                    userRecord.setAwardNickname(map.get(userRecord.getAwardUserId()));
                }
            }
        }
        return new PageImpl<UserRecord>(resultRecordList, pageable, total);

    }

    @Override
    public Page<UserRecord> queryForUserLuckRecordList(Example<Prize> example, Pageable pageable) {

        long total = 0;
        List<UserRecord> userRecordList = new ArrayList<>();
        Page<Prize> result = prizeService.findAll(example, pageable);
        total = result.getTotalElements();
        List<Prize> prizeList = result.getContent();

        if (!CollectionUtils.isEmpty(prizeList)) {
            //查询出所有的晒单记录
            Share findEntity = new Share();
            findEntity.setUserId(example.getProbe().getUserId());
            findEntity.setIsDel(DeleteState.CREATE.getCode());

            List<Share> shareList = shareService.findAll(Example.of(findEntity));

            for (Prize prize : prizeList) {
                UserRecord userRecord = new UserRecord();
                userRecord.setPrizeHeaderPic(prize.getHeadPicList().get(0).getImgURL());
                userRecord.setPrizeTitle(prize.getTitle());
                userRecord.setPrizeId(prize.getId());
                userRecord.setPrizeProductId(prize.getProductId());
                userRecord.setPrizeBuyTimes(prize.getBuyTimes());
                userRecord.setPrizeCode(prize.getCode());
                userRecord.setAwardUserId(prize.getUserId());
                userRecord.setAwardUserCodes(prize.getUserCodes());
                userRecord.setAwardUserTime(prize.getUserTime());
                userRecord.setRecordBuyTimes(prize.getUserCodes().size());
                userRecord.setPrizeCreateTime(prize.getCreateTime());
                userRecord.setPrizeFinishTime(prize.getFinishTime());
                userRecord.setPrizeState(prize.getState());

                userRecord.setIsShared(IfShareState.UNCREATE.getCode());
                for (Share share : shareList) {
                    if (share.getPrizeId().equals(prize.getId())) {
                        userRecord.setIsShared(IfShareState.CREATE.getCode());
                        userRecord.setShareId(share.getId());
                        userRecord.setShareState(share.getState());
                        userRecord.setShareSummary(share.getSummary());
                        userRecord.setShareCreateTime(share.getCreateTime());
                        userRecord.setShareDescription(share.getDescription());
                        userRecord.setSharePicList(share.getPicList());
                    }
                }

                userRecordList.add(userRecord);
            }
        }
        return new PageImpl<UserRecord>(userRecordList, pageable, total);
    }

    @Override
    public Page<UserRecord> queryForUserShareList(Example<Share> example, Pageable pageable) {

        long total = 0;
        List<UserRecord> userRecordList = new ArrayList<>();

        Page<Share> result = shareService.findAll(example, pageable);
        total = result.getTotalElements();
        List<Share> shareList = result.getContent();

        if (!CollectionUtils.isEmpty(shareList)) {
            userRecordList = new ArrayList<>();
            for (Share share : shareList) {
                UserRecord userRecord = new UserRecord();

                Prize prize = prizeService.findOne(share.getPrizeId());

                if (prize != null) {
                    //奖品的相关信息
                    userRecord.setPrizeProductId(prize.getProductId());
                    userRecord.setPrizeTitle(prize.getTitle());
                    userRecord.setPrizeCode(prize.getCode());
                }
                userRecord.setIsShared(IfShareState.CREATE.getCode());
                userRecord.setShareState(share.getState());
                userRecord.setShareSummary(share.getSummary());
                userRecord.setShareCreateTime(share.getCreateTime());
                userRecord.setShareDescription(share.getDescription());
                userRecord.setShareId(share.getId());
                userRecord.setSharePicList(share.getPicList());
                userRecord.setPrizeId(share.getPrizeId());

                userRecordList.add(userRecord);
            }
        }
        return new PageImpl<UserRecord>(userRecordList, pageable, total);
    }


    private void addInfoToPrize(Prize prize) {
        if (prize != null) {
            if (!StringUtils.isEmpty(prize.getUserId())) {
                // Users user = userService.queryForObject(Long.parseLong(prize.getUserId()));
                UserProfiles profile = profiles(Long.valueOf(prize.getUserId()));
                if (profile != null) {
                    //TODO 马赛克名字
                    prize.setUserName(profile.getNickname());
                    prize.setUserAvatar(profile.getUserAvatar());
                }
            }
            prize.setPeriod(prize.getId());
            prize.setExistingTimes(codeService.length(prize.getId()));

            if (prize.getState().equals(PrizeState.COMPLETE.getState())) {
                Pages.SortBuilder sortBuilder = Pages.sortBuilder();
                Sort sort = sortBuilder.add("createTime", false).build();
                PageRequest pageRequest = Pages.builder()
                        .page(1)
                        .size(AwardConstants.AWARD_RESULT_RECORD_SIZE)
                        .sort(sort)
                        .build();
                List<Record> findRecordList = recordService.findByCreateTimeLessThan
                        (prize.getFinishTime(), pageRequest).getContent();

                if (!CollectionUtils.isEmpty(findRecordList)) {
                    for (Record record : findRecordList) {
                        addInfoToRecord(record);
                    }
                }
                prize.setComputeRecordList(findRecordList);
            }
        }
    }

    private void addInfoToRecord(Record record) {
        if (record != null) {
            if (!StringUtils.isEmpty(record.getUserId())) {
                // Users user = userService.queryForObject(Long.parseLong(record.getUserId()));
                UserProfiles profile = profiles(Long.valueOf(record.getUserId()));
                if (profile != null) {
                    //TODO 马赛克名字
                    record.setUserName(profile.getNickname());
                    record.setUserAvatar(profile.getUserAvatar());
                }
            }
            Prize prize = prizeService.findOne(record.getPrizeId());
            if (prize != null) {
                record.setPrizeTitle(prize.getTitle());
            }
        }
    }

    private void addInfoToShare(Share share) {
        if (share != null) {
            if (!StringUtils.isEmpty(share.getUserId())) {
                // Users user = userService.queryForObject(Long.parseLong(share.getUserId()));
                UserProfiles profile = profiles(Long.valueOf(share.getUserId()));
                if (profile != null) {
                    //TODO 马赛克名字
                    share.setUserName(profile.getNickname());
                    share.setUserAvatar(profile.getUserAvatar());
                }
            }

            if (!StringUtils.isEmpty(share.getPrizeId())) {
                Prize prize = prizeService.findOne(share.getPrizeId());
                if (prize != null) {
                    share.setPrizeTitle(prize.getTitle());
                    share.setPrizeCode(prize.getCode());
                }
            }
        }
    }

    private UserProfiles profiles(Long userId) {
        Feigns.asRemote();
        ResponseEntity<Data<UserProfiles>> response = userProfilesClient.doGet(userId);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getData();
        } else {
            return null;
        }
    }

    private Map<Long, String> profiles(Collection<? extends Number> collection) {
        ResponseEntity<Data<List<UserProfiles>>> response = userProfilesClient.doGetPage(Feigns.params()
                .page(p -> p.page(1).size(100))
                .fields("user_profiles", f -> f.add(UserProfiles.ID).add(UserProfiles.NICKNAME))
                .filter(UserProfiles.ID, collection)
                .build());
        return response.getBody().getData().stream().collect(Collectors.toMap(UserProfiles::getId, UserProfiles::getNickname));
    }

}

