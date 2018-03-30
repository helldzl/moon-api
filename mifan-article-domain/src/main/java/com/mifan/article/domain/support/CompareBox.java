package com.mifan.article.domain.support;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/3
 */
public class CompareBox {

    private List<String> columns;
    private boolean collapsible;

    public CompareBox(List<String> columns, boolean collapsible) {
        this.columns = columns;
        this.collapsible = collapsible;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public boolean isCollapsible() {
        return collapsible;
    }

    public void setCollapsible(boolean collapsible) {
        this.collapsible = collapsible;
    }
}
