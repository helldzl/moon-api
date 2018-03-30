package com.mifan.article.service.impl;

import com.mifan.article.dao.PostsTextDao;
import com.mifan.article.domain.PostsText;
import com.mifan.article.service.PostsTextService;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class PostsTextServiceImpl extends AbstractBaseService<PostsText, PostsTextDao> implements PostsTextService {

    @Override
    public <S extends PostsText> int save(S entity) {
        notNull(entity.getId());
        Integer index = 1;
        if (!index.equals(entity.getNoUpdateCategoryAndTagOfNull())) {//有时候category和tag为null的时候不应该改为空字符串
            if (entity.getCategory() == null) {
                entity.setCategory("");
            }
            if (entity.getTag() == null) {
                entity.setTag("");
            }
        }
        return super.saveOrUpdate(entity);
    }

}