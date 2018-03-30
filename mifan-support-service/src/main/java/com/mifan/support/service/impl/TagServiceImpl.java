/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.service.impl.TagServiceImpl
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

import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mifan.support.dao.TagDao;
import com.mifan.support.domain.Comment;
import com.mifan.support.domain.CommentSummary;
import com.mifan.support.domain.CommentTag;
import com.mifan.support.domain.Tag;
import com.mifan.support.service.CommentSummaryService;
import com.mifan.support.service.CommentTagService;
import com.mifan.support.service.TagService;

/**
 * @author ZYW
 *
 */
@Service
public class TagServiceImpl extends AbstractBaseService<Tag, TagDao> implements TagService {
    
    @Autowired
    private CommentSummaryService commentSummaryService;
    
    @Autowired
    private CommentTagService commentTagService;

    @Override
    public int update(Tag tag){
        Tag _tag = this.findOne(tag.getId());
        if (_tag == null || _tag.getId() == null) {
            throw new IllegalStateException(getMessage("error.notFind"));
        }
        _tag.setTagName(tag.getTagName());
        _tag.setDescription(tag.getDescription());
        _tag.setModifier(tag.getModifier());
        _tag.setModified(new Date());
        
        if (baseDao.update(_tag) < 0) {
            throw new IllegalStateException(getMessage("error.update"));
        }
        return 1;
    }
    @Override
    public void saveTags(Comment comment) {
        Long[] tagIds = comment.getTagIds();
        if(tagIds != null && tagIds.length > 0){
            List<CommentTag> commentTags = new ArrayList<CommentTag>();
            for(Long tagId : tagIds){
                CommentSummary summary = new CommentSummary();
                summary.setConfId(comment.getConfId());
                summary.setThemeId(comment.getThemeId());
                summary.setCommentId(0L);
                summary.setType(3);
                summary.setTagId(tagId);
                summary.setSum(1);
                isTrue(commentSummaryService.saveOrUpdate(summary) > 0, "Error.saveOrUpdate.summry");
                
                CommentTag commentTag = new CommentTag();
                commentTag.setCommentId(comment.getId());
                commentTag.setTagId(tagId);
                commentTags.add(commentTag);
            }
            isTrue(commentTagService.save(commentTags).length > 0,"Error.Comment.save");
        }
    }
}
