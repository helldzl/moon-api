package com.mifan.article.job;

import org.quartz.*;
import org.springframework.context.ApplicationContext;

import static org.moonframework.model.mybatis.service.Services.getApplicationContext;

/**
 * <p>A marker interface for JobDetail s that wish to have their state maintained between executions.</p>
 * <p>StatefulJob instances follow slightly different rules from regular Job instances. The key difference is that their associated JobDataMap is re-persisted after every execution of the job, thus preserving state for the next execution. The other difference is that stateful jobs are not allowed to execute concurrently, which means new triggers that occur before the completion of the execute(xx) method will be delayed.</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/11/2
 */
public abstract class JobAdapter implements Job, StatefulJob {

    private JobDetail jobDetail;
    private JobDataMap jobDataMap;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        this.jobDetail = jobExecutionContext.getJobDetail();
        this.jobDataMap = this.jobDetail.getJobDataMap();
        template(getApplicationContext(), this.jobDataMap);
    }

    public abstract void template(ApplicationContext applicationContext, JobDataMap jobDataMap);

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public JobDataMap getJobDataMap() {
        return jobDataMap;
    }

}
