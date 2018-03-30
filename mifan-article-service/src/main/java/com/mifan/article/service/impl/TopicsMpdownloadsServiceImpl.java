package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsMpdownloadsDao;
import com.mifan.article.domain.MpDownloads;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.TopicsMpdownloads;
import com.mifan.article.service.TopicsMpdownloadsService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.stereotype.Service;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
@Service
public class TopicsMpdownloadsServiceImpl extends AbstractBaseService<TopicsMpdownloads, TopicsMpdownloadsDao> implements TopicsMpdownloadsService {
	@Override
	public int saveTopicsMpdownloads(List<TopicsMpdownloads> topicsMpdownloads) {
		int n = 0;
		for(TopicsMpdownloads tmd : topicsMpdownloads) {
			if(!Services.exists(MpDownloads.class, tmd.getMpDownloadId())) {
				throw new IllegalStateException("下载ID不存在！");
			}
			if(!Services.exists(Topics.class, tmd.getTopicId())) {
				throw new IllegalStateException("主题ID不存在！");
			}
			n += super.saveOrUpdate(tmd);
		}
		
		return n;
	}
	@Override
	public List<MpDownloads> getDownLoadsByTopicId(Long topicId){
		if(topicId == null) {
			return Collections.emptyList();
		}
		TopicsMpdownloads find = new TopicsMpdownloads();
		find.setTopicId(topicId);
		List<TopicsMpdownloads> list = this.findAll(find);
		List<Long> ids = list.stream().map(TopicsMpdownloads::getMpDownloadId).collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(ids)) {
		    Criterion criterion = Restrictions.and(
	                Restrictions.in(MpDownloads.ID, ids.toArray()),
	                Restrictions.eq(MpDownloads.ENABLED, 1));
	        List<MpDownloads> downloads = Services.findAll(MpDownloads.class, criterion);
	        return downloads;
		}
		return Collections.emptyList();
	}
}
