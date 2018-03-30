package com.mifan.article.domain;


import com.mifan.article.domain.support.HtmlUtils;
import com.mifan.article.domain.support.Multilingual;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;


/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class PostsText extends BaseEntity {

    public static final String TABLE_NAME = "posts_text";

    public static final String CATEGORY = "category";
    public static final String TAG = "tag";
    public static final String TITLE = "title";
    public static final String FEATURE = "feature";
    public static final String DESCRIPTION = "description";
    public static final String CONTENT = "content";

    private static final long serialVersionUID = 3341396175745946210L;

    private String category;
    private String tag;
    private String title;
    private String feature;
    private String description;
    private String content;

    private Integer noUpdateCategoryAndTagOfNull;//在更新底层，根据这个判断当category和tag为null时该怎么处理，如果等于1则不更新，其他情况则将category和tag修改为""

    public PostsText() {
    }

    public PostsText(Long id) {
        super(id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (title != null) {
            sb.append(title.trim());
        }
        if (description != null) {
            sb.append(", ");
            sb.append(description.trim());
        }
        if (content != null) {
            sb.append(", ");
            HtmlUtils.compress(content, true);
        }
        if (tag != null) {
            sb.append(", ");
            sb.append(tag.trim());
        }
        if (feature != null) {
            sb.append(", ");
            List<Map<String, Object>> list = Multilingual.getList(feature);
            for (Map<String, Object> map : list) {
                sb.append(map.get("_name"));
                sb.append(", ");
                String value = HtmlUtils.compress((String) map.get("_value"), true);
                if (!StringUtils.isEmpty(value)) {
                    sb.append(value);
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    /**
     * @return 类别
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category 类别
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return 标签
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag 标签
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return 特性
     */
    public String getFeature() {
        return feature;
    }

    /**
     * @param feature 特性
     */
    public void setFeature(String feature) {
        this.feature = feature;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    public Integer getNoUpdateCategoryAndTagOfNull() {
        return noUpdateCategoryAndTagOfNull;
    }

    public void setNoUpdateCategoryAndTagOfNull(Integer noUpdateCategoryAndTagOfNull) {
        this.noUpdateCategoryAndTagOfNull = noUpdateCategoryAndTagOfNull;
    }


}
