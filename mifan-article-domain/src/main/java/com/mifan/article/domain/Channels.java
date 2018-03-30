package com.mifan.article.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.annotation.Join;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.validation.ValidationGroups.Post;

import javax.sound.midi.Patch;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class Channels extends BaseEntity {

    private static final long serialVersionUID = 6460749709777913866L;

    public static final String TABLE_NAME = "channels";

    public static final String TARGET_ID = "target_id";
    public static final String CHANNEL_SOURCE = "channel_source";
    public static final String CHANNEL_TYPE = "channel_type";
    public static final String CHANNEL_NAME = "channel_name";
    public static final String CHANNEL_IMAGE = "channel_image";
    public static final String FILTER = "filter";
    public static final String DESCRIPTION = "description";
    public static final String WATCHED = "watched";
    public static final String DISPLAY_ORDER = "display_order";
    public static final String CANCELLABLE = "cancellable";
    public static final String REVERSE = "reverse";

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(ID)
            .add(TARGET_ID)
            .add(CHANNEL_NAME)
            .add(CHANNEL_SOURCE)
            .add(CHANNEL_TYPE)
            .add(CHANNEL_IMAGE)
            .add(FILTER)
            .add(DESCRIPTION)
            .add(WATCHED)
            .add(DISPLAY_ORDER)
            .add(CANCELLABLE)
            .add(ENABLED)
            .add(CREATOR)
            .add(MODIFIER)
            .add(CREATED)
            .add(MODIFIED)
            .build();

    public static Iterable<? extends Field> JOIN_FIELDS = Fields.builder()
            .add(UsersChannelsWatch.TABLE_NAME + "." + UsersChannelsWatch.DISPLAY_ORDER)
            .add(TABLE_NAME + "." + ID)
            .add(TABLE_NAME + "." + TARGET_ID)
            .add(TABLE_NAME + "." + CHANNEL_NAME)
            .add(TABLE_NAME + "." + CHANNEL_SOURCE)
            .add(TABLE_NAME + "." + CHANNEL_TYPE)
            .add(TABLE_NAME + "." + CHANNEL_IMAGE)
            .add(TABLE_NAME + "." + FILTER)
            .add(TABLE_NAME + "." + DESCRIPTION)
            .add(TABLE_NAME + "." + WATCHED)
            .add(TABLE_NAME + "." + DISPLAY_ORDER)
            .add(TABLE_NAME + "." + CANCELLABLE)
            .build();

    /**
     * 设置JOIN表关联
     */
    @Join(table = UsersChannelsWatch.TABLE_NAME, condition = TABLE_NAME + "." + BaseEntity.ID + " = " + UsersChannelsWatch.TABLE_NAME + "." + UsersChannelsWatch.CHANNEL_ID)
    private Long id;
    private Long targetId;
    @Range(min = 0, max = 2, groups = {Post.class, Patch.class}, message = "{Error.Channels.channelSource}")
    private Integer channelSource;
    @NotNull(groups = {Post.class, Patch.class}, message = "{NotNull.Channels.channelType}")
    @Range(min = 1, max = 5, groups = {Post.class, Patch.class}, message = "{Error.Channels.channelType}")
    private Integer channelType;
    @NotBlank(groups = {Post.class, Patch.class}, message = "NotNull.Channels.channelName")
    private String channelName;
    private String channelImage;
    private String filter;
    private String description;
    private Integer watched;
    private Integer displayOrder;
    private Integer cancellable;
    private Integer reverse;

    //
    private boolean subscribed;
    private String host;
    private List<Long> targets;

    public Channels() {
    }

    public Channels(Long id) {
        super(id);
    }

    //

    public String getChannelSourceValue() {
        if (this.channelSource == null) {
            return null;
        }

        ChannelSource source = ChannelSource.from(this.channelSource);

        if (source == null) {
            return null;
        }

        return source.getValue();
    }

    public void channelSourceValue(ChannelSource channelSource) {
        this.channelSource = channelSource.getIndex();
    }

    //

    public String getChannelTypeValue() {
        if (this.channelType == null) {
            return null;
        }

        ChannelType type = ChannelType.from(this.channelType);

        if (type == null) {
            return null;
        }

        return type.getValue();
    }

    public void channelTypeValue(ChannelType channelType) {
        this.channelType = channelType.getIndex();
    }

    /**
     * @return
     */
    public Long getTargetId() {
        return targetId;
    }

    /**
     * @param targetId
     */
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    /**
     * @return 频道来源 0:默认, 1:国内频道, 2:国外频道
     */
    public Integer getChannelSource() {
        return channelSource;
    }

    /**
     * @param channelSource 频道来源 0:默认, 1:国内频道, 2:国外频道
     */
    public void setChannelSource(Integer channelSource) {
        this.channelSource = channelSource;
    }

    /**
     * @return 1:版面频道, 2:标签频道, 3:用户频道, 4:来源频道
     */
    public Integer getChannelType() {
        return channelType;
    }

    /**
     * @param channelType 1:版面频道, 2:标签频道, 3:用户频道, 4:来源频道
     */
    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    /**
     * @return 频道名称
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * @param channelName 频道名称
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /**
     * @return
     */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public String getChannelImage() {
        return channelImage;
    }

    /**
     * @param channelImage
     */
    public void setChannelImage(String channelImage) {
        this.channelImage = channelImage;
    }

    /**
     * @return 过滤参数
     * @deprecated use elastic_function_score and elastic_query_builder
     */
    @Deprecated
    public String getFilter() {
        return filter;
    }

    /**
     * @param filter 过滤参数
     */
    @Deprecated
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * @return 频道描述
     */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public String getDescription() {
        return description;
    }

    /**
     * @param description 频道描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return
     */
    public Integer getWatched() {
        return watched;
    }

    /**
     * @param watched
     */
    public void setWatched(Integer watched) {
        this.watched = watched;
    }

    /**
     * @return
     */
    public Integer getCancellable() {
        return cancellable;
    }

    @JsonIgnore
    public Integer getReverse() {
        return reverse;
    }

    public void setReverse(Integer reverse) {
        this.reverse = reverse;
    }

    /**
     * @param cancellable
     */
    public void setCancellable(Integer cancellable) {
        this.cancellable = cancellable;
        if (cancellable != null) {
            this.reverse = cancellable == 0 ? 1 : 0;
        } else {
            this.reverse = null;
        }
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<Long> getTargets() {
        return targets;
    }

    public void setTargets(List<Long> targets) {
        this.targets = targets;
    }

    public enum ChannelSource {

        DEFAULT(0, "默认"),
        INTERNAL(1, "国内频道"),
        EXTERNAL(2, "国外频道");

        private static Map<Integer, ChannelSource> map = new HashMap<>();

        static {
            for (ChannelSource channelSource : ChannelSource.values()) {
                map.put(channelSource.getIndex(), channelSource);
            }
        }

        public static ChannelSource from(int index) {
            return map.get(index);
        }

        private final int index;
        private final String value;

        ChannelSource(int index, String value) {
            this.index = index;
            this.value = value;
        }

        public int getIndex() {
            return index;
        }

        public String getValue() {
            return value;
        }
    }

    public enum ChannelType {

        FORUM(1, "版面频道"),

        LABEL(2, "标签频道"),

        USER(3, "用户频道"),

        FROM(4, "来源频道"),

        RANK(5, "排名频道");

        private static Map<Integer, ChannelType> map = new HashMap<>();

        static {
            for (ChannelType channelType : ChannelType.values()) {
                map.put(channelType.getIndex(), channelType);
            }
        }

        public static ChannelType from(int index) {
            return map.get(index);
        }

        private final int index;
        private final String value;

        ChannelType(int index, String value) {
            this.index = index;
            this.value = value;
        }

        public int getIndex() {
            return index;
        }

        public String getValue() {
            return value;
        }
    }

}
