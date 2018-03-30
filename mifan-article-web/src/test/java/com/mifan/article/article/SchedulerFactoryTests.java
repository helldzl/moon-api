package com.mifan.article.article;

import com.mifan.article.job.SchedulerFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/7
 */
public class SchedulerFactoryTests {

    public static void main(String[] args) throws ClassNotFoundException, ParseException, SchedulerException, InterruptedException {
        Scheduler scheduler = SchedulerFactory.getInstance();

        JobDetail job = new JobDetail();
        job.setGroup(Scheduler.DEFAULT_GROUP);
        job.setName("JOI");
        job.setJobClass(Class.forName("com.mifan.article.job.impl.SpiderJob"));
        // job.setJobDataMap(jobDataMap);

        CronTrigger trigger = new CronTrigger();
        trigger.setGroup(Scheduler.DEFAULT_GROUP);
        trigger.setName("trigger-1");
        trigger.setCronExpression("0/5 * * * * ?");
        scheduler.scheduleJob(job, trigger);

        TimeUnit.SECONDS.sleep(5);
        System.out.println("FINISHED");
        scheduler.shutdown();

        TimeUnit.SECONDS.sleep(5);
        System.out.println("start again");

        scheduler = SchedulerFactory.getInstance();
        scheduler.scheduleJob(job, trigger);

        TimeUnit.SECONDS.sleep(5);
        scheduler.shutdown();
    }

}
