package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class UsersChannelsWatch extends BaseEntity {

    public static final String TABLE_NAME = "users_channels_watch";

    public static final String USER_ID = "user_id";
    public static final String CHANNEL_ID = "channel_id";
    public static final String DISPLAY_ORDER = "display_order";

    private static final long serialVersionUID = -5642123449971322114L;

    private Long userId;
    private Long channelId;
    private Integer displayOrder;

    public UsersChannelsWatch() {
    }

    public UsersChannelsWatch(Long id) {
        super(id);
    }

    /**
     * @return 
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId 
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    /**
     * @return 
     */
    public Long getChannelId() {
        return channelId;
    }

    /**
     * @param channelId 
     */
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
    /**
     * @return 
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /**
     * @param displayOrder 
     */
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

}
