package com.mifan.article.job;

import com.mifan.article.domain.QuartzJobs;
import io.jsonwebtoken.lang.Collections;
import org.moonframework.core.util.ObjectMapperFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/2
 */
public final class SchedulerFactory {

    private static Scheduler scheduler;

    private static CronTrigger trigger(String name, String group, String expression) throws ParseException {
        CronTrigger trigger = new CronTrigger();
        trigger.setName(name);
        trigger.setGroup(group);
        trigger.setCronExpression(expression);
        trigger.setStartTime(startTime());
        return trigger;
    }

    private static JobDetail jobDetail(String name, String group, String jobClass, Map<String, Object> data) throws ClassNotFoundException {
        JobDetail job = new JobDetail();
        job.setName(name);
        job.setGroup(group);
        job.setJobClass(Class.forName(jobClass));
        if (!Collections.isEmpty(data)) {
            job.setJobDataMap(new JobDataMap(data));
        }
        return job;
    }

    private static Date startTime() {
        return Date.from(LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Scheduler getInstance() {
        try {
            // 这块是线程安全的, 多个线程同时访问, 会获得同一个scheduler实例
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            if (SchedulerFactory.scheduler != scheduler) {
                // 说明scheduler已经shutdown了, 不能再使用了, 切换调度器
                SchedulerFactory.scheduler = scheduler;
                scheduler.start();
            }
            return scheduler;
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public static void rescheduleJob(QuartzJobs oldJob, QuartzJobs newJob) {
        try {
            scheduler.deleteJob(oldJob.getJobName(), oldJob.getJobGroup());

            Map<String, Object> data = newJob.getJobData() == null ? null : ObjectMapperFactory.readValue(newJob.getJobData(), Map.class);
            JobDetail jobDetail = jobDetail(newJob.getJobName(), newJob.getJobGroup(), newJob.getJobClass(), data);

            Trigger trigger = getTrigger(newJob.getTriggerName(), newJob.getTriggerGroup());
            if (trigger == null) {
                trigger = trigger(newJob.getTriggerName(), newJob.getTriggerGroup(), newJob.getTriggerCronExpression());
            } else if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                cronTrigger.setCronExpression(newJob.getTriggerCronExpression());
                cronTrigger.setStartTime(startTime());
            }

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteJob(String jobName, String jobGroup) {
        try {
            return scheduler.deleteJob(jobName, jobGroup);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * <p>是否停止</p>
     *
     * @return boolean
     */
    public static boolean isShutdown() {
        try {
            return scheduler.isShutdown();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>获取任务</p>
     *
     * @param name name
     * @return Job
     */
    public static JobDetail getJobDetail(String name) {
        return getJobDetail(name, Scheduler.DEFAULT_GROUP);
    }

    /**
     * <p>获取任务</p>
     *
     * @param name  name
     * @param group group
     * @return Job
     */
    public static JobDetail getJobDetail(String name, String group) {
        try {
            return scheduler.getJobDetail(name, group);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>获取触发器</p>
     *
     * @param name name
     * @return Trigger
     */
    public static Trigger getTrigger(String name) {
        return getTrigger(name, Scheduler.DEFAULT_GROUP);
    }

    /**
     * <p>获取触发器</p>
     *
     * @param name  name
     * @param group group
     * @return Trigger
     */
    public static Trigger getTrigger(String name, String group) {
        try {
            return scheduler.getTrigger(name, group);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>暂停任务</p>
     *
     * @param name 任务名称
     */
    public static void pauseJob(String name) {
        pauseJob(name, Scheduler.DEFAULT_GROUP);
    }

    /**
     * <p>暂停任务</p>
     *
     * @param name  任务名称
     * @param group 分组名称
     */
    public static void pauseJob(String name, String group) {
        try {
            scheduler.pauseJob(name, group);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>恢复任务</p>
     *
     * @param name 任务名称
     */
    public static void resumeJob(String name) {
        resumeJob(name, Scheduler.DEFAULT_GROUP);
    }

    /**
     * <p>恢复任务</p>
     *
     * @param name  任务名称
     * @param group 分组名称
     */
    public static void resumeJob(String name, String group) {
        try {
            scheduler.resumeJob(name, group);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>停止所有任务</p>
     */
    public static void pauseAll() {
        try {
            scheduler.pauseAll();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>恢复所有任务</p>
     */
    public static void resumeAll() {
        try {
            scheduler.resumeAll();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

}
