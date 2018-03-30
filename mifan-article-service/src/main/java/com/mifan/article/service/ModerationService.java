package com.mifan.article.service;

import com.mifan.article.domain.Moderation;

import org.moonframework.model.mybatis.service.BaseService;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
public interface ModerationService extends BaseService<Moderation> {
    /**
     * 审核精翻
     * @param entity
     * @return
     */
    int examineHumanTranslate(Moderation entity);

    int updateByPostId(Moderation entity);
}
