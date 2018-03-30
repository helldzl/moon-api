package com.mifan.reward.service.impl;

import com.mifan.reward.dao.SharesDao;
import com.mifan.reward.domain.Images;
import com.mifan.reward.domain.Prizes;
import com.mifan.reward.domain.ShareImages;
import com.mifan.reward.domain.Shares;
import com.mifan.reward.domain.support.RewardUsers;
import com.mifan.reward.domain.support.UserProfiles;
import com.mifan.reward.service.SharesService;
import com.mifan.reward.service.feign.FeignBaseServiceAdapter;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

/**
 * @author DXZ
 * @version 1.0
 * @since 2017-08-31
 */
@Service
public class SharesServiceImpl extends FeignBaseServiceAdapter<Shares, SharesDao> implements SharesService {
    @Override
    public Shares queryForObject(Long id, Iterable<? extends Field> fields) {
        Shares share =  super.queryForObject(id, fields);
        if (share == null) {
            return null;
        }
        // 图片信息
        List<Images> images = this.findImages(share.getId());
        share.setShareImages(images);
        // 奖品信息
        addPrizeInfo(share);
        // 用户信息
        addUserInfo(share);

        return share;

    }

    @Override
    public Page<Shares> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Page<Shares> sharesPage = super.findAll(criterion, pageable, fields);

        if (!sharesPage.hasContent()) {
            return sharesPage;
        }

        // ID -> shares
        Map<Long, Shares> sharesMap = sharesPage.getContent().stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        Set<Long> sharesIds = sharesMap.keySet();

        // share IDs -> sharesImages
        Map<Long, List<Images>> images = this.findImages(sharesIds);

        sharesMap.forEach((shareId, share) -> {
            // 图片信息
            share.setShareImages(images.get(shareId));
            // 奖品信息
            addPrizeInfo(share);
            // 用户信息
            addUserInfo(share);
        });

        return sharesPage;

    }

    @Override
    public <S extends Shares> int save(S entity) {
        entity.setCreator(entity.getUserId());
        entity.setModifier(entity.getUserId());
        int n = super.save(entity);

        if(!CollectionUtils.isEmpty(entity.getShareImages())) {
            ShareImages shareImage = new ShareImages();
            shareImage.setShareId(entity.getUserId());
            shareImage.setCreator(entity.getUserId());
            shareImage.setModifier(entity.getUserId());

            entity.getShareImages().forEach(
                    image -> {
                        Services.save(Images.class,image);
                        shareImage.setImageId(image.getId());
                        Services.save(ShareImages.class,shareImage);
                    });
        }
        return n;
    }

    private List<Images> findImages(Long shareId) {
        Set<Long> set = Services.findAll(
                ShareImages.class,
                Restrictions.and(
                        Restrictions.eq(ShareImages.SHARE_ID, shareId),
                        Restrictions.eq(ShareImages.ENABLED, 1)),
                Fields.builder().add(ShareImages.IMAGE_ID).build())
                .stream()
                .map(ShareImages::getImageId)
                .collect(toSet());

        if (set.isEmpty()) {
            return Collections.emptyList();
        }

        return findImagesByIds(set);
    }

    private Map<Long, List<Images>> findImages(Set<Long> shareIds) {
        // share ID -> images IDs
        Map<Long, Set<Long>> mapping = Services.findAll(ShareImages.class,
                Restrictions.and(
                        Restrictions.in(ShareImages.SHARE_ID, shareIds.toArray()),
                        Restrictions.eq(ShareImages.ENABLED, 1)),
                Fields.builder()
                        .add(ShareImages.SHARE_ID)
                        .add(ShareImages.IMAGE_ID)
                        .build())
                .stream()
                .collect(Collectors.groupingBy(ShareImages::getShareId, mapping(ShareImages::getImageId, toSet())));

        if (mapping.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, List<Images>> result = new HashMap<>(16);
        List<Images> images = findImagesByIds(mapping.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(toSet()));

        mapping.forEach(
                ((shareId, set) -> images.stream().filter(image -> set.contains(image.getId())).forEach(
                        image -> {
                            if (!result.containsKey(shareId)) {
                                result.put(shareId, new ArrayList<>());
                            }
                            result.get(shareId).add(image);
                        })
                )
        );
        return result;
    }

    private List<Images> findImagesByIds(Set<Long> ids) {
        return Services.findAll(
                Images.class,
                Restrictions.and(
                        Restrictions.in(Images.ID, ids.toArray()),
                        Restrictions.eq(Images.ENABLED, 1)),
                Images.DEFAULT_FIELDS);
    }

    private void addPrizeInfo(Shares share) {
        share.setPrizeInfo(Services.queryForObject(
                Prizes.class,
                share.getPrizeId(),
                null));
    }

    private void addUserInfo(Shares share) {
        share.setUserInfo(getUserInfo(share.getUserId()));
    }
}
