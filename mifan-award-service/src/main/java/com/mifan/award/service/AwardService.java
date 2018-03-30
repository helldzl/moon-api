package com.mifan.award.service;

import com.mifan.award.domain.*;
import com.mifan.award.domain.support.UserRecord;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AwardService {
    Prize queryForPrizeObject(String id);

    Record queryForRecordObject(String id);

    Notice queryForNoticeObject(String id);

    Share queryForShareObject(String id);

    Page<Prize> queryForPrizeList(Example<Prize> example, Pageable pageable);

    Page<Record> queryForRecordList(Example<Record> example, Pageable pageable);

    Page<Share> queryForShareList(Example<Share> example, Pageable pageable);

    Page<Category> queryForCategoryList(Example<Category> example, Pageable pageable);

    Page<Notice> queryForNoticeList(Example<Notice> example, Pageable pageable);

    int saveRecord(Record saveRecord);

    int saveShare(Share saveshare);

    Page<UserRecord> queryForUserPrizeRecordList(Example<Record> example, Integer prizeState, Pageable pageable);

    Page<UserRecord> queryForUserLuckRecordList(Example<Prize> example, Pageable pageable);

    Page<UserRecord> queryForUserShareList(Example<Share> example, Pageable pageable);
}
