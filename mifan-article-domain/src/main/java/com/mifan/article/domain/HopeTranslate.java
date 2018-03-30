package com.mifan.article.domain;

import static java.util.stream.Collectors.toList;

import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Post;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-09-05
 */
public class HopeTranslate extends BaseEntity {

    public static final String TABLE_NAME = "hope_translate";

    public static final String USER_ID = "user_id";
    public static final String EXTEND_ID = "extend_id";
    public static final String WHY = "why";
    public static final String VOTES_OPTION_IDS = "votes_option_ids";

    private static final long serialVersionUID = -2683237419051320506L;

    @NotNull(groups = {Post.class}, message = "{NotNull.HopeTranslate.userId}")
    private Long userId;
    private Long extendId;
    @NotNull(groups = {Post.class}, message = "{NotNull.HopeTranslate.why}")
    private String why;
    private String votesOptionIds;
    
    private Long[] options;
    
    @NotNull(groups = {Post.class}, message = "{NotNull.HopeTranslate.extend}")
    @Valid
    private HopeTranslateExtend extend;//扩展信息
    

    public HopeTranslate() {
    }

    public HopeTranslate(Long id) {
        super(id);
    }

    /**
     * @return 用户标识
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId 用户标识
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    /**
     * @return 
     */
    public Long getExtendId() {
        return extendId;
    }

    /**
     * @param extendId 
     */
    public void setExtendId(Long extendId) {
        this.extendId = extendId;
    }
    /**
     * @return 这篇文章对你有哪些帮助
     */
    public String getWhy() {
        return why;
    }

    /**
     * @param why 这篇文章对你有哪些帮助
     */
    public void setWhy(String why) {
        this.why = why;
    }
    /**
     * @return 要求翻译的部分
     */
    public String getVotesOptionIds() {
        return votesOptionIds;
    }

    /**
     * @param votesOptionIds 要求翻译的部分
     */
    public void setVotesOptionIds(String votesOptionIds) {
        this.votesOptionIds = votesOptionIds;
    }

    public HopeTranslateExtend getExtend() {
        return extend;
    }

    public void setExtend(HopeTranslateExtend extend) {
        this.extend = extend;
    }

    public Long[] getOptions() {
        return options;
    }

    public void setOptions(Long[] options) {
        this.options = options;
        if (options != null && options.length != 0) {
            this.votesOptionIds = String.join(",", Stream.of(options).map(Object::toString).collect(toList()));
        } else {
            this.votesOptionIds = null;
        }
    }

}
