package com.mifan.user.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pair;
import org.moonframework.model.mybatis.service.Services;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-03-31
 */
public class UserKarmaLogs extends BaseEntity {

    public static final String TABLE_NAME = "user_karma_logs";

    public static final String USER_ID = "user_id";
    public static final String SCORE = "score";
    public static final String ACTION = "action";

    private static final long serialVersionUID = 7140411220496373756L;

    @NotNull(groups = Post.class, message = "{Null.UserKarmaLogs.userId}")
    private Long userId;

    // @Null(groups = Post.class, message = "{Null.UserKarmaLogs.score}")
    private Integer score;

    // @Null(groups = Post.class, message = "{Null.UserKarmaLogs.action}")
    private String action;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Null(groups = Post.class, message = "{Null.UserKarmaLogs.created}")
    private Date created;

    private Event event;

    public UserKarmaLogs() {
    }

    public UserKarmaLogs(Long id) {
        super(id);
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

        SIGN(1, "每日签到") {
            @Override
            public int doService(UserKarmaLogs log) {
                if (log.getScore() < 0) {
                    throw new IllegalArgumentException();
                }

                // local
                LocalDateTime dateTime = LocalDateTime.now();
                LocalDate now = dateTime.toLocalDate();
                LocalDate yesterday = now.minusDays(1);

                //
                Pair.PairBuilder increments = Pair.builder();
                Pair.PairBuilder fields = Pair.builder();

                // 在事务中获取行锁, 这里更新最后更新时间, 保证在多线程下不会提交多次
                UserProfiles lock = new UserProfiles(log.getUserId());
                Services.update(UserProfiles.class, lock);

                UserProfiles profile = Services.findOne(UserProfiles.class, log.getUserId(), Fields.builder()
                        .add(BaseEntity.ID)
                        .add(UserProfiles.SIGN_COUNT_CONTINUOS)
                        .add(UserProfiles.SIGN_TIME)
                        .build());

                //
                if (profile.getSignTime() != null) {
                    LocalDate lastSignDate = LocalDateTime.ofInstant(profile.getSignTime().toInstant(), ZoneId.systemDefault()).toLocalDate();
                    if (lastSignDate.equals(now)) {
                        // 如果是今天, 说明已经签到, 抛出异常.
                        throw new IllegalStateException("今天已签到!");
                    } else if (lastSignDate.equals(yesterday)) {
                        // 如果是昨天说明是连续签到, 判断是否大于等于7, 积分翻倍.
                        if (profile.getSignCountContinuos() >= 7) {
                            log.setAction(CONTINUATION);
                        }
                        increments.add(UserProfiles.SIGN_COUNT_CONTINUOS, 1);
                    } else {
                        // 否则重置连续签到 -> 1
                        fields.add(UserProfiles.SIGN_COUNT_CONTINUOS, 1);
                    }
                } else {
                    increments.add(UserProfiles.SIGN_COUNT_CONTINUOS, 1);
                }

                // update
                increments.add(UserProfiles.USER_KARMA, log.getScore()).add(UserProfiles.SIGN_COUNT, 1);
                fields.add(UserProfiles.SIGN_TIME, Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
                return Services.update(UserProfiles.class, log.getUserId(), increments.build(), fields.build());
            }
        },

        CONTINUATION(2, "连续签到") {
            @Override
            public int doService(UserKarmaLogs log) {
                throw new UnsupportedOperationException();
            }
        },

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

        public int doService(UserKarmaLogs log) {
            if (log.getScore() < 0) {
                UserProfiles lock = new UserProfiles(log.getUserId());
                Services.update(UserProfiles.class, lock);

                UserProfiles profile = Services.findOne(UserProfiles.class, log.getUserId(), Fields.builder()
                        .add(BaseEntity.ID)
                        .add(UserProfiles.USER_KARMA)
                        .build());

                if (profile.getUserKarma() + log.getScore() < 0) {
                    throw new IllegalStateException(String.format("o(╥﹏╥)o 对不起, 您的积分不足, 当前积分:[%s], 需要积分:[%s]", profile.getUserKarma(), -log.getScore()));
                }
            }
            return Services.update(UserProfiles.class, log.getUserId(), Pair.builder().add(UserProfiles.USER_KARMA, log.getScore()).build(), null);
        }
    }

}
