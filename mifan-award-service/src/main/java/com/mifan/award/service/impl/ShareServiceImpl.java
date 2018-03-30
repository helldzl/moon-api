package com.mifan.award.service.impl;

import com.mifan.award.dao.ShareDao;
import com.mifan.award.domain.Share;
import com.mifan.award.service.ShareService;
import org.moonframework.model.mongodb.service.AbstractBaseService;
import org.springframework.stereotype.Service;

@Service("shareService")
public class ShareServiceImpl extends AbstractBaseService<Share, ShareDao> implements ShareService {

}
