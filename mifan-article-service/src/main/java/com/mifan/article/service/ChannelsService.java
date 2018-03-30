package com.mifan.article.service;

import com.mifan.article.domain.Channels;
import com.mifan.article.domain.Seeds;
import org.moonframework.model.mybatis.service.BaseService;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public interface ChannelsService extends BaseService<Channels> {

    /**
     * <p>输入用户ID, 返回channel ID</p>
     *
     * @param userId user ID
     * @return channel ID
     */
    Long channel(Long userId);

    /**
     * 添加频道
     *
     * @param entity
     * @return
     */
    int add(Channels entity);

    /**
     * 查询来源频道下的seeds
     *
     * @param channelId
     * @return
     */
    List<Seeds> findSeedsByChannel(Long channelId);
}
