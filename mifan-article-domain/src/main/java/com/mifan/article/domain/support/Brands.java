package com.mifan.article.domain.support;

import com.mifan.article.domain.Attachments;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.TopicsFetch;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/18
 */
public class Brands {

    private static Map<String, Object> EMPTY = new HashMap<>();

    static {
        EMPTY.put("id", 0L);
        EMPTY.put("name", null);
        EMPTY.put("logo", null);
        EMPTY.put("rating", 0D);
        EMPTY.put("reviews", 0);
        EMPTY.put("top", false);
    }

    private Long id;
    private String name;
    private String logo;
    private double rating;
    private int reviews;
    private boolean top;

    public static Map<String, Object> toMap(Brands brand) {
        if (brand == null) {
            return EMPTY;
        }

        Map<String, Object> map = new HashMap<>(16);
        map.put("id", brand.getId());
        String brandName = brand.getName().trim();
        map.put("name", brandName);
        String aliasName = null;
        if(brandName != null) {
            aliasName = brandName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        }
        map.put("aliasName",aliasName);
        map.put("logo", brand.getLogo());
        map.put("rating", brand.getRating());
        map.put("reviews", brand.getReviews());
        map.put("top", brand.isTop());
        return map;
    }

    /**
     * <p>为品牌设置抓取下来的数值数据</p>
     *
     * @param topicsFetch topics fetch
     */
    public Brands fetch(TopicsFetch topicsFetch) {
        if (topicsFetch != null) {
            if (topicsFetch.getRating() != null) {
                setRating(topicsFetch.getRating());
            }
            if (topicsFetch.getReviews() != null) {
                setReviews(topicsFetch.getReviews());
            }
        }
        return this;
    }

    public Brands logo(Attachments attachment) {
        if (attachment != null) {
            setLogo(attachment.getFilename());
        }
        return this;
    }

    public Brands logo(Map<Attachments.ContentType, List<Attachments>> contentTypeMap) {
        if (!CollectionUtils.isEmpty(contentTypeMap)) {
            List<Attachments> images = contentTypeMap.get(Attachments.ContentType.IMAGES);
            if (!CollectionUtils.isEmpty(images)) {
                setLogo(images.get(0).getFilename());
            }
        }
        return this;
    }

    public Brands top(Posts post) {
        if (post != null) {
            setTop(post.getContent() != null || post.getDescription() != null || post.getFeatures() != null);
        }
        return this;
    }

    //

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

}
