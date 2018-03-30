package com.mifan.support.domain.support;

/**
 * @author ZYW
 *
 */
public class TopicsClustering {
	
	public static final String TOPIC_ID = "topic_id";
    public static final String TARGET_ID = "target_id";
    public static final String ENABLED = "enabled";
    public static final String TYPE = "type";
	
    private Long topicId;
    private Long targetId;
    private Integer type;
    
    
	public Long getTopicId() {
		return topicId;
	}
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}
	public Long getTargetId() {
		return targetId;
	}
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
    
	public enum ClusteringType {

        ROBOT(0, "机器学习类型"),
        PEOPLE(1, "人工干预");

        private final int index;
        private final String name;

        ClusteringType(int index, String name) {
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
