package com.mifan.article.service.impl;

import com.mifan.article.dao.ChannelsDao;
import com.mifan.article.domain.*;
import com.mifan.article.domain.support.UserProfiles;
import com.mifan.article.feign.user.UserProfilesClient;
import com.mifan.article.service.ChannelsService;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.codec.digest.DigestUtils;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.*;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.model.mybatis.support.Association;
import org.moonframework.web.feign.Feigns;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class ChannelsServiceImpl extends AbstractBaseService<Channels, ChannelsDao> implements ChannelsService {

    /**
     * 用户微服务API
     */
    @Autowired
    private UserProfilesClient userProfilesClient;

    @Cacheable(cacheManager = "ehCacheCacheManager", cacheNames = "article:channels", key = "#id")
    @Override
    public Channels findOne(Long id) {
        Channels channel = super.findOne(id, Channels.DEFAULT_FIELDS);
        if (channel != null && channel.getChannelType() == Channels.ChannelType.FROM.getIndex()) {
            List<Long> list = Services.findAll(ChannelsSeeds.class,
                    Restrictions.and(
                            Restrictions.eq(ChannelsSeeds.CHANNEL_ID, channel.getId()),
                            Restrictions.eq(ChannelsSeeds.ENABLED, 1)),
                    Fields.builder()
                            .add(ChannelsSeeds.SEED_ID)
                            .build())
                    .stream()
                    .map(ChannelsSeeds::getSeedId)
                    .collect(Collectors.toList());
            channel.setTargets(list);
        }
        return channel;
    }

    @Override
    public List<Seeds> findSeedsByChannel(Long channelId) {
        ChannelsSeeds find = new ChannelsSeeds();
        find.setChannelId(channelId);
        List<Long> seedIds = Services.findAll(ChannelsSeeds.class, find, Fields.builder().add(ChannelsSeeds.SEED_ID).build())
                .stream().map(ChannelsSeeds::getSeedId).collect(Collectors.toList());

        List<Seeds> seeds = new ArrayList<>();
        if (seedIds.size() > 0) {
            Long[] ids = new Long[seedIds.size()];
            seedIds.toArray(ids);
            seeds = Services.findAll(Seeds.class, Restrictions.in(Seeds.ID, ids), Seeds.DEFAULT_FIELDS);
        }
        return seeds;
    }

    @Override
    public int add(Channels entity) {
        if (entity.getChannelType() == Channels.ChannelType.FROM.getIndex() && Collections.isEmpty(entity.getTargets()) ||
                entity.getChannelType() != Channels.ChannelType.FROM.getIndex() && entity.getTargetId() == null) {
            throw new IllegalArgumentException("NotNull.Channels.targetId");
        }
        int n = 0;
        if (entity.getChannelType() == Channels.ChannelType.LABEL.getIndex()) {
            ElasticQueryBuilder eqb = new ElasticQueryBuilder(entity.getTargetId());
            n = Services.update(ElasticQueryBuilder.class, eqb);//加锁，同时验证是否存在
            if (n > 0) {
                if (entity.getId() == null) {
                    n += super.save(entity);
                } else {
                    n += super.update(entity);
                }
            } else {
                throw new IllegalArgumentException("Err.Channels.targetId");
            }
        } else if (entity.getChannelType() == Channels.ChannelType.USER.getIndex()) {
            if (entity.getId() == null) {

                channel(entity.getTargetId());

//                UserProfiles profile = new UserProfiles(entity.getTargetId());
//                profile.setUserAllowPm(1);
//                n = Services.update(UserProfiles.class, profile, Restrictions.eq(UserProfiles.USER_ALLOW_PM, 0));//加锁，同时验证是否存在
//                if (n > 0) {
//                    try {
//                        n += super.save(entity);
//                    } catch (Exception e) {
//                        profile.setUserAllowPm(0);
//                        Services.update(UserProfiles.class, profile);
//                        throw new RuntimeException("创建频道失败");
//                    }
//                } else {
//                    throw new IllegalArgumentException("Err.Channels.targetId");
//                }
            } else {

                Channels channel = buildChannel(entity.getTargetId());
                if (channel != null) {
                    channel.setId(entity.getId());
                    super.update(channel);
                }

//                Channels channel = super.findOne(entity.getId());
//                if (channel == null || !channel.getTargetId().equals(entity.getTargetId())) {//用户频道的targetId不允许修改
//                    throw new RuntimeException("修改频道失败");
//                }
//
//                UserProfiles profile = new UserProfiles(entity.getTargetId());
//                n = Services.update(UserProfiles.class, profile, Restrictions.eq(UserProfiles.USER_ALLOW_PM, 1));//加锁，同时验证是否存在
//                if (n > 0) {
//                    try {
//                        n += super.update(entity);
//                    } catch (Exception e) {
//                        throw new RuntimeException("修改频道失败");
//                    }
//                }
            }
        } else if (entity.getChannelType() == Channels.ChannelType.FROM.getIndex()) {
            if (entity.getId() == null) {
                List<Seeds> seeds = entity.getTargets().stream().filter(s -> s != null).map(s -> new Seeds(s)).collect(Collectors.toList());
                n = Services.update(Seeds.class, seeds);//加锁，同时验证是否存在
                if (n != 0 && n == seeds.size()) {
                    n += super.save(entity);
                    List<ChannelsSeeds> list = seeds.stream().map(s -> new ChannelsSeeds(entity.getId(), s.getId())).collect(Collectors.toList());
                    Services.save(ChannelsSeeds.class, list);
                } else {
                    throw new IllegalArgumentException("Err.Channels.targetId");
                }
            } else {
                ChannelsSeeds cs = new ChannelsSeeds();
                cs.setChannelId(entity.getId());
                List<Long> old = Services.findAll(ChannelsSeeds.class, cs, Fields.builder().add(ChannelsSeeds.SEED_ID).build()).stream().map(ChannelsSeeds::getSeedId).collect(Collectors.toList());
                List<Long> add = new ArrayList<Long>(entity.getTargets());
                add.removeAll(old);
                List<Long> delete = new ArrayList<Long>(old);
                delete.removeAll(entity.getTargets());
                if (add.size() > 0) {
                    List<Seeds> seeds = add.stream().filter(s -> s != null).map(s -> new Seeds(s)).collect(Collectors.toList());
                    n = Services.update(Seeds.class, seeds);//加锁，同时验证是否存在
                    if (n == seeds.size()) {
                        List<ChannelsSeeds> addList = seeds.stream().map(s -> new ChannelsSeeds(entity.getId(), s.getId())).collect(Collectors.toList());
                        Services.save(ChannelsSeeds.class, addList);
                    } else {
                        throw new IllegalArgumentException("Err.Channels.targetId");
                    }
                }
                if (delete.size() > 0) {
                    Long[] seedsId = new Long[delete.size()];
                    delete.toArray(seedsId);
                    List<Long> deleteCS = Services.findAll(ChannelsSeeds.class,
                            Restrictions.and(Restrictions.eq(ChannelsSeeds.CHANNEL_ID, entity.getId()), Restrictions.in(ChannelsSeeds.SEED_ID, seedsId)),
                            Fields.builder().add(ChannelsSeeds.ID).build()).stream().map(ChannelsSeeds::getId).collect(Collectors.toList());
                    Services.delete(ChannelsSeeds.class, deleteCS);
                }
                super.update(entity);
            }
        } else {
            if (entity.getId() == null) {
                n += super.save(entity);
            } else {
                n += super.update(entity);
            }
        }
        return n;
    }

    @Override
    public Page<Channels> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        if (logger.isDebugEnabled()) {
            logger.debug(DigestUtils.md5Hex(criterion.toString() + ',' + pageable.toString()));
        }

        Include include = Restrictions.get(Include.class);
        boolean contains = include != null && include.contains(UsersChannelsWatch.TABLE_NAME);
        Page<Channels> page;
        if (contains) {
            page = super.findAll(criterion, pageable, Channels.JOIN_FIELDS);
        } else {
            page = super.findAll(criterion, pageable, Channels.DEFAULT_FIELDS, true, Association.DELAYED);
        }

        // [grouping]
        Map<Integer, List<Channels>> collect = page.getContent().stream().collect(groupingBy(Channels::getChannelType));

        // [seeds]
        List<Channels> channels = collect.get(Channels.ChannelType.FROM.getIndex());
        if (!CollectionUtils.isEmpty(channels)) {
            Map<Long, List<Long>> map = Services.findAll(ChannelsSeeds.class,
                    Restrictions.and(
                            Restrictions.in(ChannelsSeeds.CHANNEL_ID, channels.stream().map(BaseEntity::getId).collect(toSet()).toArray()),
                            Restrictions.eq(ChannelsSeeds.ENABLED, 1)),
                    Fields.builder()
                            .add(ChannelsSeeds.CHANNEL_ID)
                            .add(ChannelsSeeds.SEED_ID)
                            .build())
                    .stream()
                    .collect(Collectors.groupingBy(ChannelsSeeds::getChannelId,
                            Collectors.mapping(ChannelsSeeds::getSeedId, Collectors.toList())));

            for (Channels channel : channels) {
                List<Long> targets = map.get(channel.getId());
                if (!CollectionUtils.isEmpty(targets)) {
                    Seeds seed = Services.findOne(Seeds.class, targets.get(0));
                    if (seed != null) {
                        channel.setHost(seed.getHost());
                    }
                }
            }
        }

        return page;
    }

    @Override
    public Channels queryForObject(Long id, Iterable<? extends Field> fields) {
        return findOne(id);
    }

    /**
     * <p>使用非分布式事物机制重构, 修复了分布式事务下导致的数据不一致问题</p>
     *
     * @param userId user ID
     * @return channel ID
     */
    @Override
    public Long channel(Long userId) {
        // 根据用户ID查询频道ID
        Page<Channels> page = Services.findAll(Channels.class,
                Restrictions.and(
                        Restrictions.eq(Channels.TARGET_ID, userId),
                        Restrictions.eq(Channels.CHANNEL_TYPE, Channels.ChannelType.USER.getIndex())),
                Pages.builder().page(1).size(1).build(),
                Fields.builder().add(Channels.ID).add(Channels.ENABLED).build(),
                false);

        Channels c;
        if (!page.hasContent()) {
            if ((c = Services.lock(
                    Services.findOne(SystemResourceLock.class, Restrictions.and(Restrictions.eq(SystemResourceLock.RESOURCE_TYPE, SystemResourceLock.ResourceType.USERS.getIndex()), Restrictions.eq(SystemResourceLock.TARGET_ID, userId))),
                    () -> Services.save(SystemResourceLock.class, new SystemResourceLock(SystemResourceLock.ResourceType.USERS, userId)) > 0,
                    () -> {
                        // 只有在获取全局唯一锁的时候才执行该段代码
                        Channels channel = buildChannel(userId);
                        Services.save(Channels.class, channel);
                        return channel;
                    })) == null) {
                throw new IllegalStateException("系统繁忙, 请稍后再试");
            }
        } else if ((c = page.getContent().get(0)).getEnabled() == 0) {
            throw new RuntimeException("该用户频道已经被锁定, 禁止执行操作");
        }
        return c.getId();
    }

    private Channels buildChannel(Long userId) {
        ResponseEntity<Data<UserProfiles>> response = userProfilesClient.doGet(userId, Feigns.params().fields("user_profiles", f -> f.add(UserProfiles.NICKNAME).add(UserProfiles.USER_AVATAR).add(UserProfiles.USER_SIG)).build());
        if (response.getStatusCode().is2xxSuccessful()) {
            UserProfiles profile = response.getBody().getData();

            Channels channel = new Channels();
            channel.channelTypeValue(Channels.ChannelType.USER);
            channel.setTargetId(userId);

            if (profile != null) {
                channel.setChannelName(profile.getNickname());
                channel.setChannelImage(profile.getUserAvatar());
                channel.setDescription(profile.getUserSig());
            }

            channel.channelSourceValue(Channels.ChannelSource.INTERNAL);
            channel.setCreator(userId);
            channel.setModifier(userId);
            return channel;
        } else {
            throw new IllegalStateException();
        }

    }

}
