package com.mifan.article.service.impl;

import com.mifan.article.dao.SeedsDao;
import com.mifan.article.domain.Channels;
import com.mifan.article.domain.ChannelsSeeds;
import com.mifan.article.domain.Seeds;
import com.mifan.article.service.SeedsService;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeedsManagerImpl extends AbstractBaseService<Seeds, SeedsDao> implements SeedsService {

    @Override
    public Seeds findOne(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Read [Seeds] from database, ID : " + id);
        }

        Seeds seed = super.findOne(id);
        if (seed == null) {
            return null;
        }

        URI uri = URI.create(seed.getUrl());
        seed.setHost(uri.getScheme() + "://" + uri.getHost());

        // [relationships]
        Set<Long> set = Services.findAll(ChannelsSeeds.class, Restrictions.and(
                Restrictions.eq(ChannelsSeeds.SEED_ID, id),
                Restrictions.eq(ChannelsSeeds.ENABLED, 1)),
                Fields.builder()
                        .add(ChannelsSeeds.ID)
                        .add(ChannelsSeeds.CHANNEL_ID)
                        .build())
                .stream()
                .map(ChannelsSeeds::getChannelId)
                .collect(Collectors.toSet());

        // [channels]
        if (!set.isEmpty()) {
            List<Channels> channels = Services.findAll(
                    Channels.class,
                    Restrictions.in(BaseEntity.ID, set.toArray()),
//                    Restrictions.and(
//                            Restrictions.in(BaseEntity.ID, set.toArray()),
//                            Restrictions.eq(BaseEntity.ENABLED, 1)),
                    Channels.DEFAULT_FIELDS);

            int size = channels.size();
//            if (size > 1) {
//                throw new IllegalStateException(String.format("Channel size except one, but found : %s, seed : %s", size, id));
//            } else if (size == 1) {
//                seed.setChannel(channels.get(0));
//            }
            if (size >= 1) {
                seed.setChannel(channels.get(0));
            }
        }

        return seed;
    }

    @Override
    public Page<Seeds> getQuestionTemplate(int page, int size) {
        Pageable pageable = Pages.builder().page(page).size(size).build();
        return baseDao.getQuestionTemplate(pageable, null);
    }

    @Override
    public Page<Seeds> getNotWholeTemplate(int page, int size) {
        Pageable pageable = Pages.builder().page(page).size(size).build();
        return baseDao.getNotWholeTemplate(pageable, null);
    }

}
