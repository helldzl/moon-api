package com.mifan.user.service;

import com.mifan.user.domain.UserProfiles;

import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/04/05
 */
public interface UserProfilesService extends BaseService<UserProfiles> {
    
    /**
     * 后台列表查询
     * @param criterion
     * @param pageable
     * @return
     */
    Page<UserProfiles> findAllAdmin(Criterion criterion, Pageable pageable);
    /**
     * 后台详情查询
     * @param id
     * @return
     */
    UserProfiles findOneAdmin(Long id);
}
