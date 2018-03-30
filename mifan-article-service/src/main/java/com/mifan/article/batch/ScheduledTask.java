package com.mifan.article.batch;

import com.mifan.article.domain.ScheduledJob;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.service.Services;

import java.util.Date;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/4
 */
public abstract class ScheduledTask {

    protected static Log logger = LogFactory.getLog(ScheduledTask.class);

    /**
     * job id
     */
    private long id;

    public ScheduledTask(long id) {
        this.id = id;
    }

    public void task() {
        // 根据ID查找: id, version, job_status, last_modified_time,
        ScheduledJob find = new ScheduledJob(id);
        find.setEnabled(1);
        ScheduledJob job = Services.findOne(ScheduledJob.class, find);
        if (job == null || !job.runnable()) {
            return;
        }

        // 根据id, version更新数据, 并使version++
        Date now = new Date();
        ScheduledJob newJob = new ScheduledJob(id);
        newJob.setVersion(job.getVersion() + 1);
        newJob.setStartTime(now);
        newJob.setJobStatus(ScheduledJob.JobStatus.NORMAL);
        newJob.setMessage("");
        int update = Services.update(ScheduledJob.class, newJob, Restrictions.eq(ScheduledJob.VERSION, job.getVersion()));

        // 更新失败直接return
        if (update <= 0) {
            return;
        }

        try {
            // 如果更新成功: 说明在分布式环境中获取到了唯一锁, 进入调度任务
            doTask(job.getLastModifiedTime(), now);
            ScheduledJob success = new ScheduledJob(id);
            success.setJobStatus(ScheduledJob.JobStatus.DONE);
            success.setEndTime(new Date());
            success.setLastModifiedTime(now);
            success.setMessage("");
            update(success);
        } catch (Exception e) {
            ScheduledJob error = new ScheduledJob(id);
            error.setJobStatus(ScheduledJob.JobStatus.EXCEPTIONAL);
            error.setEndTime(new Date());
            error.setMessage(e.getMessage() + ", " + e.toString());
            update(error);
        }
    }

    protected abstract void doTask(Date from, Date to);

    private void update(ScheduledJob job) {
        Services.update(ScheduledJob.class, job);
    }

}
