package com.mifan.user.service.impl;

import com.mifan.user.dao.SitesDao;
import com.mifan.user.domain.Sites;
import com.mifan.user.service.SitesService;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/2
 */
@Service
public class SitesServiceImpl extends AbstractBaseService<Sites, SitesDao> implements SitesService {

    /**
     * <p>添加站点</p>
     * <p>判断appKey, 如果没有自动生成</p>
     *
     * @param entity entity
     * @param <S>    sub class
     * @return 0:ERROR 1:SUCCESS
     */
    @Override
    public <S extends Sites> int save(S entity) {
        init(entity);
        Date date = new Date();
        entity.setCreated(date);
        entity.setModified(date);
        return baseDao.save(entity);
    }


    /**
     * <p>修改站点</p>
     *
     * @param entity entity
     * @param <S>    sub class
     * @return 0:ERROR 1:SUCCESS
     */
    @Override
    public <S extends Sites> int update(S entity) {
        init(entity);
        Date date = new Date();
        entity.setModified(date);
        return baseDao.update(entity);
    }

    /**
     * <p>根据appKey查找siteId</p>
     *
     * @param appKey appKey
     * @return site id
     */
    @Override
    public Long findSiteIdByAppKey(String appKey) {
        Sites site = new Sites();
        site.setEnabled(1);
        site.setAppKey(appKey);
        site = baseDao.findOne(site, Fields.builder().add(Sites.ID).build());
        return site == null ? null : site.getId();
    }

    /**
     * <p>更新KEY</p>
     *
     * @param entity entity
     * @param <S>    sub class
     */
    private <S extends Sites> void init(S entity) {
        String appKey = entity.getAppKey();
        if (isEmpty(appKey)) {
            entity.setAppKey(UUID.randomUUID().toString());
            entity.setAppSecret(UUID.randomUUID().toString());
        }
    }
}
