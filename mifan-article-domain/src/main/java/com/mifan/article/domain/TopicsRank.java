package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-08-23
 */
public class TopicsRank extends BaseEntity {

    public static final String TABLE_NAME = "topics_rank";

    public static final String RANK_TYPE = "rank_type";
    public static final String FORUM_ID = "forum_id";
    public static final String TOPIC_ID = "topic_id";
    public static final String SCORE = "score";

    private static final long serialVersionUID = 4891699452739723215L;

    private Integer rankType;
    private Long forumId;
    private Long topicId;
    private Long score;

    public TopicsRank() {
    }

    public TopicsRank(Long id) {
        super(id);
    }

    public RankType rankType() {
        if (this.rankType == null) {
            return null;
        }
        return RankType.from(this.rankType);
    }

    public String getRankTypeValue() {
        RankType rankType = rankType();
        if (rankType == null) {
            return null;
        } else {
            return rankType.getName();
        }
    }

    /**
     * @return 1:HOT, 2:NEW, 3:BEST
     */
    public Integer getRankType() {
        return rankType;
    }

    /**
     * @param rankType 1:HOT, 2:NEW, 3:BEST
     */
    public void setRankType(Integer rankType) {
        this.rankType = rankType;
    }

    /**
     * @return forum ID
     */
    public Long getForumId() {
        return forumId;
    }

    /**
     * @param forumId forum ID
     */
    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }

    /**
     * @return topic ID
     */
    public Long getTopicId() {
        return topicId;
    }

    /**
     * @param topicId topic ID
     */
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    /**
     * @return (currentTimeMillis / 1000L) + (86400 * n(days))
     */
    public Long getScore() {
        return score;
    }

    /**
     * @param score (currentTimeMillis / 1000L) + (86400 * n(days))
     */
    public void setScore(Long score) {
        this.score = score;
    }

    public enum RankType {

        HOT(1, "hot"),
        NEW(2, "new"),
        BEST(3, "best");

        private static Map<String, RankType> nameMap = new HashMap<>();
        private static Map<Integer, RankType> indexMap = new HashMap<>();

        static {
            for (RankType rankType : RankType.values()) {
                nameMap.put(rankType.getName(), rankType);
                indexMap.put(rankType.getIndex(), rankType);
            }
        }

        public static RankType from(String name) {
            return nameMap.get(name);
        }

        public static RankType from(int index) {
            return indexMap.get(index);
        }

        private final int index;
        private final String name;

        RankType(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }

}
