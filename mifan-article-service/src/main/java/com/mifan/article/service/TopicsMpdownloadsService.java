package com.mifan.article.service;

import com.mifan.article.domain.MpDownloads;
import com.mifan.article.domain.TopicsMpdownloads;

import java.util.List;

import org.moonframework.model.mybatis.service.BaseService;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
public interface TopicsMpdownloadsService extends BaseService<TopicsMpdownloads> {
	/**
	 * 查询某个文章的相关下载
	 * @param topicId
	 * @return
	 */
	List<MpDownloads> getDownLoadsByTopicId(Long topicId);
	/**
	 * 用于批量保存topic和下载的关联关系
	 * @param topicsMpdownloads
	 * @return
	 */
	int saveTopicsMpdownloads(List<TopicsMpdownloads> topicsMpdownloads);
}
