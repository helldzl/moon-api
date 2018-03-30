package com.mifan.article.service;

import com.mifan.article.domain.TopicsModel;
import org.moonframework.model.mybatis.service.BaseService;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-11-08
 */
public interface TopicsModelService extends BaseService<TopicsModel> {

    /**
     * <p>分类数据</p>
     *
     * @param forumId forumId
     * @param topicId topicId
     */
    void classification(Long forumId, Long topicId);

}
