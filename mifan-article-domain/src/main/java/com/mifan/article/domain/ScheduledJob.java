package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-08-05
 */
public class ScheduledJob extends BaseEntity {

    public static final String TABLE_NAME = "scheduled_job";

    public static final String VERSION = "version";
    public static final String JOB_STATUS = "job_status";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String LAST_MODIFIED_TIME = "last_modified_time";
    public static final String MESSAGE = "message";
    public static final String DESCRIPTION = "description";

    private static final long serialVersionUID = -4993484208134794933L;

    private Integer version;
    private String jobStatus;
    private Date startTime;
    private Date endTime;
    private Date lastModifiedTime;
    private String message;
    private String description;

    public ScheduledJob() {
    }

    public ScheduledJob(Long id) {
        super(id);
    }

    public JobStatus getJobStatusValue() {
        return JobStatus.from(jobStatus);
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus.getName();
    }

    /**
     * 是否可运行
     *
     * @return
     */
    public boolean runnable() {
        // return JobStatus.NORMAL != getJobStatusValue();
        return true;
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
     * @return 任务开始时间
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 任务开始时间
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return 任务结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 任务结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return 最后更新时间
     */
    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * @param lastModifiedTime 最后更新时间
     */
    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    /**
     * @return 异常信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message 异常信息
     */
    public void setMessage(String message) {
        this.message = message;
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

    public enum JobStatus {

        NORMAL("NORMAL"),
        EXCEPTIONAL("EXCEPTIONAL"),
        DONE("DONE");

        private static Map<String, JobStatus> map = new HashMap<>();

        static {
            for (JobStatus jobStatus : JobStatus.values()) {
                map.put(jobStatus.getName(), jobStatus);
            }
        }

        public static JobStatus from(String name) {
            return map.get(name);
        }

        private final String name;

        JobStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
