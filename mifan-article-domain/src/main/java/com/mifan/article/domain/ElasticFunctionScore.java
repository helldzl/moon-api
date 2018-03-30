package com.mifan.article.domain;

import javax.validation.constraints.NotNull;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.validation.ValidationGroups.Patch;
import org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-25
 */
public class ElasticFunctionScore extends BaseEntity {

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(ElasticFunctionScore.ID)
            .add(ElasticFunctionScore.TITLE)
            .add(ElasticFunctionScore.NUMERIC_FIELD)
            .add(ElasticFunctionScore.FILTERS)
            .add(ElasticFunctionScore.WEIGHT)
            .add(ElasticFunctionScore.FUNCTION_MODIFIER)
            .add(ElasticFunctionScore.FUNCTION_FACTOR)
            .add(ElasticFunctionScore.FUNCTION_MISSING)
            .add(ElasticFunctionScore.FUNCTION_GLOBAL)
            .build();

    public static final String TABLE_NAME = "elastic_function_score";

    public static final String TITLE = "title";
    public static final String NUMERIC_FIELD = "numeric_field";
    public static final String FILTERS = "filters";
    public static final String WEIGHT = "weight";
    public static final String FUNCTION_MODIFIER = "function_modifier";
    public static final String FUNCTION_FACTOR = "function_factor";
    public static final String FUNCTION_MISSING = "function_missing";
    public static final String FUNCTION_GLOBAL = "function_global";

    private static final long serialVersionUID = 5536460128423571388L;
    @NotNull(groups = {Post.class,Patch.class}, message = "{NotNull.ElasticFunctionScore.title}")
    private String title;
    @NotNull(groups = {Post.class,Patch.class}, message = "{NotNull.ElasticFunctionScore.numericField}")
    private String numericField;
    private String filters;
    private Double weight;
    private String functionModifier;
    private Double functionFactor;
    private Double functionMissing;
    private Integer functionGlobal;

    public ElasticFunctionScore() {
    }

    public ElasticFunctionScore(Long id) {
        super(id);
    }

    /**
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return
     */
    public String getNumericField() {
        return numericField;
    }

    /**
     * @param numericField
     */
    public void setNumericField(String numericField) {
        this.numericField = numericField;
    }

    /**
     * @return
     */
    public String getFilters() {
        return filters;
    }

    /**
     * @param filters
     */
    public void setFilters(String filters) {
        this.filters = filters;
    }

    /**
     * @return
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * @param weight
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * @return
     */
    public String getFunctionModifier() {
        return functionModifier;
    }

    /**
     * @param functionModifier
     */
    public void setFunctionModifier(String functionModifier) {
        this.functionModifier = functionModifier;
    }

    /**
     * @return
     */
    public Double getFunctionFactor() {
        return functionFactor;
    }

    /**
     * @param functionFactor
     */
    public void setFunctionFactor(Double functionFactor) {
        this.functionFactor = functionFactor;
    }

    /**
     * @return
     */
    public Double getFunctionMissing() {
        return functionMissing;
    }

    /**
     * @param functionMissing
     */
    public void setFunctionMissing(Double functionMissing) {
        this.functionMissing = functionMissing;
    }

    /**
     * @return
     */
    public Integer getFunctionGlobal() {
        return functionGlobal;
    }

    /**
     * @param functionGlobal
     */
    public void setFunctionGlobal(Integer functionGlobal) {
        this.functionGlobal = functionGlobal;
    }

}
