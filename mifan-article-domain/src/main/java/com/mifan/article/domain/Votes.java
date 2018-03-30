package com.mifan.article.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;

import java.util.Date;
import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-05
 */
public class Votes extends BaseEntity {

    //系统默认VOTE
    public static final long SYSTEM_VOTE_REPORT = 1L;
    public static final long TRANSLATE_REPORT = 2L;

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(Votes.ID)
            .add(Votes.VOTE_TYPE)
            .add(Votes.VOTE_TEXT)
            .add(Votes.VOTE_START)
            .add(Votes.VOTE_LENGTH)
            .add(BaseEntity.ENABLED)
            .build();

    public static final String TABLE_NAME = "votes";

    public static final String VOTE_TYPE = "vote_type";
    public static final String VOTE_TEXT = "vote_text";
    public static final String VOTE_START = "vote_start";
    public static final String VOTE_LENGTH = "vote_length";

    private static final long serialVersionUID = 2086126123733682339L;

    private String voteType;
    private String voteText;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date voteStart;
    private Integer voteLength;

    private List<VotesOption> options;

    public Votes() {
    }

    public Votes(Long id) {
        super(id);
    }

    /**
     * @return 投票类型: 复选框, 单选
     */
    public String getVoteType() {
        return voteType;
    }

    /**
     * @param voteType 投票类型: 复选框, 单选
     */
    public void setVoteType(String voteType) {
        this.voteType = voteType;
    }

    /**
     * @return 投票描述
     */
    public String getVoteText() {
        return voteText;
    }

    /**
     * @param voteText 投票描述
     */
    public void setVoteText(String voteText) {
        this.voteText = voteText;
    }

    /**
     * @return 投票开始日期
     */
    public Date getVoteStart() {
        return voteStart;
    }

    /**
     * @param voteStart 投票开始日期
     */
    public void setVoteStart(Date voteStart) {
        this.voteStart = voteStart;
    }

    /**
     * @return 投票期限 0:无限制 (单位天)
     */
    public Integer getVoteLength() {
        return voteLength;
    }

    /**
     * @param voteLength 投票期限 0:无限制 (单位天)
     */
    public void setVoteLength(Integer voteLength) {
        this.voteLength = voteLength;
    }

    public List<VotesOption> getOptions() {
        return options;
    }

    public void setOptions(List<VotesOption> options) {
        this.options = options;
    }
}
