package com.mifan.batch.spider.domain;


import java.util.Date;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class Attachments {
    public static final int DEFAULT_RETRY = 3;
    private Long id;
    private String origin;
    private Long originHash;
    private String mime;
    private String filename;
    private String extension;
    private String title;
    private String description;
    private String extra;
    private Integer filesize;
    private Integer download;
    private Integer retry=0;
    private Integer groupId;
    private Integer enabled;
    private Long creator;
    private Long modifier;
    private Date created;
    private Date modified;
    private String agencyIp;
    private Integer agencyIpPort;


    private byte[] bytes;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Long getOriginHash() {
        return originHash;
    }

    public void setOriginHash(Long originHash) {
        this.originHash = originHash;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtra() {
        return extra;
    }


    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Integer getFilesize() {
        return filesize;
    }

    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
    }

    public Integer getDownload() {
        return download;
    }

    public void setDownload(Integer download) {
        this.download = download;
    }

    public Integer getRetry() {
        return retry;
    }

    public void setRetry(Integer retry) {
        this.retry = retry;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
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
