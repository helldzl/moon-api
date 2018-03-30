package com.mifan.article.article;

import com.mifan.article.domain.Topics;

import java.util.List;

/**
 * Created by LiuKai on 2017/8/1.
 */
public class TestDomain {
    private Long id;
    private List<Topics> topicsList;

    public TestDomain() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Topics> getTopicsList() {
        return topicsList;
    }

    public void setTopicsList(List<Topics> topicsList) {
        this.topicsList = topicsList;
    }
}
