/**
 * 详情浏览实体
 */
package com.mifan.support.domain.support;

import java.io.Serializable;

/**
 * @author ZYW
 *
 */
public class PageViews implements Serializable{

    private static final long serialVersionUID = 1558553209816727706L;

    private String urlLog;
    private Long topicId;
    private long pageViews;
    private String title;

    public String getUrlLog() {
        return urlLog;
    }

    public void setUrlLog(String urlLog) {
        this.urlLog = urlLog;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
    public long getPageViews() {
        return pageViews;
    }

    public void setPageViews(long pageViews) {
        this.pageViews = pageViews;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
