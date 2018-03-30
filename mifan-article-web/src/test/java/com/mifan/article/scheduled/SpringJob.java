package com.mifan.article.scheduled;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/2
 */
public class SpringJob extends QuartzJobBean implements StatefulJob {

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {

    }

    public static void main(String[] args) {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.getObject();
    }
}
