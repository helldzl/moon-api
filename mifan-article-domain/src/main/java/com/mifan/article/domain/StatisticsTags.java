package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;
import java.util.Date;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class StatisticsTags extends BaseEntity {

    public static final String TABLE_NAME = "statistics_tags";

    public static final String CURRENT_HOUR = "current_hour";
    public static final String TOTAL_COUNT = "total_count";
    public static final String TAG = "tag";

    private static final long serialVersionUID = -7422615160721780122L;

    private Date currentHour;
    private Integer totalCount;
    private String tag;

    public StatisticsTags() {
    }

    public StatisticsTags(Long id) {
        super(id);
    }

    /**
     * @return 小时
     */
    public Date getCurrentHour() {
        return currentHour;
    }

    /**
     * @param currentHour 小时
     */
    public void setCurrentHour(Date currentHour) {
        this.currentHour = currentHour;
    }
    /**
     * @return 记录数
     */
    public Integer getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount 记录数
     */
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
    /**
     * @return 标签
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag 标签
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

}
