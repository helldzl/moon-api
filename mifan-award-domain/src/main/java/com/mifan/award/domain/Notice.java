package com.mifan.award.domain;

import org.moonframework.model.mongodb.domain.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "b_award_notice")
public class Notice extends BaseEntity {

    @Field("title")
    private String title;
    @Field("content")
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
