package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-08
 */
public class TopicsBrandMapping extends BaseEntity {

    public static final String TABLE_NAME = "topics_brand_mapping";

    public static final String PREFIX = "prefix";
    public static final String SOURCE_NAME = "source_name";
    public static final String TARGET_NAME = "target_name";

    private static final long serialVersionUID = -52662047246282330L;

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(TopicsBrandMapping.ID)
            .add(TopicsBrandMapping.PREFIX)
            .add(TopicsBrandMapping.SOURCE_NAME)
            .add(TopicsBrandMapping.TARGET_NAME)
            .build();

    private String prefix;
    private String sourceName;
    private String targetName;

    public TopicsBrandMapping() {
    }

    public TopicsBrandMapping(Long id) {
        super(id);
    }

    /**
     * @return
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     * @param sourceName
     */
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    /**
     * @return
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * @param targetName
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

}
