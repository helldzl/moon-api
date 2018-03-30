package com.mifan.article.service;

import com.mifan.article.domain.Attachments;
import org.moonframework.model.mybatis.service.BaseService;
import org.moonframework.web.jsonapi.Data;

import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public interface AttachmentsService extends BaseService<Attachments> {
    Data<Map<String, Object>> watermark(Long id);

    Data<Map<String, Object>> watermark(String attachment);
    
    /**
     * 添加附件，处理一些附件信息
     * @param attachments
     * @return
     */
    int add(Attachments attachments);

}
