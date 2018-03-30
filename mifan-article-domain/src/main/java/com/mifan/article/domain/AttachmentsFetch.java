package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class AttachmentsFetch extends BaseEntity {

    public static final String TABLE_NAME = "attachments_fetch";

    public static final String ATTACHMENT_ID = "attachment_id";
    public static final String ORIGIN = "origin";
    public static final String ORIGIN_HASH = "origin_hash";

    private static final long serialVersionUID = -4690454751714483989L;

    private Long attachmentId;
    private String origin;
    private Long originHash;

    public AttachmentsFetch() {
    }

    public AttachmentsFetch(Long id) {
        super(id);
    }

    /**
     * @return 附件ID
     */
    public Long getAttachmentId() {
        return attachmentId;
    }

    /**
     * @param attachmentId 附件ID
     */
    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }
    /**
     * @return 来源URL
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * @param origin 来源URL
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    /**
     * @return 来源URL HASH
     */
    public Long getOriginHash() {
        return originHash;
    }

    /**
     * @param originHash 来源URL HASH
     */
    public void setOriginHash(Long originHash) {
        this.originHash = originHash;
    }

}
