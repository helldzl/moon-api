package com.mifan.batch.spider.service;

import com.mifan.batch.spider.domain.Attachments;

/**
 * Created by LiuKai on 2017/7/6.
 */
public interface AttachmentsService {
    Attachments saveOrUpdate(Attachments attachment);
}
