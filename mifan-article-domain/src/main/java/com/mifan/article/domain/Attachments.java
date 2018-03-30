package com.mifan.article.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static com.mifan.article.domain.Attachments.ContentType.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class Attachments extends BaseEntity {

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(Attachments.ID)
            .add(Attachments.MIME)
            .add(Attachments.FILENAME)
            .add(Attachments.DESCRIPTION)
            .add(Attachments.EXTRA)
            .add(Attachments.GROUP_ID)
            .build();

    public static final int DEFAULT_RETRY = 3;

    public static final String TABLE_NAME = "attachments";

    public static final String MIME = "mime";
    public static final String FILENAME = "filename";
    public static final String EXTENSION = "extension";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String EXTRA = "extra";
    public static final String FILESIZE = "filesize";
    public static final String DOWNLOAD = "download";
    public static final String RETRY = "retry";
    public static final String GROUP_ID = "group_id";

    private static final long serialVersionUID = 2516575977746212753L;

    private String mime;
    private String filename;
    private String extension;
    private String title;
    private String description;
    private String extra;
    private Integer filesize;
    private Integer download;
    private Integer retry;
    private Integer groupId;

    // 文件字节流
    private byte[] bytes;
    private AttachmentsFetch from;

    //上传文件
    private MultipartFile file;

    public Attachments() {
    }

    public Attachments(Long id) {
        super(id);
    }

    /**
     * @return MIME类型
     */
    public String getMime() {
        return mime;
    }

    /**
     * @param mime MIME类型
     */
    public void setMime(String mime) {
        this.mime = mime;
    }

    /**
     * @return 附件名称
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename 附件名称
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return 扩展名称
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @param extension 扩展名称
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * @return 附件标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 附件标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return 附件描述
     */
    @JsonIgnore
    public String getDescription() {
        return description;
    }

    /**
     * @param description 附件描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return 额外信息
     */
    @JsonIgnore
    public String getExtra() {
        return extra;
    }

    /**
     * @param extra 额外信息
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }

    /**
     * @return 附件大小
     */
    public Integer getFilesize() {
        return filesize;
    }

    /**
     * @param filesize 附件大小
     */
    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
    }

    /**
     * @return 下载次数
     */
    public Integer getDownload() {
        return download;
    }

    /**
     * @param download 下载次数
     */
    public void setDownload(Integer download) {
        this.download = download;
    }

    /**
     * @return 重试次数
     */
    @JsonIgnore
    public Integer getRetry() {
        return retry;
    }

    /**
     * @param retry 重试次数
     */
    public void setRetry(Integer retry) {
        this.retry = retry;
    }

    /**
     * @return 分组
     */
    @JsonIgnore
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * @param groupId 分组
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public AttachmentsFetch getFrom() {
        return from;
    }

    public void setFrom(AttachmentsFetch from) {
        this.from = from;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public enum MediaType {

        ROTATION(ROTATIONS, null),

        GIF(IMAGES, "image/gif"),
        IEF(IMAGES, "image/ief"),
        JPE(IMAGES, "image/jpeg"),
        JPEG(IMAGES, "image/jpeg"),
        JPG(IMAGES, "image/jpeg"),

        MP2(VIDEOS, "video/mpeg"),
        MPA(VIDEOS, "video/mpeg"),
        MPE(VIDEOS, "video/mpeg"),
        MPEG(VIDEOS, "video/mpeg"),
        MPG(VIDEOS, "video/mpeg"),
        MPV2(VIDEOS, "video/mpeg"),
        MOV(VIDEOS, "video/quicktime"),
        QT(VIDEOS, "video/quicktime"),
        LSF(VIDEOS, "video/x-la-asf"),
        LSX(VIDEOS, "video/x-la-asf"),
        ASF(VIDEOS, "video/x-la-asf"),
        ASR(VIDEOS, "video/x-la-asf"),
        ASX(VIDEOS, "video/x-la-asf"),
        AVI(VIDEOS, "video/x-msvideo"),
        MOVIE(VIDEOS, "video/x-sgi-movie"),

        AU(AUDIOS, "audio/basic"),
        SND(AUDIOS, "audio/basic"),
        MID(AUDIOS, "audio/mid"),
        RMI(AUDIOS, "audio/mid"),
        MP3(AUDIOS, "audio/mpeg"),
        AIF(AUDIOS, "audio/x-aiff"),
        AIFC(AUDIOS, "audio/x-aiff"),
        AIFF(AUDIOS, "audio/x-aiff"),
        M3U(AUDIOS, "audio/x-mpegurl"),
        RA(AUDIOS, "audio/x-pn-realaudio"),
        RAM(AUDIOS, "audio/x-pn-realaudio"),
        WAV(AUDIOS, "audio/x-wav");

        private static Map<String, MediaType> map = new HashMap<>();

        static {
            for (MediaType mediaType : MediaType.values()) {
                if (mediaType.mime != null) {
                    map.put(mediaType.mime, mediaType);
                }
            }
        }

        public static MediaType from(String mime, int group) {
            if (group == 36 && mime.startsWith("image")) {
                return MediaType.ROTATION;
            }
            MediaType mediaType = map.get(mime);
            if (mediaType == null) {
                throw new IllegalArgumentException(String.format("Unknown mime type : %s", mime));
            }
            return mediaType;
        }

        private final ContentType contentType;
        private final String mime;

        MediaType(ContentType contentType, String mime) {
            this.contentType = contentType;
            this.mime = mime;
        }

        public ContentType getContentType() {
            return contentType;
        }

        public String getMime() {
            return mime;
        }
    }

    public enum ContentType {

        ROTATIONS("rotations"),
        IMAGES("images"),
        AUDIOS("audios"),
        VIDEOS("videos");

        private final String name;

        ContentType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
