/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.service.impl.CommentServiceimpl
 * @description:
 * @version:v0.0.1
 * @author:ZYW Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年5月22日 ZYW v0.0.1 create
 */
package com.mifan.support.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.web.feign.Feigns;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mifan.support.dao.CommentDao;
import com.mifan.support.domain.Comment;
import com.mifan.support.domain.CommentSummary;
import com.mifan.support.domain.Tag;
import com.mifan.support.domain.support.TagSummary;
import com.mifan.support.domain.support.UserProfiles;
import com.mifan.support.feign.user.UserProfilesClient;
import com.mifan.support.service.CommentService;
import com.mifan.support.service.CommentSummaryService;
import com.mifan.support.service.TagService;
import com.mifan.support.util.SensitivewordFilter;

/**
 * @author ZYW
 */
@Service
public class CommentServiceImpl extends AbstractBaseService<Comment, CommentDao> implements CommentService {

    private static final String COMMENT_PRAISE_COUNT = "comment_praise_count";

    private static final String COMMENT_COUNT = "comment_count";

    @Autowired
    private SensitivewordFilter filter;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentSummaryService commentSummaryService;

    /*@Autowired
    private TopicsClusteringService topicsClusteringService;*/

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserProfilesClient userProfilesClient;


    @Override
    public int save(Comment comment) {//TODO 当类型为回复时，是否需要判断topId标识的comment与当前comment的conf_id和theme_id是否一致，以及replayId标识的comment与当前comment的topId是否一致
        if (comment.getTopId() != 0) {//当topId不等于0时，这条评论就是回复，topId一定是一个comment标识，replayId一定是一个comment标识
            isTrue(this.exists(comment.getTopId()), "Error.Comment.topId");
//            isTrue(comment.getReUserId() != null,"NotNull.Comment.ReUserId");//当被回复评论匿名时，re_user_id是空的
            isTrue(comment.getReplayId() != null, "NotNull.Comment.replayId");
            isTrue(this.exists(comment.getReplayId()), "Error.Comment.replayId");
            isTrue(comment.getTagIds() == null, "MustNull.Comment.tagIds");//当类型为回复时则不能包含标签
        } else {
            isTrue(comment.getReplayId() == null, "MustNull.Comment.replayId");
            isTrue(comment.getReUserId() == null, "MustNull.Comment.reUserId");
        }
        if (comment.getIsAnonymous() != 1) {
            comment.setIsAnonymous(0);
        }
        comment.setEnabled(1);
        comment.setCreated(new Date());
        Set<String> set = filter.getSensitiveWord(comment.getContent(), 1);
        if (set.size() > 0) {
            comment.setState(1);
            for (String str : set) {
                logger.info(str);
            }
        } else {
            comment.setState(0);
        }
        isTrue(baseDao.save(comment) > 0, "Error.Comment.save");

        if (comment.getState() == 0) {//当该评论的状态为正常时才更新回复数
            updateCommentCount(comment, 1);//更新回复数
        }

        tagService.saveTags(comment);//保存标签，并统计数
        return 1;
    }

    @Override
    public int remove(Long id) {
        Comment comment = this.findOne(id);
        if (comment == null || comment.getEnabled() == 0) {
            return 0;
        }
        comment.setEnabled(0);
        int index = this.update(comment);
        if (index > 0) {
            if (comment.getState() == 0) {
                updateCommentCount(comment, -1);
            }
        }
        return index;
    }

    @Override
    public Comment queryForObject(Long id, Iterable<? extends Field> fields) {
        notNull(id);
        Comment comment = baseDao.queryForObject(id, fields);
        if (comment == null) {
            return comment;
        }
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        if (comment.getTopId() == 0) {
            long relyCount = getReplyCount(comment.getConfId(), comment.getThemeId(), comment.getId(), ops);//获取回复数
            comment.setReplayCount(relyCount);
        }

        long praiseCount = updatePraiseCountRedis2(comment.getConfId(), comment.getThemeId(), comment.getId(), 1, ops);//获取点赞数
        comment.setPraiseCount(praiseCount);
        long _praiseCount = updatePraiseCountRedis2(comment.getConfId(), comment.getThemeId(), comment.getId(), -1, ops);//获取踩数
        comment.set_praiseCount(_praiseCount);

        if (comment.getIsAnonymous() == 0) {//获取评论者信息
            // UserProfiles creatorPro = userProfilesService.findOne(comment.getCreator(), fieldsUserProfile());
            // REMOTE CALL
            UserProfiles profile = profiles(comment.getCreator());
            if (profile != null) {
                comment.setNickName(profile.getNickname());
                comment.setUserAvatar(profile.getUserAvatar());
            }
        } else {
            comment.setCreator(null);
        }
        if (comment.getReplayId() != null) {//获取被回复的comment信息，如果是非匿名则获取该comment的创建者信息
            Comment reComment = this.findOne(comment.getReplayId(), fieldsComment());
            if (reComment != null) {
                if (reComment.getIsAnonymous() == 0) {
                    // UserProfiles replyUserPro = userProfilesService.findOne(reComment.getCreator(), fieldsUserProfile());
                    // REMOTE CALL
                    UserProfiles profile = profiles(comment.getCreator());
                    comment.setReUserNickName(profile.getNickname());
                } else {
                    comment.setReUserId(null);
                }
            }
        }

        return comment;
    }

