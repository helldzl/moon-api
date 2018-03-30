package com.mifan.article.batch.impl;

import com.mifan.article.batch.ScheduledTask;
import com.mifan.article.domain.Attachments;
import com.mifan.article.service.RestService;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/4
 */
@Component
public class WaterPrintTask extends ScheduledTask {

    @Autowired
    private RestService restService;

    @Value("${application.scheduler.enable.water.print:false}")
    private boolean enableWaterPrint;

    public WaterPrintTask() {
        super(2L);
    }

    @Scheduled(initialDelay = 10 * 500, fixedRate = 10 * 24 * 3600 * 1000)
    @Override
    public void task() {
        super.task();
    }

    @Override
    protected void doTask(Date from, Date to) {
        if (!enableWaterPrint) {
            return;
        }
        Services.doList(Attachments.class,
                from,
                to,
                Fields.builder().add("id").add("filename").build(),
                Restrictions.eq(Attachments.MIME, "image/jpeg"),
                this::doPage);
    }

    /**
     * API : http://note.youdao.com/share/?id=e3c3fc561bf003be2225ef4998a474f1&type=note#/
     *
     * @param page page
     */
    private void doPage(Page<Attachments> page) {
        for (Attachments entity : page.getContent()) {
            try {
                Map<String, String> map = new HashMap<>(16);
                if (entity.getFilename() != null) {
                    map.put("image", entity.getFilename());
                    Data<Map> result = this.restService.postForEntity(Map.class, "/api/watermark", RestService.requestAsMap(map));
                    if (logger.isInfoEnabled()) {
                        logger.info(result.getData());
                    }
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
    }

}
