package com.mifan.article.service;

import com.mifan.article.domain.TranslateTask;
import com.mifan.article.domain.support.TranslateTaskAudit;
import org.moonframework.model.mybatis.service.BaseService;
import org.springframework.data.domain.Page;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-11-02
 */
public interface TranslateTaskService extends BaseService<TranslateTask> {
    /**
     * 翻译人员操作
     * 1：领取任务，2：第一次添加翻译，3：修改翻译 4：审核失败后继续翻译
     * @param entity
     * @return
     */
    int updateOfTranslator(TranslateTask entity);
    /**
     * 翻译人员-列表查询
     * @param entity
     * @param page
     * @param size
     * @return
     */
    Page<TranslateTask> doGetPageOfTranslator(TranslateTask entity, int page,int size);

    /**
     * 审核人员操作
     * 1开始审核，2暂存、提交审核结果
     * @param task
     * @return
     */
    int updateOfAuditor(TranslateTaskAudit task);
    /**
     * 审核人员-列表查询
     * @param entity
     * @param page
     * @param size
     * @return
     */
    Page<TranslateTask> doGetPageOfAuditor(TranslateTask entity, int page,int size);
}
