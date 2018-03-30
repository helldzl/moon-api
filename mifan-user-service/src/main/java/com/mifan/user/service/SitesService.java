package com.mifan.user.service;

import com.mifan.user.domain.Sites;
import org.moonframework.model.mybatis.service.BaseService;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/2
 */
public interface SitesService extends BaseService<Sites> {

    /**
     * <p>根据appKey查找siteId</p>
     *
     * @param appKey appKey
     * @return site id
     */
    Long findSiteIdByAppKey(String appKey);

}
