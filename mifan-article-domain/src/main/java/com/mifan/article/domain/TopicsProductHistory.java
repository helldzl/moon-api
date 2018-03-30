package com.mifan.article.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.moonframework.model.mybatis.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class TopicsProductHistory extends BaseEntity {

    public static final String TABLE_NAME = "topics_product_history";

    public static final String TOPIC_ID = "topic_id";
    public static final String PRICE = "price";
    public static final String EFFECTIVE_DATE = "effective_date";

    private static final long serialVersionUID = 2148005741436614161L;

    private Long topicId;
    private BigDecimal price;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date effectiveDate;

    public TopicsProductHistory() {
    }

    public TopicsProductHistory(Long id) {
        super(id);
    }

    /**
     * @return 主题ID
     */
    public Long getTopicId() {
        return topicId;
    }

    /**
     * @param topicId 主题ID
     */
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    /**
     * @return 价格
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price 价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @return 生效时间
     */
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * @param effectiveDate 生效时间
     */
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

}
