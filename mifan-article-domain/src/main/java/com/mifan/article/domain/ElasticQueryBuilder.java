package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.validation.ValidationGroups.Patch;
import org.moonframework.validation.ValidationGroups.Post;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-25
 */
public class ElasticQueryBuilder extends BaseEntity {

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(ElasticQueryBuilder.ID)
            .add(ElasticQueryBuilder.TITLE)
            .add(ElasticQueryBuilder.QUERY_FIELDS_EN)
            .add(ElasticQueryBuilder.QUERY_FIELDS_CN)
            .add(ElasticQueryBuilder.FILTER_CATEGORIES_EN)
            .add(ElasticQueryBuilder.FILTER_CATEGORIES_CN)
            .add(ElasticQueryBuilder.POSITIVE_QUERY_STRING_EN)
            .add(ElasticQueryBuilder.POSITIVE_QUERY_STRING_CN)
            .add(ElasticQueryBuilder.NEGATIVE_QUERY_STRING_EN)
            .add(ElasticQueryBuilder.NEGATIVE_QUERY_STRING_CN)
            .add(ElasticQueryBuilder.NEGATIVE_BOOST)
            .add(ElasticQueryBuilder.FUNCTION_SCORE_MODE)
            .add(ElasticQueryBuilder.FUNCTION_BOOST_MODE)
            .build();

    public static final String TABLE_NAME = "elastic_query_builder";

    public static final String TITLE = "title";
    public static final String QUERY_FIELDS_EN = "query_fields_en";
    public static final String QUERY_FIELDS_CN = "query_fields_cn";
    public static final String FILTER_CATEGORIES_EN = "filter_categories_en";
    public static final String FILTER_CATEGORIES_CN = "filter_categories_cn";
    public static final String POSITIVE_QUERY_STRING_EN = "positive_query_string_en";
    public static final String POSITIVE_QUERY_STRING_CN = "positive_query_string_cn";
    public static final String NEGATIVE_QUERY_STRING_EN = "negative_query_string_en";
    public static final String NEGATIVE_QUERY_STRING_CN = "negative_query_string_cn";
    public static final String NEGATIVE_BOOST = "negative_boost";
    public static final String FUNCTION_SCORE_MODE = "function_score_mode";
    public static final String FUNCTION_BOOST_MODE = "function_boost_mode";

    private static final long serialVersionUID = -6244730059107376624L;
    @NotNull(groups = {Post.class,Patch.class}, message = "{NotNull.ElasticQueryBuilder.title}")
    private String title;
    private String queryFieldsEn;
    private String queryFieldsCn;
    private String filterCategoriesEn;
    private String filterCategoriesCn;
    private String positiveQueryStringEn;
    private String positiveQueryStringCn;
    private String negativeQueryStringEn;
    private String negativeQueryStringCn;
    private Double negativeBoost;
    private String functionScoreMode;
    private String functionBoostMode;

    //

    private List<ElasticFunctionScore> functionScoreList;

    public ElasticQueryBuilder() {
    }

    public ElasticQueryBuilder(Long id) {
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
    public String getQueryFieldsEn() {
        return queryFieldsEn;
    }

    /**
     * @param queryFieldsEn
     */
    public void setQueryFieldsEn(String queryFieldsEn) {
        this.queryFieldsEn = queryFieldsEn;
    }

    /**
     * @return
     */
    public String getQueryFieldsCn() {
        return queryFieldsCn;
    }

    /**
     * @param queryFieldsCn
     */
    public void setQueryFieldsCn(String queryFieldsCn) {
        this.queryFieldsCn = queryFieldsCn;
    }

    /**
     * @return
     */
    public String getFilterCategoriesEn() {
        return filterCategoriesEn;
    }

    /**
     * @param filterCategoriesEn
     */
    public void setFilterCategoriesEn(String filterCategoriesEn) {
        this.filterCategoriesEn = filterCategoriesEn;
    }

    /**
     * @return
     */
    public String getFilterCategoriesCn() {
        return filterCategoriesCn;
    }

    /**
     * @param filterCategoriesCn
     */
    public void setFilterCategoriesCn(String filterCategoriesCn) {
        this.filterCategoriesCn = filterCategoriesCn;
    }

    /**
     * @return
     */
    public String getPositiveQueryStringEn() {
        return positiveQueryStringEn;
    }

    /**
     * @param positiveQueryStringEn
     */
    public void setPositiveQueryStringEn(String positiveQueryStringEn) {
        this.positiveQueryStringEn = positiveQueryStringEn;
    }

    /**
     * @return
     */
    public String getPositiveQueryStringCn() {
        return positiveQueryStringCn;
    }

    /**
     * @param positiveQueryStringCn
     */
    public void setPositiveQueryStringCn(String positiveQueryStringCn) {
        this.positiveQueryStringCn = positiveQueryStringCn;
    }

    /**
     * @return
     */
    public String getNegativeQueryStringEn() {
        return negativeQueryStringEn;
    }

    /**
     * @param negativeQueryStringEn
     */
    public void setNegativeQueryStringEn(String negativeQueryStringEn) {
        this.negativeQueryStringEn = negativeQueryStringEn;
    }

    /**
     * @return
     */
    public String getNegativeQueryStringCn() {
        return negativeQueryStringCn;
    }

    /**
     * @param negativeQueryStringCn
     */
    public void setNegativeQueryStringCn(String negativeQueryStringCn) {
        this.negativeQueryStringCn = negativeQueryStringCn;
    }

    /**
     * @return
     */
    public Double getNegativeBoost() {
        return negativeBoost;
    }

    /**
     * @param negativeBoost
     */
    public void setNegativeBoost(Double negativeBoost) {
        this.negativeBoost = negativeBoost;
    }

    /**
     * @return
     */
    public String getFunctionScoreMode() {
        return functionScoreMode;
    }

    /**
     * @param functionScoreMode
     */
    public void setFunctionScoreMode(String functionScoreMode) {
        this.functionScoreMode = functionScoreMode;
    }

    /**
     * @return
     */
    public String getFunctionBoostMode() {
        return functionBoostMode;
    }

    /**
     * @param functionBoostMode
     */
    public void setFunctionBoostMode(String functionBoostMode) {
        this.functionBoostMode = functionBoostMode;
    }

    public List<ElasticFunctionScore> getFunctionScoreList() {
        return functionScoreList;
    }

    public void setFunctionScoreList(List<ElasticFunctionScore> functionScoreList) {
        this.functionScoreList = functionScoreList;
    }
}
