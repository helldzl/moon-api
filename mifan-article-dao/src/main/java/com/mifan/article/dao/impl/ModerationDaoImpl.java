package com.mifan.article.dao.impl;

import com.mifan.article.dao.ModerationDao;
import com.mifan.article.domain.Moderation;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
@Repository
public class ModerationDaoImpl extends AbstractBaseDao<Moderation> implements ModerationDao {
    @Override
    public int updateByPostId(Moderation entity) {
        String updateByPostId = entityClass.getName() + "." + "updateByPostId";

        return session.update(updateByPostId, entity);
    }
}
