package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-05
 */
public class VotesOption extends BaseEntity {

    public static final String TABLE_NAME = "votes_option";

    public static final String VOTE_ID = "vote_id";
    public static final String VOTE_OPTION_TEXT = "vote_option_text";
    public static final String VOTE_OPTION_COUNT = "vote_option_count";
    public static final String DISPLAY_ORDER = "display_order";

    private static final long serialVersionUID = 1842147798977737153L;

    private Long voteId;
    private String voteOptionText;
    private Integer voteOptionCount;
    private Integer displayOrder;

    public VotesOption() {
    }

    public VotesOption(Long id) {
        super(id);
    }

    /**
     * @return 投票ID
     */
    public Long getVoteId() {
        return voteId;
    }

    /**
     * @param voteId 投票ID
     */
    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }

    /**
     * @return 选项描述
     */
    public String getVoteOptionText() {
        return voteOptionText;
    }

    /**
     * @param voteOptionText 选项描述
     */
    public void setVoteOptionText(String voteOptionText) {
        this.voteOptionText = voteOptionText;
    }

    /**
     * @return 选项票数
     */
    public Integer getVoteOptionCount() {
        return voteOptionCount;
    }

    /**
     * @param voteOptionCount 选项票数
     */
    public void setVoteOptionCount(Integer voteOptionCount) {
        this.voteOptionCount = voteOptionCount;
    }

    /**
     * @return 选项排序
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /**
     * @param displayOrder 选项排序
     */
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

}
