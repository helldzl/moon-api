package com.mifan.reward.service.impl;

import com.mifan.reward.dao.ImagesDao;
import com.mifan.reward.domain.Images;
import com.mifan.reward.service.ImagesService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author DXZ
 * @version 1.0
 * @since 2017-08-31
 */
@Service
public class ImagesServiceImpl extends AbstractBaseService<Images, ImagesDao> implements ImagesService {
}
