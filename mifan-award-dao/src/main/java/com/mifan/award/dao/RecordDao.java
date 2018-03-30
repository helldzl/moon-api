package com.mifan.award.dao;

import com.mifan.award.domain.Record;
import org.moonframework.model.mongodb.repository.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

public interface RecordDao extends BaseDao<Record> {
    Page<Record> findByCreateTimeLessThan(Date createTime, Pageable pageable);
    List<Record> findByCreateTimeLessThan(Date createTime, Sort sort);
    List<Record> findByCreateTimeLessThan(Date createTime);
}
