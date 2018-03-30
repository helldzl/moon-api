package com.mifan.article.batch.impl;

import com.mifan.article.batch.ScheduledTask;
import com.mifan.article.domain.Topics;
import com.mifan.article.service.SearchService;
import org.moonframework.concurrent.util.LockUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/16
 */
@Component
public class IndexTask extends ScheduledTask {

    public IndexTask() {
        super(1L);
    }

    @Value("${application.scheduler.enable.index:false}")
    private boolean enableIndex;

    @Autowired
    private SearchService searchService;

    private final Lock indexLock = new ReentrantLock();

    @Scheduled(initialDelay = 10 * 1000, fixedRate = 60 * 5 * 1000)
    @Override
    public void task() {
        super.task();
    }

    @Override
    public void doTask(Date from, Date to) {
        if (!enableIndex) {
            return;
        }

        if (logger.isInfoEnabled()) {
            logger.info("Execute index scheduler");
        }

        boolean result = LockUtils.tryLock(indexLock, -1, () -> {
            searchService.index(Topics.class);
            return true;
        });

        if (result) {
            if (logger.isInfoEnabled()) {
                logger.info("Execute index scheduler successful!");
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("Execute index scheduler failed, try lock timeout!");
            }
        }

    }

}
