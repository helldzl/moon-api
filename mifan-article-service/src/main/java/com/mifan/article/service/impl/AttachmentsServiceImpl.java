package com.mifan.article.service.impl;

import com.mifan.article.dao.AttachmentsDao;
import com.mifan.article.domain.Attachments;
import com.mifan.article.domain.AttachmentsFetch;
import com.mifan.article.domain.TopicsAttachments;
import com.mifan.article.service.AttachmentsService;
import com.mifan.article.service.RestService;
import com.mifan.article.service.attachment.AttachmentUploader;
import com.mifan.article.service.attachment.AttachmentUrl;
import com.mifan.article.service.attachment.DefaultAttachmentUrl;
import com.mifan.article.service.util.EntityUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moonframework.concurrent.util.LockUtils;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class AttachmentsServiceImpl extends AbstractBaseService<Attachments, AttachmentsDao> implements AttachmentsService {
    protected static Log logger = LogFactory.getLog(AttachmentsServiceImpl.class);

    @Value("${application.image.baseUrl}")
    private String baseUrl;

    @Autowired
    private AttachmentUploader uploader;

    @Autowired
    private RestService restService;

    private AttachmentUrl attachmentUrl = new DefaultAttachmentUrl();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Transactional(propagation = NOT_SUPPORTED)
    @Override
    public Data<Map<String, Object>> watermark(Long id) {
        Attachments attachment = findOne(id, Fields.builder().add(Attachments.ID).add(Attachments.FILENAME).build());
        if (attachment != null) {
            return watermark(attachment.getFilename());
        }
        return null;
    }

    @Transactional(propagation = NOT_SUPPORTED)
    @Override
    public Data<Map<String, Object>> watermark(String attachment) {
        Map<String, String> map = new HashMap<>(16);
        map.put("attachment", attachment);
        return restService.postForEntity("/api/watermark", RestService.requestAsMap(map));
    }

    /**
     * <p>保存/更新图片</p>
     *
     * @param entity images
     * @param <S>    S
     * @return effective rows
     */
    @Override
    public <S extends Attachments> int save(S entity) {
        // [fetch] 判断数据是否存在于fetch中
        AttachmentsFetch from = entity.getFrom();
        if (from != null && from.getOrigin() != null) {
            from.setOriginHash(EntityUtils.asLong(from.getOrigin()));

            // 不要使用from作为查询条件, 显示设置查询条件
            AttachmentsFetch one = new AttachmentsFetch();
            one.setOrigin(from.getOrigin());
            one.setOriginHash(from.getOriginHash());
            one = Services.findOne(AttachmentsFetch.class, one, Fields.builder().add(AttachmentsFetch.ATTACHMENT_ID).build());
            if (one != null) {
                Long attachmentId = one.getAttachmentId();
                entity.setId(attachmentId);
                entity.setFrom(null);
            }
        } else {
            entity.setFrom(null);
        }

        // [attachments]
        int n;
        if (entity.getId() != null) {

            // 当更新附件的时候, 首先尝试将(enabled = 0 AND retry >= DEFAULT_RETRY)记录的重试次数清零
//            Attachments attachment = new Attachments(entity.getId());
//            attachment.setRetry(0);
//            n = super.update(attachment, Restrictions.and(
//                    Restrictions.eq(Attachments.ENABLED, 0),
//                    Restrictions.ge(Attachments.RETRY, Attachments.DEFAULT_RETRY)));
//            if (n == 0) {
//
//            }

            // id, height, weight, score, retry, enabled
            n = super.update(entity);
            if (n > 0 && entity.getEnabled() != null && entity.getEnabled() == 1) {
                Page<TopicsAttachments> topicsAttachmentsPage;
                SetOperations<String, String> setOperations = redisTemplate.opsForSet();
                int i = 1;
                do {
                    System.out.println(entity.getId());
                    topicsAttachmentsPage = Services.findAll(TopicsAttachments.class,
                            Restrictions.and(
                                    Restrictions.eq(TopicsAttachments.ATTACHMENT_ID, entity.getId()),
                                    Restrictions.eq(TopicsAttachments.ENABLED, 1)),
                            Pages.builder().page(i).size(100).build(),
                            Fields.builder().add(TopicsAttachments.TOPIC_ID).build());
                    topicsAttachmentsPage.getContent().forEach(topicsAttachments -> setOperations.add("topicIds", topicsAttachments.getTopicId().toString()));
                    ++i;
                } while (topicsAttachmentsPage.hasNext() && topicsAttachmentsPage.getContent().size() != 0);


                /*Services.doList(
                        TopicsAttachments.class,
                        Restrictions.and(
                                Restrictions.eq(TopicsAttachments.ATTACHMENT_ID, entity.getId()),
                                Restrictions.eq(TopicsAttachments.ENABLED, 1)),
                        Pages.builder().page(1).size(100).build(),
                        Fields.builder().add(TopicsAttachments.TOPIC_ID).build(),
                        list -> Services.update(Topics.class, list.stream().map(topicsAttachments -> new Topics(topicsAttachments.getTopicId())).collect(Collectors.toSet())));*/
            }
        } else {
            // FIXME 目前只有图片
            entity.setMime(Attachments.MediaType.JPG.getMime());
            entity.setFilename("");
            entity.setExtension("jpg");
            if ((n = super.save(entity)) > 0) {
                entity.setFilename(attachmentUrl.apply(baseUrl, entity.getId().toString()));
                Attachments update = new Attachments(entity.getId());
                update.setFilename(entity.getFilename());
                n = super.update(update);
            }
        }

        // [fetch] 不为空说明在数据库中没有匹配到, 是新的来源, 直接保存
        from = entity.getFrom();
        if (from != null) {
            from.setAttachmentId(entity.getId());
            Services.save(AttachmentsFetch.class, from);
        }

        // [file] upload file to Image Server if exists
        byte[] bytes = entity.getBytes();
        if (bytes != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    // upload file and watermark it
                    uploader.execute(new ByteArrayInputStream(bytes), entity, null); //image -> watermark(image.getFilename())
                    if (logger.isInfoEnabled()) {
                        logger.info("Upload image : " + entity.getFilename());
                    }

                } catch (Exception e) {
                    logger.error("error", e);
                    Attachments update = new Attachments();
                    update.setId(entity.getId());
                    update.setRetry(0);
                    update.setEnabled(0);
                    super.update(update);
                }
            });
        }

        return n;
    }

    /**
     * <p>修复下载成功但是没有上传到图片服务器的数据, 需要在图片服务器上执行的调度任务</p>
     * <p>调度任务不需要在事务中运行, 设置传播行为为never</p>
     */
    private Lock fixed = new ReentrantLock();

    // @Scheduled(initialDelay = 10 * 1000, fixedRate = 10 * 24 * 3600 * 1000)
    // @Transactional(propagation = Propagation.NOT_SUPPORTED)
    private void fix() {
        LockUtils.tryLock(fixed, () -> {
            Pageable pageable = Pages.builder().page(1).size(100).sort(Pages.sortBuilder().add(BaseEntity.ID, true).build()).build();
            Page<Attachments> page = Services.findAll(Attachments.class, condition(0L), pageable);
            AtomicInteger count = new AtomicInteger(0);
            AtomicInteger n = new AtomicInteger(0);
            while (page.hasContent()) {
                page.forEach(attachments -> {
                    File file = new File(attachments.getFilename().substring(6));
                    if (!file.exists()) {
                        Attachments attachment1 = new Attachments(attachments.getId());
                        attachment1.setRetry(0);
                        attachment1.setEnabled(0);
                        int update = Services.update(Attachments.class, attachment1);
                        if (logger.isInfoEnabled()) {
                            logger.info(String.format("STATUS: %s, NOT EXISTS: %s", update, file.getAbsolutePath()));
                        }

                        count.incrementAndGet();
                    }
                });
                Long id = page.getContent().get(page.getContent().size() - 1).getId();
                page = Services.findAll(Attachments.class, condition(id), pageable);

                //
                if (n.incrementAndGet() % 10 == 0) {
                    long total = page.getTotalElements();
                    if (logger.isInfoEnabled()) {
                        logger.info(String.format("progress total: %s, current: %s", total, id));
                    }

                }
            }
            if (logger.isInfoEnabled()) {
                logger.info("task finished count : " + count.get());
            }

            return true;
        });
    }

    private Criterion condition(Long id) {
        return Restrictions.and(
                Restrictions.eq(Attachments.ENABLED, 1),
                Restrictions.gt(BaseEntity.ID, id));
    }

    @Override
    public int add(Attachments attachments) {
        //TODO 图片大小，尺寸
        if (attachments.getId() != null) {
            Attachments att = this.findOne(attachments.getId());
            if (att == null) {
                throw new IllegalStateException();
            }
            attachments.setFilename(att.getFilename());
            attachments.setEnabled(1);
        }
        attachments.setGroupId(1);
        return this.save(attachments);
    }

    @Override
    public Page<Attachments> findAll(Criterion criterion, Pageable pageable, boolean count) {
        Page<Attachments> attachmentsPage = super.findAll(criterion, pageable, count);
        if (!attachmentsPage.hasContent()) {
            return attachmentsPage;
        }

        Map<Long, Attachments> attachmentsMap = attachmentsPage.getContent().stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        Set<Long> attachmentsIds = attachmentsMap.keySet();
        Object[] attachmentsIdsArray = attachmentsIds.toArray();

        Page<AttachmentsFetch> attachmentsFetchPage = Services.findAll(AttachmentsFetch.class,
                Restrictions.and(
                        Restrictions.in(AttachmentsFetch.ATTACHMENT_ID, attachmentsIdsArray)),
                Pages.builder().page(1).size(attachmentsIdsArray.length).build(),
                false);
        if (!attachmentsFetchPage.hasContent()) {
            return attachmentsPage;
        }
        attachmentsFetchPage.forEach(attachmentsFetch -> attachmentsMap.get(attachmentsFetch.getAttachmentId()).setFrom(attachmentsFetch));

        return attachmentsPage;
    }
}

