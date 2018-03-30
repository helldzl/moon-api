package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class Forums extends BaseEntity {

    public static final String TABLE_NAME = "forums";

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String TOPICS = "topics";
    public static final String DISPLAY_ORDER = "display_order";
    public static final String MODERATED = "moderated";

    private static final long serialVersionUID = -3686911417802725481L;

    private String name;
    private String description;
    private Integer topics;
    private Integer displayOrder;
    private Integer moderated;

    public Forums() {
    }

    public Forums(Long id) {
        super(id);
    }

    /**
     * @return 版面名称
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 版面名称
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return 版面描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 版面描述
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return 主题数量
     */
    public Integer getTopics() {
        return topics;
    }

    /**
     * @param topics 主题数量
     */
    public void setTopics(Integer topics) {
        this.topics = topics;
    }
    /**
     * @return 排序
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /**
     * @param displayOrder 排序
     */
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    /**
     * @return 是否审核此版面{0:否, 1:是}
     */
    public Integer getModerated() {
        return moderated;
    }

    /**
     * @param moderated 是否审核此版面{0:否, 1:是}
     */
    public void setModerated(Integer moderated) {
        this.moderated = moderated;
    }

}
