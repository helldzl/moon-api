package com.mifan.support.domain;

import org.moonframework.validation.ValidationGroups.Patch;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Post;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-04-21
 */
public class EventDic extends BaseEntity {

    public static final String TABLE_NAME = "event_dic";

    public static final String EVENT_TYPE = "event_type";
    public static final String EVENT_CODE = "event_code";
    public static final String EVENT_DESCRIBE = "event_describe";

    private static final long serialVersionUID = 3598987356806702389L;

    @NotNull(groups = {Post.class,Patch.class}, message = "{NotNull.EventDic.eventType}")
    private Integer eventType;
    @NotBlank(groups = {Post.class,Patch.class}, message = "{NotNull.EventDic.eventCode}")
    private String eventCode;
    @NotBlank(groups = {Post.class,Patch.class}, message = "{NotNull.EventDic.eventDescribe}")
    private String eventDescribe;

    public EventDic() {
    }

    public EventDic(Long id) {
        super(id);
    }

    /**
     * @return 事件类型，待定义
     */
    public Integer getEventType() {
        return eventType;
    }

    /**
     * @param eventType 事件类型，待定义
     */
    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }
    /**
     * @return 事件编码，应该唯一
     */
    public String getEventCode() {
        return eventCode;
    }

    /**
     * @param eventCode 事件编码，应该唯一
     */
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }
    /**
     * @return 事件描述
     */
    public String getEventDescribe() {
        return eventDescribe;
    }

    /**
     * @param eventDescribe 事件描述
     */
    public void setEventDescribe(String eventDescribe) {
        this.eventDescribe = eventDescribe;
    }

}
