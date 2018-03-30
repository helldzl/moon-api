package com.mifan.award.service.impl;

import com.mifan.award.dao.NoticeDao;
import com.mifan.award.domain.Notice;
import com.mifan.award.service.NoticeService;
import org.moonframework.core.toolkit.generator.IdGenerator;
import org.moonframework.model.mongodb.service.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("noticeService")
public class NoticeServiceImpl extends AbstractBaseService<Notice, NoticeDao> implements NoticeService {
}