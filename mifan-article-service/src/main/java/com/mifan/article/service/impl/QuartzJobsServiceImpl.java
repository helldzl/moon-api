package com.mifan.article.service.impl;

import com.mifan.article.dao.QuartzJobsDao;
import com.mifan.article.domain.QuartzJobs;
import com.mifan.article.domain.QuartzJobs.JobStatus;
import com.mifan.article.job.SchedulerFactory;
import com.mifan.article.service.QuartzJobsService;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.quartz.Scheduler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * <p>Unsafe</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017-11-02
 */
@Service
public class QuartzJobsServiceImpl extends AbstractBaseService<QuartzJobs, QuartzJobsDao> implements QuartzJobsService, InitializingBean {

    @Value("${application.admin.quartz.enable}")
    private boolean adminQuartzEnable;

    @Override
    public void afterPropertiesSet() {
        if (adminQuartzEnable) {
            SchedulerFactory.getInstance();
            super.findAll(Restrictions.and(
                    Restrictions.eq(BaseEntity.ENABLED, 1),
                    Restrictions.eq(QuartzJobs.AUTO, 1),
                    Restrictions.eq(QuartzJobs.JOB_STATUS, JobStatus.RUNNABLE.name())
            )).forEach(quartzJobs -> SchedulerFactory.rescheduleJob(quartzJobs, quartzJobs));
        }
    }

    /**
     * <p>保存一个调度任务</p>
     *
     * @param entity entity
     * @param <S>    S
     * @return int
     */
    @Override
    public <S extends QuartzJobs> int save(S entity) {
        String jobGroup = entity.getJobGroup();
        if (jobGroup == null) {
            jobGroup = Scheduler.DEFAULT_GROUP;
            entity.setJobGroup(jobGroup);
        }

        String triggerGroup = entity.getTriggerGroup();
        if (triggerGroup == null) {
            triggerGroup = Scheduler.DEFAULT_GROUP;
            entity.setTriggerGroup(triggerGroup);
        }

        // 检查JOB命名空间
        if (super.count(Restrictions.and(
                Restrictions.eq(QuartzJobs.JOB_GROUP, jobGroup),
                Restrictions.eq(QuartzJobs.JOB_NAME, entity.getJobName()))) > 0) {
            throw new IllegalStateException("任务命名空间不唯一, 请换一个任务名称");
        }

        // 检查TRIGGER命名空间
        if (super.count(Restrictions.and(
                Restrictions.eq(QuartzJobs.TRIGGER_GROUP, triggerGroup),
                Restrictions.eq(QuartzJobs.TRIGGER_NAME, entity.getTriggerName()))) > 0) {
            throw new IllegalStateException("触发器命名空间不唯一, 请换一个触发器名称");
        }

        // 检查任务
        try {
            Class.forName(entity.getJobClass());
        } catch (Exception e) {
            throw new IllegalStateException("没有找到任务:" + entity.getJobClass());
        }

        entity.setJobStatus(JobStatus.NEW);
        return super.save(entity);
    }

    /**
     * <p>删除任务</p>
     * <ol>
     * <li>从数据库中逻辑删除任务, 并修改任务状态</li>
     * <li>从调度器中删除任务</li>
     * </ol>
     *
     * @param id id
     * @return int
     */
    @Override
    public int remove(Long id) {
        // find job info
        QuartzJobs one = super.findOne(id, Fields.builder().add(QuartzJobs.JOB_NAME).add(QuartzJobs.JOB_GROUP).build());

        // update state
        QuartzJobs update = new QuartzJobs(id);
        update.setEnabled(0);
        update.setJobStatus(JobStatus.TERMINATED);

        int n;
        if ((n = super.update(update, Restrictions.eq(BaseEntity.ENABLED, 1))) > 0) {
            SchedulerFactory.deleteJob(one.getJobName(), one.getJobGroup());
        }
        return n;
    }

    /**
     * @param entity entity
     * @param <S>    S
     * @return int
     */
    @Override
    public <S extends QuartzJobs> int update(S entity) {
        QuartzJobs oldJob = super.findOne(entity.getId());

        Integer version = entity.getVersion();
        entity.setVersion(version + 1);
        if (super.update(entity, Restrictions.eq(QuartzJobs.VERSION, version)) > 0) {
            QuartzJobs newJob = super.findOne(entity.getId());

            JobStatus oldJobStatus = JobStatus.valueOf(oldJob.getJobStatus());
            JobStatus newJobStatus = JobStatus.valueOf(newJob.getJobStatus());

            if (newJob.getEnabled() == 1 && JobStatus.RUNNABLE == newJobStatus) {
                SchedulerFactory.rescheduleJob(oldJob, newJob);
            } else {
                SchedulerFactory.deleteJob(oldJob.getJobName(), oldJob.getJobGroup());
            }

            return 1;
        } else {
            throw new IllegalStateException("版本已过期, 可能有其他管理员在修改数据, 请稍后再操作!");
        }
    }

    @Override
    public Page<QuartzJobs> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        return super.findAll(criterion, pageable, fields);
    }

}
