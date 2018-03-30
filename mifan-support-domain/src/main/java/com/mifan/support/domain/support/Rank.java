/**
 * 
 */
package com.mifan.support.domain.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZYW
 *
 */
public class Rank implements Serializable {

    private static final long serialVersionUID = 2149909758925774920L;

    private Long topicId;

    private Double score;

    private String title;

    public Rank() {
        super();
    }

    public Rank(Long topicId, Double score) {
        super();
        this.topicId = topicId;
        this.score = score;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public enum RankType {
        DAY("day_ranking", "日排行"),

        WEEK("week_ranking", "周排行"),

        MONTH("month_ranking", "月排行"),

        ALL("all_ranking", "总排行");


        private String key;
        private final String description;
        
        RankType(String key, String description) {
            this.key = key;
            this.description = description;
        }
        private static Map<String, RankType> map = new HashMap<>();
        static {
            for (RankType rankType : RankType.values()) {
                map.put(rankType.getKey(), rankType);
            }
        }

        public static RankType getRankType(String key) {
            return map.get(key);
        }
        
        
        public String getKey() {
            return key;
        }

        public String getDescription() {
            return description;
        }
        
    }

}
