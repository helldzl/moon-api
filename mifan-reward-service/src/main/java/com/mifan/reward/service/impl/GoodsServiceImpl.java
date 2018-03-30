package com.mifan.reward.service.impl;

import com.mifan.reward.dao.GoodsDao;
import com.mifan.reward.domain.*;
import com.mifan.reward.service.GoodsService;
import com.mifan.reward.service.enums.EntityState;
import com.mifan.reward.service.enums.PrizesState;
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
public class GoodsServiceImpl extends AbstractBaseService<Goods, GoodsDao> implements GoodsService {

    @Override
    public Goods queryForObject(Long id, Iterable<? extends Field> fields) {

        Goods good =  super.queryForObject(id, fields);

        if (good == null) {
            return null;
        }

        // good ID -> headImage
        List<Images> headImages = this.findHeadImages(good.getId());
        good.setHeadImages(headImages);
        // good ID -> contentImage
        List<Images> contentImages = this.findContentImages(good.getId());
        good.setContentImages(contentImages);


        good.setPrizeInfo(
                Services.findOne(
                        Prizes.class,
                        Restrictions.and(
                                Restrictions.eq(Prizes.GOOD_ID, good.getId()),
                                Restrictions.eq(Prizes.ENABLED, EntityState.ENABLED.getState()),
                                Restrictions.eq(Prizes.STATE, PrizesState.PRIZING.getState())
                        )
                )
        );
        return good;

    }

    @Override
    public Page<Goods> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Page<Goods> goodsPage = super.findAll(criterion, pageable, fields);

        if (!goodsPage.hasContent()) {
            return goodsPage;
        }

        // ID -> goods
        Map<Long, Goods> goodsMap = goodsPage.getContent().stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        Set<Long> goodsIds = goodsMap.keySet();

        // good ID -> headImage
        Map<Long, List<Images>> headImages = this.findHeadImages(goodsIds);
        // good ID -> contentImage
        Map<Long, List<Images>> contentImages = this.findContentImages(goodsIds);

        goodsMap.forEach((goodId,good) -> {
            good.setHeadImages(headImages.get(goodId));
            good.setContentImages(contentImages.get(goodId));
            good.setPrizeInfo(
                    Services.findOne(
                            Prizes.class,
                            Restrictions.and(
                                    Restrictions.eq(Prizes.GOOD_ID, good.getId()),
                                    Restrictions.eq(Prizes.ENABLED, EntityState.ENABLED.getState()),
                                    Restrictions.eq(Prizes.STATE, PrizesState.PRIZING.getState())
                            ),
                            null
                    )
            );
        });