    private long getReplyCount(Long confId, Long themeId, Long commentId, ValueOperations<String, String> ops) {//获取回复数
        String replyCountStr = ops.get(getCommentCount(confId, themeId, commentId));
        long replyCount = 0;
        if (replyCountStr != null) {
            replyCount = Long.parseLong(replyCountStr);
        } else {
            CommentSummary summary = new CommentSummary();
            summary.setConfId(confId);
            summary.setThemeId(themeId);
            summary.setCommentId(commentId);
            summary.setType(2);
            summary.setTagId(0L);
            summary = commentSummaryService.findOne(summary);
            if (summary != null) {
                replyCount = summary.getSum();
                ops.set(getCommentCount(confId, themeId, commentId), String.valueOf(replyCount));
            } else {
                replyCount = 0;
            }
        }

        return replyCount;
    }

    @Override
    public List<Comment> findHotComments(Long currentUserId, Long confId, long[] themeIds) {//获取热门评论
//        long[] themeIds = getThemeIds(confId, themeId);
        List<Comment> comments = baseDao.findHot(currentUserId, themeIds, confId);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        for (Comment comment : comments) {
            if (comment.getTopId() == 0) {
                long replayCount = getReplyCount(comment.getConfId(), comment.getThemeId(), comment.getId(), ops);
                comment.setReplayCount(replayCount);
            }
        }
        setRelevantInfo(comments, true);
        return comments;
    }

    /*private long[] getThemeIds(Long confId, Long themeId) {
        long[] themeIds = null;//topic有可能重复合并，所以在这里合并的topic的评论也要合并的查出来
        if (confId == 1) {
        	//targetId=0是否存在，是区分该主题是否被人工干预过的标志
            TopicsClustering clus = new TopicsClustering();
            clus.setTopicId(themeId);
            clus.setType(TopicsClustering.ClusteringType.PEOPLE.getIndex());
            clus.setTargetId(0l);
            Criterion criterion = Restrictions.eq(TopicsClustering.TOPIC_ID, themeId);
            if (topicsClusteringService.exists(clus)) {//如果被干预过，则机器的不再算数
                criterion = Restrictions.and(Restrictions.eq(TopicsClustering.TYPE, TopicsClustering.ClusteringType.PEOPLE.getIndex()),
                        Restrictions.eq(TopicsClustering.ENABLED, 1),
                        Restrictions.ne(TopicsClustering.TARGET_ID, 0));
            }
            List<TopicsClustering> topicsClusterings = topicsClusteringService.findAll(criterion);

            themeIds = new long[topicsClusterings.size() + 1];
            for (int i = 0; i < topicsClusterings.size(); i++) {
                themeIds[i] = topicsClusterings.get(i).getTopicId();
            }
            int last = topicsClusterings.size();
            themeIds[last] = themeId;
        } else {
            themeIds = new long[1];
            themeIds[0] = themeId;
        }
        return themeIds;
    }
*/
    @Override
    public Page<Comment> doGetPage(Long currentUserId, Long confId, long[] themeIds, Long topId, int page, int size) {

//        long[] themeIds = getThemeIds(confId, themeId);//topic有可能重复合并，所以在这里合并的topic的评论也要合并的查出来

        PageRequest pageRequest = Pages.builder().page(page).size(size).sort(Pages.sortBuilder().add(Comment.CREATED, false).build()).build();
        Comment entity = new Comment(confId, topId);
        Page<Comment> result = baseDao.findPage(currentUserId, themeIds, entity, pageRequest);
        List<Comment> list = result.getContent();
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        if (topId == 0) {
            for (Comment comment : list) {//获取每个一级评论的回复数
                long replayCount = getReplyCount(comment.getConfId(), comment.getThemeId(), comment.getId(), ops);
                comment.setReplayCount(replayCount);
                List<Comment> hotComments = baseDao.findHotRe(currentUserId, comment.getId());//查询热门的两条二级评论
                setRelevantInfo(hotComments, false);
                comment.setHotComments(hotComments);
            }
        }
        setRelevantInfo(list, false);
        return result;
    }

