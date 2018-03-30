package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-13
 */
public class ChannelsSeeds extends BaseEntity {

    public static final String TABLE_NAME = "channels_seeds";

    public static final String CHANNEL_ID = "channel_id";
    public static final String SEED_ID = "seed_id";

    private static final long serialVersionUID = 7773740485904941185L;

    private Long channelId;
    private Long seedId;

    public ChannelsSeeds() {
    }

    public ChannelsSeeds(Long id) {
        super(id);
    }

    public ChannelsSeeds(Long channelId, Long seedId) {
        super();
        this.channelId = channelId;
        this.seedId = seedId;
    }

    /**
     * @return channelId
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
     * @return seedId
     */
    public Long getSeedId() {
        return seedId;
    }

    /**
     * @param seedId
     */
    public void setSeedId(Long seedId) {
        this.seedId = seedId;
    }

}
