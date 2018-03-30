package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2018-01-18
 */
public class SystemResourceLock extends BaseEntity {

    public static final String TABLE_NAME = "system_resource_lock";

    public static final String RESOURCE_TYPE = "resource_type";
    public static final String TARGET_ID = "target_id";
    public static final String VERSION = "version";

    private static final long serialVersionUID = 6299309544964269543L;

    private Integer resourceType;
    private Long targetId;
    private Integer version;

    public SystemResourceLock() {
    }

    public SystemResourceLock(Long id) {
        super(id);
    }

    public SystemResourceLock(ResourceType resourceType, Long targetId) {
        this.resourceType = resourceType.getIndex();
        this.targetId = targetId;
    }

    public ResourceType resourceType() {
        return ResourceType.from(resourceType);
    }

    /**
     * @return
     */
    public Integer getResourceType() {
        return resourceType;
    }

    /**
     * @param resourceType
     */
    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType.getIndex();
    }

    /**
     * @return
     */
    public Long getTargetId() {
        return targetId;
    }

    /**
     * @param targetId
     */
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    /**
     * @return
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    public enum ResourceType {

        USERS("users", 1);

        private static Map<Integer, ResourceType> map = new HashMap<>();

        static {
            for (ResourceType resourceType : ResourceType.values()) {
                map.put(resourceType.getIndex(), resourceType);
            }
        }

        public static ResourceType from(Integer index) {
            return map.get(index);
        }

        private final String name;
        private final int index;

        ResourceType(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }
    }

}
