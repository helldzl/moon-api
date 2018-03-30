/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.service.impl.CommentConfServiceImpl
 *
 * @description:
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年5月22日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.support.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mifan.support.dao.CommentConfDao;
import com.mifan.support.domain.CommentConf;
import com.mifan.support.domain.CommentConfTag;
import com.mifan.support.domain.Tag;
import com.mifan.support.service.CommentConfService;
import com.mifan.support.service.CommentConfTagService;
import com.mifan.support.service.TagService;

/**
 * @author ZYW
 *
 */
@Service
public class CommentConfServiceImpl extends AbstractBaseService<CommentConf, CommentConfDao> implements CommentConfService {
    
    @Autowired
    private CommentConfTagService commentConfTagService;
    
    @Autowired
    private TagService tagService;
    
    @Override
    public int save(CommentConf commentConf){
        Date now = new Date();
        commentConf.setModified(now);
        commentConf.setCreated(now);
        isTrue(baseDao.save(commentConf) > 0,"Error.save.CommentConf");
        addCommentConfTags(commentConf);
        
        return 1;
    }
    
    @Override
    public int update(CommentConf commentConf){
        CommentConf conf = this.findOne(commentConf.getId());
        if (conf == null) {
            throw new IllegalStateException(getMessage("error.notFind"));
        }
        conf.setConfName(commentConf.getConfName());
        conf.setDescription(commentConf.getDescription());
        conf.setModifier(commentConf.getModifier());
        conf.setModified(new Date());
        
        CommentConfTag confTag = new CommentConfTag();
        confTag.setConfId(conf.getId());
        List<CommentConfTag> list = commentConfTagService.findAll(confTag);
        List<Long> confTagList = new ArrayList<Long>();
        if(list != null && list.size() > 0){
            list.forEach((cf) -> confTagList.add(cf.getId()));
            isTrue(commentConfTagService.delete(confTagList).length > 0,"Error.delete.CommentConfTag");
        }
        
        addCommentConfTags(commentConf);
        
        isTrue(baseDao.update(conf) > 0,"Error.update.CommentConf");
        return 1;
    }
    
    private void addCommentConfTags(CommentConf commentConf){
        if(commentConf.getTagIds() != null && commentConf.getTagIds().size() > 0){
            List<CommentConfTag> confTags = new ArrayList<CommentConfTag>();
            for(Long tagId : commentConf.getTagIds()){
                CommentConfTag confTag = new CommentConfTag();
                confTag.setConfId(commentConf.getId());
                confTag.setTagId(tagId);
                confTags.add(confTag);
            }
            isTrue(commentConfTagService.save(confTags).length > 0,"Error.save.CommentConfTags");
        }
    }

    @Override
    public CommentConf details(Long id) {
        Fields.FieldBuilder builderConf = Fields.builder();
        builderConf.add(CommentConf.ID);
        builderConf.add(CommentConf.CONF_NAME);
        CommentConf conf = this.findOne(id, builderConf.build());
        
        if(conf == null || conf.getId() == null){
            return null;
        }
        
        CommentConfTag confTag = new CommentConfTag();
        confTag.setConfId(id);
        List<CommentConfTag> list = commentConfTagService.findAll(confTag);
        if(list.size() > 0){
            Long[] tagIds = new Long[list.size()];
            for(int i = 0; i < list.size();i ++){
                tagIds[i] = list.get(i).getTagId();
            }
            Criterion criterion = Restrictions.in(Tag.ID, tagIds);
            criterion = Restrictions.and(criterion,Restrictions.eq(Tag.ENABLED, 1));
            
            
            Fields.FieldBuilder builderTag = Fields.builder();
            builderTag.add(Tag.ID);
            builderTag.add(Tag.TAG_NAME);
            List<Tag> tags = tagService.findAll(criterion, builderTag.build());
            conf.setTags(tags);
        }
        return conf;
    }
}
