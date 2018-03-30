package com.mifan.batch.analyzer.support;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/16
 */
public class Model {

    /**
     * 模型ID
     */
    private String id;

    /**
     * 模型名称
     */
    private String name;

    public Model() {
    }

    public Model(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("ID:%s, Model:%s", id, name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
