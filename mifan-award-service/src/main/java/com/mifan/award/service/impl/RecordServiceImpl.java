package com.mifan.award.service.impl;

import com.mifan.award.dao.RecordDao;
import com.mifan.award.domain.Record;
import com.mifan.award.service.RecordService;
import org.moonframework.model.mongodb.service.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("recordService")
public class RecordServiceImpl extends AbstractBaseService<Record, RecordDao> implements RecordService {
    @Autowired
    RecordDao recordDao;

    @Override
    public Page<Record> findByCreateTimeLessThan(Date createTime, Pageable pageable) {
        return recordDao.findByCreateTimeLessThan(createTime, pageable);
    }

    @Override
    public List<Record> findByCreateTimeLessThan(Date createTime, Sort sort) {
        return recordDao.findByCreateTimeLessThan(createTime, sort);
    }

    @Override
    public List<Record> findByCreateTimeLessThan(Date createTime) {
        return recordDao.findByCreateTimeLessThan(createTime);
    }
}
