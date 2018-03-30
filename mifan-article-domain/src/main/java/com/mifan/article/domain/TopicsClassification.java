package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class TopicsClassification extends BaseEntity {

    public static final String TABLE_NAME = "topics_classification";

    public static final String FINGERPRINT = "fingerprint";
    public static final String TARGET_VARIABLE = "target_variable";

    private static final long serialVersionUID = -7783523835157086987L;

    private Long fingerprint;
    private String targetVariable;

    public TopicsClassification() {
    }

    public TopicsClassification(Long id) {
        super(id);
    }

    /**
     * @return 语义指纹
     */
    public Long getFingerprint() {
        return fingerprint;
    }

    /**
     * @param fingerprint 语义指纹
     */
    public void setFingerprint(Long fingerprint) {
        this.fingerprint = fingerprint;
    }
    /**
     * @return 目标变量
     */
    public String getTargetVariable() {
        return targetVariable;
    }

    /**
     * @param targetVariable 目标变量
     */
    public void setTargetVariable(String targetVariable) {
        this.targetVariable = targetVariable;
    }

}
