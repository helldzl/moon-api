package com.mifan.article.domain;

import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.validation.ValidationGroups.Patch;
import org.moonframework.validation.ValidationGroups.Post;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class Seeds extends BaseEntity {

    private static final long serialVersionUID = 8680548094930403437L;

    public static final String TABLE_NAME = "seeds";

    public static final String CHANNEL_ID = "channel_id";
    public static final String URL = "url";
    public static final String SOURCE = "source";
    public static final String CONF = "conf";
    public static final String NAME = "name";
    public static final String AGENCY_IP = "agency_ip";
    public static final String AGENCY_IP_PORT = "agency_ip_port";
    public static final String CHARSET = "charset";
    public static final String DESCRIPTION = "description";
    public static final String RANK = "rank";
    public static final String UPDATE_RATE = "update_rate";


    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(ID)
            .add(URL)
            .add(SOURCE)
            .add(NAME)
            .add(AGENCY_IP)
            .add(AGENCY_IP_PORT)
            .add(CHARSET)
            .add(DESCRIPTION)
            .add(RANK)
            .add(UPDATE_RATE)
            .add(ENABLED)
            .add(CREATOR)
            .add(MODIFIER)
            .add(CREATED)
            .add(MODIFIED)
            .build();

    @NotNull(groups = {Post.class, Patch.class}, message = "{NotNull.Seeds.url}")
    private String url;

    @NotNull(groups = {Post.class, Patch.class}, message = "{NotNull.Seeds.source}")
    private String source;

    @NotNull(groups = {Post.class, Patch.class}, message = "{NotNull.Seeds.conf}")
    private String conf;

    private String name;
    private String agencyIp;
    private Integer agencyIpPort;
    private String charset;
    private String description;
    private Integer rank;
    private Integer updateRate;

    @NotNull(groups = {Post.class, Patch.class}, message = "{NotNull.Seeds.language}")
    @Range(groups = {Post.class, Patch.class}, message = "{Range.Seeds.language}", min = 1, max = 2)
    private Integer language;

    //
    private String host;
    private Channels channel;

    public static void putSource(Map<String, Object> attributes, Seeds seed) {
        putSource(attributes, seed, null);
    }

    public static void putSource(Map<String, Object> attributes, Seeds seed, Consumer<Map<String, Object>> consumer) {
        if (seed != null) {
            Map<String, Object> origin = new HashMap<>(16);
            origin.put("seedId", seed.getId());
            origin.put("host", seed.getHost());

            Channels channel = seed.getChannel();
            if (channel != null) {
                origin.put("source", channel.getChannelName());
                origin.put("image", channel.getChannelImage());
                origin.put("channelId", channel.getId());
            }

            if (consumer != null) {
                consumer.accept(origin);
            }
            attributes.put("from", origin);
        }
    }

    public Seeds() {
    }

    public Seeds(Long id) {
        super(id);
    }

    /**
     * @return 网站URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url 网站URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return 来源描述
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source 来源描述
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return
     */
    public String getConf() {
        return conf;
    }

    /**
     * @param conf
     */
    public void setConf(String conf) {
        this.conf = conf;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return
     */
    public String getAgencyIp() {
        return agencyIp;
    }

    /**
     * @param agencyIp
     */
    public void setAgencyIp(String agencyIp) {
        this.agencyIp = agencyIp;
    }

    /**
     * @return
     */
    public Integer getAgencyIpPort() {
        return agencyIpPort;
    }

    /**
     * @param agencyIpPort
     */
    public void setAgencyIpPort(Integer agencyIpPort) {
        this.agencyIpPort = agencyIpPort;
    }

    /**
     * @return
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return 网站PageRank值
     */
    public Integer getRank() {
        return rank;
    }

    /**
     * @param rank 网站PageRank值
     */
    public void setRank(Integer rank) {
        this.rank = rank;
    }

    /**
     * @return 网站更新频率的度量值
     */
    public Integer getUpdateRate() {
        return updateRate;
    }

    /**
     * @param updateRate 网站更新频率的度量值
     */
    public void setUpdateRate(Integer updateRate) {
        this.updateRate = updateRate;
    }

    /**
     * @return 语言 0:缺省, 1:中文, 2英文
     */
    public Integer getLanguage() {
        return language;
    }

    /**
     * @param language 语言 0:缺省, 1:中文, 2英文
     */
    public void setLanguage(Integer language) {
        this.language = language;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Channels getChannel() {
        return channel;
    }

    public void setChannel(Channels channel) {
        this.channel = channel;
    }
}