        return goodsPage;

    }

    @Override
    public <S extends Goods> int save(S entity) {

        int n = super.save(entity);

        if(!CollectionUtils.isEmpty(entity.getHeadImages())) {
            GoodHeadImages goodHeadImage = new GoodHeadImages();
            goodHeadImage.setGoodId(entity.getId());
            goodHeadImage.setCreator(entity.getCreator());
            goodHeadImage.setModifier(entity.getModifier());

            entity.getHeadImages().forEach(
                    image -> {
                        Services.save(Images.class,image);
                        goodHeadImage.setImageId(image.getId());
                        Services.save(GoodHeadImages.class,goodHeadImage);
                    });
        }

        if(!CollectionUtils.isEmpty(entity.getContentImages())) {
            GoodContentImages goodContentImage = new GoodContentImages();
            goodContentImage.setGoodId(entity.getId());
            goodContentImage.setCreator(entity.getCreator());
            goodContentImage.setModifier(entity.getModifier());

            entity.getContentImages().forEach(
                    image -> {
                        Services.save(Images.class,image);
                        goodContentImage.setImageId(image.getId());
                        Services.save(GoodContentImages.class,goodContentImage);
                    });
        }
        return n;
    }

    @Override
    public <S extends Goods> int update(S entity) {
        int n =  super.update(entity);

        Goods findGoods = Services.queryForObject(Goods.class,entity.getId());

        if (findGoods != null) {

        }
        Map<Long, Map<String, Object>> data = new HashMap<>(16);
        for (Long imageId : entity.getHeadImages().stream().map(Images::getId).collect(Collectors.toSet())) {
            data.put(imageId, null);
        }

        Services.doToManyPost(Goods.class, entity.getId(), Goods.RELATIONSHIPS_HEAD_IMAGES, data, false);

        return n;
    }

    private List<Images> findContentImages(Long goodId) {
        Set<Long> set = Services.findAll(
                GoodContentImages.class,
                Restrictions.and(
                        Restrictions.eq(GoodContentImages.GOOD_ID, goodId),
                        Restrictions.eq(GoodContentImages.ENABLED, 1)),
                Fields.builder().add(GoodContentImages.IMAGE_ID).build())
                .stream()
                .map(GoodContentImages::getImageId)
                .collect(toSet());

        if (set.isEmpty()) {
            return Collections.emptyList();
        }

        return findImagesByIds(set);
    }

    private Map<Long, List<Images>> findContentImages(Set<Long> goodIds) {
        // good ID -> images IDs
        Map<Long, Set<Long>> mapping = Services.findAll(GoodContentImages.class,
                Restrictions.and(
                        Restrictions.in(GoodContentImages.GOOD_ID, goodIds.toArray()),
                        Restrictions.eq(GoodContentImages.ENABLED, 1)),
                Fields.builder()
                        .add(GoodContentImages.GOOD_ID)
                        .add(GoodContentImages.IMAGE_ID)
                        .build())
                .stream()
                .collect(Collectors.groupingBy(GoodContentImages::getGoodId, mapping(GoodContentImages::getImageId, toSet())));

        if (mapping.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, List<Images>> result = new HashMap<>(16);
        List<Images> images = findImagesByIds(mapping.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(toSet()));

        mapping.forEach(
                ((goodId, set) -> images.stream().filter(image -> set.contains(image.getId())).forEach(
                        image -> {
                            if (!result.containsKey(goodId)) {
                                result.put(goodId, new ArrayList<>());
                            }
                            result.get(goodId).add(image);
                        })
                )
        );
        return result;
    }

    private List<Images> findHeadImages(Long goodId) {
        Set<Long> set = Services.findAll(
                GoodHeadImages.class,
                Restrictions.and(
                        Restrictions.eq(GoodHeadImages.GOOD_ID, goodId),
                        Restrictions.eq(GoodHeadImages.ENABLED, 1)),
                Fields.builder().add(GoodHeadImages.IMAGE_ID).build())
                .stream()
                .map(GoodHeadImages::getImageId)
                .collect(toSet());

        if (set.isEmpty()) {
            return Collections.emptyList();
        }

        return findImagesByIds(set);
    }

    private Map<Long, List<Images>> findHeadImages(Set<Long> goodIds) {
        // good ID -> images IDs
        Map<Long, Set<Long>> mapping = Services.findAll(GoodHeadImages.class,
                Restrictions.and(
                        Restrictions.in(GoodHeadImages.GOOD_ID, goodIds.toArray()),
                        Restrictions.eq(GoodHeadImages.ENABLED, 1)),
                Fields.builder()
                        .add(GoodHeadImages.GOOD_ID)
                        .add(GoodHeadImages.IMAGE_ID)
                        .build())
                .stream()
                .collect(Collectors.groupingBy(GoodHeadImages::getGoodId, mapping(GoodHeadImages::getImageId, toSet())));

        if (mapping.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, List<Images>> result = new HashMap<>(16);
        List<Images> images = findImagesByIds(mapping.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(toSet()));

        mapping.forEach(
                ((goodId, set) -> images.stream().filter(image -> set.contains(image.getId())).forEach(
                        image -> {
                            if (!result.containsKey(goodId)) {
                                result.put(goodId, new ArrayList<>());
                            }
                            result.get(goodId).add(image);
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
}