    /**
     * @param list
     * @param type false：set被回复用户信息的时候过滤掉一级二级评论，true：只过滤一级评论
     */
    private void setRelevantInfo(List<Comment> list, boolean type) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        Set<Long> userIdList = new HashSet<Long>();
        Set<Long> replayIds = new HashSet<Long>();
        for (Comment comment : list) {//获取赞踩数
            long praiseCount = updatePraiseCountRedis2(comment.getConfId(), comment.getThemeId(), comment.getId(), 1, ops);//获取点赞数
            comment.setPraiseCount(praiseCount);
            long _praiseCount = updatePraiseCountRedis2(comment.getConfId(), comment.getThemeId(), comment.getId(), -1, ops);//获取踩数
            comment.set_praiseCount(_praiseCount);

            userIdList.add(comment.getCreator());

            if (type) {
                if (comment.getReplayId() != null) {
                    replayIds.add(comment.getReplayId());
                } else {
                    comment.setReUserId(null);
                }
            } else {
                if (comment.getReplayId() != null && (!comment.getReplayId().equals(comment.getTopId()))) {
                    replayIds.add(comment.getReplayId());
                } else {//一级评论和二级评论排除在外，因为不需要被他们回复的用户信息
                    comment.setReUserId(null);
                }
            }

        }
        if (userIdList.size() > 0) {
            // List<UserProfiles> userProfiles = userProfilesService.findAll(userIdList, fieldsUserProfile());
            List<UserProfiles> userProfiles = profiles(userIdList);
            for (Comment comment : list) {//为每个评论添加用户信息
                for (UserProfiles userPro : userProfiles) {
                    if (comment.getIsAnonymous() == 0) {
                        if (userPro.getId().equals(comment.getCreator())) {
                            comment.setNickName(userPro.getNickname());
                            comment.setUserAvatar(userPro.getUserAvatar());
                            break;
                        }
                    } else {
                        comment.setCreator(null);
                        break;
                    }
                }

            }
        }
        if (replayIds.size() > 0) {//为每个二级以下的评论添加被回复者信息  //因为reUserId不存在的comment不一定是匿名的，所以要根据查出来的被回复评论获取不匿名的用户id
            userIdList.clear();
            List<Comment> reCommentList = this.findAll(replayIds, fieldsComment());
            for (Comment comment : list) {
                for (Comment re : reCommentList) {
                    if (re.getId().equals(comment.getReplayId())) {
                        if (re.getIsAnonymous() == 0) {
                            comment.setReUserId(re.getCreator());
                            userIdList.add(re.getCreator());
                            break;
                        } else {
                            comment.setReUserId(null);
                            break;
                        }
                    }
                }

            }
            if (userIdList.size() > 0) {
                // List<UserProfiles> userProfiles = userProfilesService.findAll(userIdList, fieldsUserProfile());
                List<UserProfiles> userProfiles = profiles(userIdList);
                for (Comment comment : list) {
                    for (UserProfiles userPro : userProfiles) {
                        if (comment.getReUserId() != null) {
                            if (comment.getReUserId().equals(userPro.getId())) {
                                comment.setReUserNickName(userPro.getNickname());
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    private long updatePraiseCountRedis2(Long confId, Long themeId, Long commentId, int score, ValueOperations<String, String> ops) {
        String key = getPraiseCount(confId, themeId, commentId, score);
        String praiseCountStr = ops.get(key);//用来查询点赞的计数
        long sum = 0L;
        if (praiseCountStr == null) {
            int type = score == 1 ? 0 : 1;
            CommentSummary summary = new CommentSummary();
            summary.setConfId(confId);
            summary.setThemeId(themeId);
            summary.setCommentId(commentId);
            summary.setType(type);
            summary.setTagId(0L);
            summary = commentSummaryService.findOne(summary);

            if (summary != null) {
                sum = summary.getSum();
                this.redisTemplate.opsForValue().set(key, String.valueOf(sum));
            }
        } else {
            sum = Long.parseLong(praiseCountStr);
        }
        return sum;
    }

    private List<Field> fieldsComment() {
        Fields.FieldBuilder builder = Fields.builder();
        builder.add(Comment.ID);
        builder.add(Comment.CREATED);
        builder.add(Comment.THEME_ID);
        builder.add(Comment.CONF_ID);
        builder.add(Comment.TOP_ID);
        builder.add(Comment.CONTENT);
        builder.add(Comment.RE_USER_ID);
        builder.add(Comment.REPLAY_ID);
        builder.add(Comment.IS_ANONYMOUS);
        builder.add(Comment.CREATOR);
        return builder.build();
    }

    @Override
    public Map<String, Object> themeInfoCount(Long confId, Long themeId) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        long themeCommentCount = getReplyCount(confId, themeId, 0L, ops);//获取回复数
        map.put("theme_comment_count", themeCommentCount);

        long praiseCount = updatePraiseCountRedis2(confId, themeId, 0L, 1, ops);
        map.put("praise_count", praiseCount);//获取赞数
        long _praiseCount = updatePraiseCountRedis2(confId, themeId, 0L, -1, ops);//获取踩数
        map.put("_praise_count", _praiseCount);

        CommentSummary summary = new CommentSummary();
        summary.setConfId(confId);
        summary.setThemeId(themeId);
        summary.setCommentId(0L);
        summary.setType(3);
        List<CommentSummary> list = commentSummaryService.findAll(summary);//获取标签的统计
        if (list.size() > 0) {
            List<TagSummary> tagSummarys = new ArrayList<TagSummary>();
            for (CommentSummary _summary : list) {
                Tag tag = tagService.findOne(_summary.getTagId());
                TagSummary tagSummary = new TagSummary();
                tagSummary.setTagId(_summary.getTagId());
                tagSummary.setSum(_summary.getSum());
                tagSummary.setTagName(tag.getTagName());
                tagSummarys.add(tagSummary);
            }
            map.put("tag_summarys", tagSummarys);
        }
        return map;
    }

    private void updateCommentCount(Comment comment, int sum) {//保存comment计数
        CommentSummary summary = new CommentSummary();
        summary.setConfId(comment.getConfId());
        summary.setThemeId(comment.getThemeId());
        summary.setCommentId(comment.getTopId());
        summary.setType(2);
        summary.setTagId(0L);
        summary.setSum(sum);
        int index = commentSummaryService.saveOrUpdate(summary);
        isTrue(index > 0, "Error.saveOrUpdate.summry");

        summary = commentSummaryService.findOne(summary.getId());

        comment.setReplayCount(Long.valueOf(summary.getSum()));//返回给调用方回复数
        // TODO 临时处理
        /*if (summary.getConfId() == 1L) {
            Topics topic = new Topics(comment.getThemeId());
            topic.setReplies(summary.getSum());
            Services.update(Topics.class, topic);
        }*/
        // TODO 临时处理

        long commentCount = this.redisTemplate.opsForValue().increment(getCommentCount(comment.getConfId(), comment.getThemeId(), comment.getTopId()), sum);

        int _sum = summary.getSum();
        if (_sum > 1 && commentCount == 1) {//当redis是第一次保存该记录或者redis数据丢失时，会触发这个检测
            commentCount = this.redisTemplate.opsForValue().increment(getCommentCount(comment.getConfId(), comment.getThemeId(), comment.getTopId()), _sum - 1);
        }
        if (_sum != commentCount) {//为可能发生的错误纠正
            this.redisTemplate.opsForValue().set(getCommentCount(comment.getConfId(), comment.getThemeId(), comment.getTopId()), String.valueOf(_sum));
        }
    }

    private String getPraiseCount(Long confId, Long themeId, Long commentId, int score) {
        return COMMENT_PRAISE_COUNT + "+score:" + score + "confId:" + confId + "themeId:" + themeId + "commentId:" + commentId;
    }

    private String getCommentCount(Long confId, Long themeId, Long topId) {
        return COMMENT_COUNT + "confId:" + confId + "themeId:" + themeId + "topId:" + topId;
    }

    /**
     * <p>REMOTE CALL</p>
     *
     * @param userId user ID
     * @return user profiles
     */
    private UserProfiles profiles(Long userId) {
    	Feigns.asRemote();
        ResponseEntity<Data<UserProfiles>> response = userProfilesClient.doGet(userId);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getData();
        }
        return null;
    }

    /**
     * <p>REMOTE CALL</p>
     *
     * @param collection collection
     * @return list
     */
    private List<UserProfiles> profiles(Collection<? extends Number> collection) {
        ResponseEntity<Data<List<UserProfiles>>> response = userProfilesClient.doGetPage(Feigns.params()
                .page(p -> p.page(1).size(100))
                .fields("user_profiles", UserProfiles.AVATAR_FIELDS)
                .filter(UserProfiles.ID, collection)
                .build());
        return response.getBody().getData();
    }
}
