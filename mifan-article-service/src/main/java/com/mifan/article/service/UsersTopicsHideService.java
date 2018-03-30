package com.mifan.article.service;

import com.mifan.article.domain.UsersTopicsHide;
import org.moonframework.model.mybatis.service.BaseService;

import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public interface UsersTopicsHideService extends BaseService<UsersTopicsHide> {

    /**
     * <p>根据用户ID查找用户隐藏的主题ID集合</p>
     *
     * @param userId user ID
     * @return hided topics IDs
     */
    Set<String> findByUserId(Long userId);

}
