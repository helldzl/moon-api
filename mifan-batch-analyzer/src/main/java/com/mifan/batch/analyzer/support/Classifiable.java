package com.mifan.batch.analyzer.support;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/14
 */
public class Classifiable {

    public static final String USELESS = "Useless";

    private Model model;
    private String id;
    private String label;
    private String value;
    private boolean enabled;

    public Classifiable() {
    }

    @Override
    public String toString() {
        return String.format("model:%s, id:%s, enabled:%s, label:%s", model.getName(), id, enabled, label);
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        if (enabled) {
            return label;
        } else {
            return USELESS;
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
