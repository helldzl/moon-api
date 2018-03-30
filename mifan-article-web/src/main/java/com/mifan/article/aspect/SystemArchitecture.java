package com.mifan.article.aspect;

import com.mifan.article.domain.Channels;
import com.mifan.article.domain.Topics;
import com.mifan.article.service.ChannelsService;
import com.mifan.article.service.TopicsModelService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

//import com.mifan.user.domain.UserProfiles;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/5/5
 */
@Aspect
@Component
public class SystemArchitecture {

    @Autowired
    private TopicsModelService topicsModelService;

    @Autowired
    private ChannelsService channelsService;

    @Pointcut("execution(* com.mifan.article.service.impl.TopicsServiceImpl.save(..))")
    public void saveTopics() {
    }

//    @Pointcut("execution(* com.mifan.user.service.impl.UserProfilesServiceImpl.update(..))")
//    public void updateUserProfiles() {
//    }

    /**
     * <p>用户更新如下字段时, 要同时更新一下channels的冗余字段</p>
     * <ol>
     * <li>昵称</li>
     * <li>头像</li>
     * </ol>
     *
     * @param pjp pjp
     * @return Object
     * @throws Throwable Throwable
     */
//    @Around("com.mifan.aspect.SystemArchitecture.updateUserProfiles()")
//    public Object doUpdateUserProfiles(ProceedingJoinPoint pjp) throws Throwable {
//        Object retVal = pjp.proceed();
//        Object arg = pjp.getArgs()[0];
//        if (arg instanceof UserProfiles && (Integer) retVal > 0) {
//            UserProfiles profile = (UserProfiles) arg;
//            Long id = profile.getId();
//            String nickname = profile.getNickname();
//            String userAvatar = profile.getUserAvatar();
//            String userSig = profile.getUserSig();
//            if (id != null && (nickname != null || userAvatar != null || userSig != null)) {
//                Channels channel = channelsService.findOne(Restrictions.and(
//                        Restrictions.eq(Channels.TARGET_ID, id),
//                        Restrictions.eq(Channels.CHANNEL_TYPE, Channels.ChannelType.USER.getIndex())),
//                        Fields.builder()
//                                .add(Channels.ID)
//                                .build());
//                if (channel != null) {
//                    channel.setChannelName(nickname);
//                    channel.setChannelImage(userAvatar);
//                    channel.setDescription(userSig);
//                    channelsService.update(channel);
//                }
//            }
//        }
//        return retVal;
//    }

    /**
     * <p>当保存或更新产品时, 发送数据让离线分析器分析</p>
     *
     * @param pjp pjp
     * @return Object
     * @throws Throwable Throwable
     */
    @Around("com.mifan.article.aspect.SystemArchitecture.saveTopics()")
    public Object doAnalyzeProduct(ProceedingJoinPoint pjp) throws Throwable {
        Object retVal = pjp.proceed();
        // after process
        Object arg = pjp.getArgs()[0];
        if (arg instanceof Topics) {
            Topics topic = ((Topics) arg);

            // 分类训练
            if (topic.getForumId() != null && topic.getId() != null) {
                CompletableFuture.runAsync(() -> topicsModelService.classification(topic.getForumId(), topic.getId()));
            }

            // 用户频道
            Long userId = topic.getCreator();
            if (userId != null && userId > 0L && !channelsService.exists(Restrictions.and(
                    Restrictions.eq(Channels.TARGET_ID, userId),
                    Restrictions.eq(Channels.CHANNEL_TYPE, Channels.ChannelType.USER.getIndex())))) {
                channelsService.channel(userId);
            }
        }
        return retVal;
    }

}
