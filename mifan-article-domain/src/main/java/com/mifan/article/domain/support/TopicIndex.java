package com.mifan.article.domain.support;

import org.moonframework.model.mybatis.domain.BaseEntity;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/22
 */
public class TopicIndex extends BaseEntity {

    private List<String> array;
    private int start;
    private int end;

    public TopicIndex() {
    }

    public TopicIndex(Long id) {
        super(id);
    }

    public List<String> getArray() {
        return array;
    }

    public void setArray(List<String> array) {
        this.array = array;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

}
