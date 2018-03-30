package com.mifan.article.domain;

import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.Date;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-11-02
 */
public class QuartzJobs extends BaseEntity {

    public static final String TABLE_NAME = "quartz_jobs";

    public static final String JOB_STATUS = "job_status";
    public static final String JOB_GROUP = "job_group";
    public static final String JOB_NAME = "job_name";
    public static final String JOB_CLASS = "job_class";
    public static final String JOB_DATA = "job_data";
    public static final String JOB_DATA_TEMPLATE = "job_data_template";
    public static final String TRIGGER_GROUP = "trigger_group";
    public static final String TRIGGER_NAME = "trigger_name";
    public static final String TRIGGER_CRON_EXPRESSION = "trigger_cron_expression";
    public static final String DESCRIPTION = "description";
    public static final String MESSAGE = "message";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String LAST_START_TIME = "last_start_time";
    public static final String VERSION = "version";
    public static final String AUTO = "auto";

    private static final long serialVersionUID = 9003357031983486306L;

    private String jobStatus;

    @Size(groups = {Post.class, Patch.class}, message = "{Size.QuartzJobs.jobGroup}", min = 1, max = 80)
    private String jobGroup;

    @NotNull(groups = {Post.class}, message = "{NotNull.QuartzJobs.jobName}")
    @Size(groups = {Post.class, Patch.class}, message = "{Size.QuartzJobs.jobName}", min = 1, max = 80)
    private String jobName;

    @NotNull(groups = {Post.class}, message = "{NotNull.QuartzJobs.jobClass}")
    @Size(groups = {Post.class, Patch.class}, message = "{Size.QuartzJobs.jobClass}", min = 1, max = 200)
    private String jobClass;

    @Size(groups = {Post.class, Patch.class}, message = "{Size.QuartzJobs.jobData}", min = 1, max = 2048)
    private String jobData;

    @Size(groups = {Post.class, Patch.class}, message = "{Size.QuartzJobs.jobDataTemplate}", min = 1, max = 2048)
    private String jobDataTemplate;

    @Size(groups = {Post.class, Patch.class}, message = "{Size.QuartzJobs.triggerGroup}", min = 1, max = 80)
    private String triggerGroup;

    @NotNull(groups = {Post.class}, message = "{NotNull.QuartzJobs.triggerName}")
    @Size(groups = {Post.class, Patch.class}, message = "{Size.QuartzJobs.triggerName}", min = 1, max = 80)
    private String triggerName;

    @NotNull(groups = {Post.class}, message = "{NotNull.QuartzJobs.triggerCronExpression}")
    private String triggerCronExpression;

    @Size(groups = {Post.class, Patch.class}, message = "{Size.QuartzJobs.description}", min = 1, max = 200)
    private String description;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.QuartzJobs.message}")
    private String message;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.QuartzJobs.startTime}")
    private Date startTime;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.QuartzJobs.endTime}")
    private Date endTime;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.QuartzJobs.lastStartTime}")
    private Date lastStartTime;

    @NotNull(groups = {Patch.class}, message = "{NotNull.QuartzJobs.version}")
    @Null(groups = {Post.class}, message = "{Null.QuartzJobs.version}")
    private Integer version;

    @Range(groups = {Post.class, Patch.class}, message = "{Range.QuartzJobs.auto}", min = 0, max = 1)
    private Integer auto;

    public QuartzJobs() {
    }

    public QuartzJobs(Long id) {
        super(id);
    }

    public String getJobStatusValue() {
        if (jobStatus == null) {
            return null;
        }
        return JobStatus.valueOf(jobStatus).getName();
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus.name();
    }

    //

    /**
     * @return 任务状态
     */
    public String getJobStatus() {
        return jobStatus;
    }

    /**
     * @param jobStatus 任务状态
     */
    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    /**
     * @return 任务组
     */
    public String getJobGroup() {
        return jobGroup;
    }

    /**
     * @param jobGroup 任务组
     */
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    /**
     * @return 任务名称
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param jobName 任务名称
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * @return 任务类
     */
    public String getJobClass() {
        return jobClass;
    }

    /**
     * @param jobClass 任务类
     */
    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    /**
     * @return 任务参数, JSON格式
     */
    public String getJobData() {
        return jobData;
    }

    /**
     * @param jobData 任务参数, JSON格式
     */
    public void setJobData(String jobData) {
        this.jobData = jobData;
    }

    /**
     * @return 任务参数模板
     */
    public String getJobDataTemplate() {
        return jobDataTemplate;
    }

    /**
     * @param jobDataTemplate 任务参数模板
     */
    public void setJobDataTemplate(String jobDataTemplate) {
        this.jobDataTemplate = jobDataTemplate;
    }

    /**
     * @return 触发器组
     */
    public String getTriggerGroup() {
        return triggerGroup;
    }

    /**
     * @param triggerGroup 触发器组
     */
    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    /**
     * @return 触发器名称
     */
    public String getTriggerName() {
        return triggerName;
    }

    /**
     * @param triggerName 触发器名称
     */
    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    /**
     * @return CRON表达式
     */
    public String getTriggerCronExpression() {
        return triggerCronExpression;
    }

    /**
     * @param triggerCronExpression CRON表达式
     */
    public void setTriggerCronExpression(String triggerCronExpression) {
        this.triggerCronExpression = triggerCronExpression;
    }

    /**
     * @return 任务描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 任务描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return
     */
    public Date getLastStartTime() {
        return lastStartTime;
    }

    /**
     * @param lastStartTime
     */
    public void setLastStartTime(Date lastStartTime) {
        this.lastStartTime = lastStartTime;
    }

    /**
     * @return 版本号
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version 版本号
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return 是否自动启动
     */
    public Integer getAuto() {
        return auto;
    }

    /**
     * @param auto 是否自动启动
     */
    public void setAuto(Integer auto) {
        this.auto = auto;
    }

    public enum JobStatus {
        NEW("新建"),
        SUSPEND("暂停"),
        RUNNABLE("运行中"),
        TERMINATED("终止");

        private final String name;

        JobStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
