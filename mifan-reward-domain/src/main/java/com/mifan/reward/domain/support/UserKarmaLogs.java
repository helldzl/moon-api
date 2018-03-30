package com.mifan.reward.domain.support;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

public class UserKarmaLogs {

    public static final String TABLE_NAME = "user_karma_logs";

    public static final String USER_ID = "user_id";
    public static final String SCORE = "score";
    public static final String ACTION = "action";

    private static final long serialVersionUID = 7140411220496373756L;

    private Long id;

    @Range(min = 0, max = 9)
    private Integer enabled;

    private Long creator;

    private Long modifier;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modified;

    private Long userId;

    private Integer score;

    private String action;

    private Event event;

    public UserKarmaLogs() {
    }

    public UserKarmaLogs(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    /**
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return 积分
     */
    public Integer getScore() {
        return score;
    }

    /**
     * @param score 积分
     */
    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     * @return 行为
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action 行为
     */
    public void setAction(String action) {
        this.action = action;
    }

    public void setAction(Event event) {
        if (event.getScore() != 0) {
            this.score = event.getScore();
        }
        this.action = event.getAction();
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        setAction(event);
    }

    public enum Event {

        SIGN(1, "每日签到"),

        CONTINUATION(2, "连续签到"),

        REGISTRATION(10, "注册"),

        INFORMATION(10, "完善资料"),

        INVITATION(5, "邀请好友"),

        CONSUMPTION(10, "消费"),

        AWARD(-10, "抽奖扣除"),

        TRANSLATE(0, "翻译奖励");

        private final int score;
        private final String action;

        Event(int score, String action) {
            this.score = score;
            this.action = action;
        }

        public int getScore() {
            return score;
        }

        public String getAction() {
            return action;
        }

    }

}
