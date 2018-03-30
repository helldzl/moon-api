/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.service.impl.PraiseServiceImpl
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

import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.mifan.support.dao.PraiseDao;
import com.mifan.support.domain.CommentSummary;
import com.mifan.support.domain.Praise;
import com.mifan.support.service.CommentSummaryService;
import com.mifan.support.service.PraiseService;

/**
 * @author ZYW
 *
 */
@Service
public class PraiseServiceImpl extends AbstractBaseService<Praise, PraiseDao> implements PraiseService {
    
    private static final String COMMENT_PRAISE_COUNT = "comment_praise_count";
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private CommentSummaryService commentSummaryService;
    
    @Override
    public int save(Praise praise){//TODO 当前每次修改都会有count操作，以后看情况可以优化为，先判断redis中是否存在，如果存在则+1或者-1，如果不存在再使用count
        isTrue(praise.getScore() != 0,"Error.Praise.score");//score的值只能是1或者-1，hibernate校验包含了0，所以在这里做限制
        Praise _praise = new Praise();
        _praise.setConfId(praise.getConfId());
        _praise.setThemeId(praise.getThemeId());
        _praise.setCreator(praise.getCreator());
//        _praise.setType(praise.getType());
        if(praise.getType() == 1){//类型为对comment点赞时，comment_id不能为空
            isTrue(praise.getCommentId() != null,"NotNull.Praise.commentId");
            _praise.setCommentId(praise.getCommentId());
        }else{
            isTrue(praise.getCommentId() == null,"MustNull.Praise.commentId");
            praise.setCommentId(0L);
        }
        int sum = 1;
        _praise = this.findOne(_praise);
        if(_praise != null){
            _praise.setModified(praise.getModified());
            _praise.setModifier(praise.getModifier());
            if(praise.getScore().equals(_praise.getScore())){//当参数score和数据库中的score相等时，视为取消赞踩
                _praise.setScore(0);
                isTrue(baseDao.update(_praise) > 0,"Error.praise.update");
                sum = -1;//当取消赞或踩时，当前的score统计数应该-1
            }else{//当参数score和数据库中的score不相等且数据库中的score为0时，视为添加赞踩，当数据库中的score不为0时，视为修改
                if(_praise.getScore() != 0){
                    int score = praise.getScore() == -1 ? 1 : -1;//当修改操作且当前用户，当前的score的对立面的统计数会-1，所以需要更新统计表
                    Praise pra = (Praise) praise.clone();
                    pra.setScore(score);
                    updatePraiseCount(pra,-1);
                }
                _praise.setScore(praise.getScore());
                isTrue(baseDao.update(_praise) > 0,"Error.praise.update");
            }
            praise.setId(_praise.getId());//id通过praise对象返回给Controller，最终返回给前端
            
        }else{
            praise.setEnabled(1);
            isTrue(baseDao.save(praise) > 0,"Error.praise.save");
        }
        updatePraiseCount(praise,sum);
        
        return 1;
    }
    
    private void updatePraiseCount(Praise praise,int sum){//保存赞踩统计数
        int type = praise.getScore() == 1 ? 0 : 1;
        
        CommentSummary summary = new CommentSummary();
        summary.setConfId(praise.getConfId());
        summary.setThemeId(praise.getThemeId());
        long commentId = praise.getCommentId() == null ? 0 : praise.getCommentId();
        summary.setCommentId(commentId);
        summary.setType(type);
        summary.setTagId(0L);
        summary.setSum(sum);
        int index = commentSummaryService.saveOrUpdate(summary);
        isTrue(index > 0, "Error.saveOrUpdate.summry");
        
        summary = commentSummaryService.findOne(summary.getId());
        
        long praiseCount = this.redisTemplate.opsForValue().increment(getPraiseCount(praise), sum);
        
        int _sum = summary.getSum();
        if(_sum > 1 && praiseCount == 1){//当redis是第一次保存该记录或者redis数据丢失时，会触发这个检测
            praiseCount = this.redisTemplate.opsForValue().increment(getPraiseCount(praise), _sum - 1);
        }
        if(_sum != praiseCount){//为可能发生的错误纠正
            this.redisTemplate.opsForValue().set(getPraiseCount(praise), String.valueOf(_sum));
        }
    }
    
    private String getPraiseCount(Praise praise){
        long commentId = 0L;
        if(praise.getCommentId() != null){
            commentId = praise.getCommentId();
        }
        return COMMENT_PRAISE_COUNT + "+score:" + praise.getScore() + "confId:" + praise.getConfId() + "themeId:" + praise.getThemeId() + "commentId:" + commentId;
        /*if(praise.getType() == 0){
            return COMMENT_PRAISE_COUNT + "+score:" + praise.getScore() + "confId:" + praise.getConfId() + "themeId:" + praise.getThemeId();
        }else{
            return COMMENT_PRAISE_COUNT + "+score:" + praise.getScore() + "commentId:" + praise.getCommentId();
        }*/
    }
}
