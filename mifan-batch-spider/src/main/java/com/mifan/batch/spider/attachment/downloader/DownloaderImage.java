package com.mifan.batch.spider.attachment.downloader;

import com.mifan.batch.spider.domain.Attachments;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.core.amqp.Message;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by LiuKai on 2017/12/5.
 */

public class DownloaderImage implements Callable {
    private final Log logger = LogFactory.getLog(DownloaderImage.class);
    private Message message;
    private Map<Long, Object> map;
    private StringRedisTemplate template;

    public DownloaderImage(Message message,StringRedisTemplate template, Map<Long, Object> map ) {
        this.message = message;
        this.map = map;
        this.template = template;
    }

    public DownloaderImage(Message message, Map<Long, Object> map) {
        this.message = message;
        this.map = map;
    }

    private List<Attachments> attachmentsList;

    @Override
    public Long call() {
        Long topic_id=0L;
        for (Long l : map.keySet()) {
            Object obj = map.get(l);
            if (obj instanceof List) {
                attachmentsList = (List<Attachments>) obj;
            }
            topic_id=l;
        }
        Long returnLong=new DownloaderHandler(message,template).download(attachmentsList);
        logger.info("Topic_id:"+topic_id+" 可以更新，设置enabled=1了");
        return returnLong;
    }
}
