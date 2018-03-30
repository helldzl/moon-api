package com.mifan.batch.spider.service.impl;

import com.mifan.batch.spider.domain.Attachments;
import com.mifan.batch.spider.service.AttachmentsService;
import org.moonframework.core.amqp.Message;
import org.moonframework.core.util.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiuKai on 2017/7/6.
 */
@Service
public class AttachmentsServiceImpl implements AttachmentsService {
    @Autowired
    private Message message;
    @Autowired
    private StringRedisTemplate template;

    @Override
    public Attachments saveOrUpdate(Attachments attachment) {
        // meta
        Map<String, String> meta = new HashMap<>(3);
        meta.put("name", "images");
        meta.put("origin", attachment.getOrigin());
        meta.put("className", "com.mifan.article.domain.Attachments");

        Map<String, Object> result = message.sendAndReceive(meta, BeanUtils.toMap(attachment), null);

        Attachments target = new Attachments();
        BeanWrapper wrapper = new BeanWrapperImpl(target);
        wrapper.setPropertyValues(result);

        attachment.setId(target.getId());
        attachment.setFilename(target.getFilename());
        attachment.setRetry(target.getRetry());
        attachment.setEnabled(target.getEnabled());

        if (target.getEnabled() == 1) {
            URI uri = URI.create(attachment.getOrigin());
            template.opsForSet().add(uri.getHost() + ":" + "image", attachment.getOrigin());
        }

        return attachment;
    }
}
