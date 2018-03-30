package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
public class MpDownloads extends BaseEntity {

    public static final String TABLE_NAME = "mp_downloads";

    public static final String TYPE = "type";
    public static final String DISPLAY_ORDER = "display_order";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";
    public static final String LINK = "link";
    public static final String TIMES = "times";

    private static final long serialVersionUID = 1320201576725786573L;

    private Integer type;
    private Integer displayOrder;
    private String title;
    private String description;
    private String image;
    private String link;
    private Integer times;

    public MpDownloads() {
    }

    public MpDownloads(Long id) {
        super(id);
    }

    /**
     * @return 0：91助手，1：驱动下载，2：常用软件
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 0：91助手，1：驱动下载，2：常用软件
     */
    public void setType(Integer type) {
        this.type = type;
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
     * @return 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return 标题图
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image 标题图
     */
    public void setImage(String image) {
        this.image = image;
    }
    /**
     * @return 下载地址
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link 下载地址
     */
    public void setLink(String link) {
        this.link = link;
    }
    /**
     * @return 下载次数
     */
    public Integer getTimes() {
        return times;
    }

    /**
     * @param times 下载次数
     */
    public void setTimes(Integer times) {
        this.times = times;
    }

}
