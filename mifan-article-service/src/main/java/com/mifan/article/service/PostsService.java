package com.mifan.article.service;

import com.mifan.article.domain.Posts;

import java.util.List;

import org.moonframework.model.mybatis.service.BaseService;
import org.springframework.data.domain.Page;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public interface PostsService extends BaseService<Posts> {

    Page<Posts> findPageHumanTranslate(Posts entity, int page,int size);

    /**
     * 删除精翻
     * @param id
     * @return
     */
    int deleteHumanTranslate (Long id );

    /**
     * 设置enabled=1
     * @param id
     * @return
     */
    int enabled(Long id);
    /**
     * 根据topicId获取所有posts列表
     * @param topicId
     * @param enabled
     * @return
     */
    List<Posts> getAllPosts(Long topicId,Integer enabled);
}
