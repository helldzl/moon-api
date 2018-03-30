package com.mifan.batch.spider.domain;

import java.util.Date;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/10
 */
public class Seeds {

    private Long id;

    private String url;
    private String source;
    private String conf;
    private String name;
    private String description;
    private Integer rank;
    private Integer updateRate;
    private Integer enabled;
    private Long creator;
    private Long modifier;
    private Date created;
    private Date modified;
    private String charset;
    private String agencyIp;
    private Integer agencyIpPort;

    //
    private String host;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

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

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getUpdateRate() {
        return updateRate;
    }

    public void setUpdateRate(Integer updateRate) {
        this.updateRate = updateRate;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Long getModifier() {
        return modifier;
    }

    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getAgencyIp() {
        return agencyIp;
    }

    public void setAgencyIp(String agencyIp) {
        this.agencyIp = agencyIp;
    }

    public Integer getAgencyIpPort() {
        return agencyIpPort;
    }

    public void setAgencyIpPort(Integer agencyIpPort) {
        this.agencyIpPort = agencyIpPort;
    }
}
