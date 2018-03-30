package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * Created by LiuKai on 2017/11/9.
 */
public class SpiderStatistics {
    //种子id
    private Integer seedId;
    //网站名字
    private String name;
    //网站描述
    private String description;
    //网站爬取 数目合计
    private Long counts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSeedId() {
        return seedId;
    }

    public void setSeedId(Integer seedId) {
        this.seedId = seedId;
    }

    public Long getCounts() {
        return counts;
    }

    public void setCounts(Long counts) {
        this.counts = counts;
    }

    @Override
    public String toString() {
        return "SpiderStatistics{" +
                "seedId=" + seedId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", counts=" + counts +
                '}';
    }
}
