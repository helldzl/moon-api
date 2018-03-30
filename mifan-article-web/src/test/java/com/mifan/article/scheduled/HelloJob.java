package com.mifan.article.scheduled;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/2
 */
public class HelloJob
//        extends QuartzJobBean
        implements Job, StatefulJob {
    private int timeout;

//    @Override
//    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
//        System.out.println("Hello QuartzJobBean!");
//        System.out.println(timeout);
//    }

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Object timeout = jobDataMap.get("timeout");
        this.timeout = (int) timeout;
        System.out.println("Hello Quartz! " + timeout);
//        if (this.timeout == 10) {
//            try {
//                TimeUnit.SECONDS.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    private static JobDetail job(int timeout, String name) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("timeout", timeout);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(map);
        // 任务
        JobDetail job = new JobDetail();
        job.setGroup("job-group");
        job.setName(name);
        job.setJobClass(Class.forName("com.mifan.article.scheduled.HelloJob"));
        job.setJobDataMap(jobDataMap);
        return job;
    }

    public static CronTrigger trigger(String name, String cronExpression) throws ParseException {
        CronTrigger trigger = new CronTrigger();
        trigger.setGroup("trigger-group");
        trigger.setName(name);
        trigger.setCronExpression(cronExpression);
        return trigger;
    }


    public static void main(String[] args) throws Exception {

        // 触发器 "0/5 * * * * ?"
        //schedule it
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();

        scheduler.start();
        scheduler.scheduleJob(job(10, "JOB-A"), trigger("TRIGGER-A", "0/2 * * * * ?"));
        scheduler.scheduleJob(job(20, "JOB-B"), trigger("TRIGGER-B", "0/4 * * * * ?"));


        TimeUnit.SECONDS.sleep(10);
        System.out.println("修改任务");
        for (String group : scheduler.getTriggerGroupNames()) {
            for (String triggerName : scheduler.getTriggerNames(group)) {
                CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerName, group);
                cronTrigger.setCronExpression("0/15 * * * * ?");

                //cronTrigger.getJobDataMap().put("timeout", 110);
                Date date = new Date();
                System.out.println("START:" + cronTrigger.getStartTime());
                System.out.println("END:" + cronTrigger.getEndTime());
                System.out.println(date);
                cronTrigger.setStartTime(new Date());
                System.out.println("rebuild");

                JobDetail dummyJobName = scheduler.getJobDetail("JOB-A", "job-group");

                // 第一种方式:
                scheduler.deleteJob("JOB-A", "job-group");
                scheduler.scheduleJob(dummyJobName, cronTrigger);

                // 第二种方式: 重新安排任务, 参数没有变的时候可以使用此策略, 即仅trigger变化时
                // scheduler.rescheduleJob(cronTrigger.getName(), cronTrigger.getGroup(), cronTrigger);


                break;
            }
        }

        TimeUnit.SECONDS.sleep(20);
        System.out.println("关闭任务");
        // scheduler.shutdown(true); // Halts the Scheduler's firing of Triggers, and cleans up all resources associated with the Scheduler.

        System.out.println(scheduler.isStarted());

        TimeUnit.SECONDS.sleep(20);
        System.out.println("开启任务");
        scheduler.start();

        // 第三种方式: 暂停/恢复任务
        TimeUnit.SECONDS.sleep(20);
        System.out.println("开始暂停任务");
        scheduler.pauseJob("JOB-A", "job-group");

        TimeUnit.SECONDS.sleep(20);
        System.out.println("开始恢复任务");
        scheduler.resumeJob("JOB-A", "job-group");

        // 第四种方式: 暂停/恢复所有任务
        TimeUnit.SECONDS.sleep(20);
        scheduler.pauseAll();
        scheduler.resumeAll();

        System.out.println();
    }

    // 通过scheduler获得trigger和job时, 使用的都是clone模式
    // 一个job和一个trigger绑定

    // 1. 删除任务 -> deleteJob(jobName, jobGroup)
    // 2. 重新安排 -> deleteJob(jobName, jobGroup) + scheduleJob(job, trigger) | rescheduleJob

}
