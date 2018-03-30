package com.mifan.article.service.amqp;

import com.mifan.article.domain.*;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.core.amqp.Message;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/14
 */
@Service
public class MessageImpl implements Message {

    protected static Log logger = LogFactory.getLog(MessageImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> sendAndReceive(Map<String, String> meta, Map<String, Object> data, Map<String, Set<Long>> relationships) {
        try {
            String className = meta.get("className");
            Object target = Class.forName(className).newInstance();
            String id = meta.get("id");
            if (target.getClass() == Seeds.class) {
                if (id != null) {
                    return BeanUtils.toMap(Services.findOne(Seeds.class, Long.valueOf(id)), true);
                } else {
                    String msg = "DataPersistence warn, SeedID is null : " + target;
                    if (logger.isErrorEnabled()) {
                        logger.error(msg);
                    }

                    return result("error", msg);
                }
            }


            if (!(target instanceof BaseEntity)) {
                String msg = "DataPersistence warn, Class type not BaseEntity : " + target;
                if (logger.isErrorEnabled()) {
                    logger.error(msg);
                }

                return result("error", msg);
            }

            String origin = meta.get("origin");
            if (target instanceof Attachments) {
                //这个里面主要区分是否是图片重下载
                if (meta.get("pictureAgain") == null) {
                    // [fetch]
                    AttachmentsFetch from = new AttachmentsFetch();
                    from.setOrigin(origin);

                    // [attachments]
                    Attachments attachment = bind((Attachments) target, data);
                    attachment.setFrom(from);

                    save(Attachments.class, attachment, null);
                    return BeanUtils.toMap(Services.findOne(Attachments.class, attachment.getId(), Fields.builder()
                            .add(Attachments.ID)
                            .add(Attachments.FILENAME)
                            .add(Attachments.RETRY)
                            .add(Attachments.ENABLED)
                            .build()));
                } else {
                    if (meta.get("enabled") != null && meta.get("retry") != null) {
                        Page<Attachments> page = Services.findAll(Attachments.class,
                                Restrictions.and(
                                        Restrictions.eq(Attachments.ENABLED, meta.get("enabled")),
                                        Restrictions.lt(Attachments.RETRY, meta.get("retry")),
                                        Restrictions.gt(Attachments.ID, id)
                                ),
                                Pages.builder().page(1).size(100).build(),
                                false);
                        return BeanUtils.toMap(page.getContent(), true);
                    }
                    String msg = "DataPersistence warn, PictureAgain is null : " + target;
                    if (logger.isErrorEnabled()) {
                        logger.error(msg);
                    }

                    return result("error", msg);
                }
            } else {
                Long seedId = Long.valueOf(data.get("seedId").toString());
                Seeds seed = Services.findOne(Seeds.class, seedId);
                if (seed == null) {
                    throw new IllegalArgumentException(String.format("Unknown seed ID : %s", seedId));
                }

                // [fetch]
                TopicsFetch from = new TopicsFetch();
                from.setOrigin(origin);
                from.setSeedId(seedId);
                if (data.get("reviews")!=null){
                    from.setReviews(Integer.parseInt(data.get("reviews").toString()));
                }

                // [posts]
                PostsText text = bind(new PostsText(), data);
                Posts post = bind(new Posts(), data);
                // TODO categories
                post.setPostsText(text);
                post.setLanguage(seed.getLanguage());

                // [topics]
                Topics topic = bind(new Topics(), data);
                topic.setReviews(null);
                topic.setReplies(null);
                topic.setSeedId(null);
                topic.setThumbsUp(null);
                topic.setFrom(from);
                topic.setPost(post);

                // [attachments]
                if (!CollectionUtils.isEmpty(relationships)) {
                    List<Attachments> attachments = new ArrayList<>();
                    topic.setAttachments(attachments);
                    relationships.forEach((relationship, set) -> {
                        if (!CollectionUtils.isEmpty(set)) {
                            attachments.addAll(set.stream().map(Attachments::new).collect(Collectors.toList()));
                        }
                    });
                }

                if (topic.getAttachments() == null) {
                    topic.setAttachments(new ArrayList<>());
                }

                // [others]
                if (target instanceof TopicsProduct) {
                    topic.setProduct(bind((TopicsProduct) target, data));
                } else if (target instanceof TopicsDocument) {
                    topic.setDocument(bind((TopicsDocument) target, data));
                } else if (target instanceof TopicsBrand) {
                    topic.setTopicsBrand(bind((TopicsBrand) target, data));
                }

                save(Topics.class, topic, null);
                Map<String, Object> result = new HashMap<>(16);
                result.put(BaseEntity.ID, topic.getId());
                return result;
            }
        } catch (Exception e) {
            logger.error("error", e);
            e.printStackTrace();
            return result("error", e.getMessage());
        }
    }

    private Map<String, Object> result(String key, Object value) {
        Map<String, Object> result = new HashMap<>(16);
        result.put(key, value);
        return result;
    }

    private <S extends BaseEntity> int save(Class<S> clazz, S entity, Map<String, Set<Long>> relationships) {
        int n = Services.save(clazz, entity);
        if (!CollectionUtils.isEmpty(relationships)) {
            Long id = entity.getId();
            relationships.forEach((relationship, set) -> {
                if (!CollectionUtils.isEmpty(set)) {
                    Map<Long, Map<String, Object>> data = new HashMap<>(16);
                    for (Long value : set) {
                        data.put(value, null);
                    }
                    Services.doToManyPatch(clazz, id, relationship, data, false);
                }
            });
        }
        return n;
    }

    private <T extends BaseEntity> T bind(T target, Map<String, Object> data) {
        BeanWrapper wrapper = new BeanWrapperImpl(target);
        wrapper.setPropertyValues(new MutablePropertyValues(data), true);
        return target;
    }

}