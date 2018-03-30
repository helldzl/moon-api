package com.mifan.article.service;

import com.mifan.article.domain.MpDownloads;

import java.util.List;
import java.util.Map;

import org.moonframework.model.mybatis.service.BaseService;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
public interface MpDownloadsService extends BaseService<MpDownloads> {
    
    /**
     * 快速下载列表
     * @param assistantsSize 
     * @param driversSize
     * @param softwareSize
     * @return
     */
    Map<String,List<MpDownloads>> fastDownload(int assistantsSize,int driversSize,int softwareSize);
    /**
     * 根据文章标题查询相关下载
     * @param topicTitle
     * @return
     */
    List<MpDownloads> relatedTitle(String topicTitle);
}
