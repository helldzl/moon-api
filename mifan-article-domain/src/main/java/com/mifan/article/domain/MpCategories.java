package com.mifan.article.domain;

import java.util.List;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
public class MpCategories extends BaseEntity {

    public static final String TABLE_NAME = "mp_categories";

    public static final String ROOT_ID = "root_id";
    public static final String PARENT_ID = "parent_id";
    public static final String TYPE = "type";
    public static final String TITLE = "title";
    public static final String IMAGE = "image";
    public static final String MOBILE_IMAGE = "mobile_image";
    public static final String PATH = "path";
    public static final String DEPTH = "depth";
    public static final String LEAF = "leaf";
    public static final String DISPLAY_ORDER = "display_order";

    private static final long serialVersionUID = -8685198643359553719L;

    private Long rootId;
    private Long parentId;
    private Integer type;
    private String title;
    private String image;
    private String mobileImage;
    private String path;
    private Integer depth;
    private Integer leaf;
    private Integer displayOrder;
    
    private List<MpCategories> children;

    public MpCategories() {
    }

    public MpCategories(Long id) {
        super(id);
    }

    /**
     * @return 根节点
     */
    public Long getRootId() {
        return rootId;
    }

    /**
     * @param rootId 根节点
     */
    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }
    /**
     * @return 父节点
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId 父节点
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    /**
     * @return 0:大类,1:型号,2:小类
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 0:大类,1:型号,2:小类
     */
    public void setType(Integer type) {
        this.type = type;
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
     * @return 标题图
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image 标题图
     */
    public void setImage(String image) {
        this.image = image;
    }
    /**
     * @return 手机标题图
     */
    public String getMobileImage() {
        return mobileImage;
    }

    /**
     * @param mobileImage 手机标题图
     */
    public void setMobileImage(String mobileImage) {
        this.mobileImage = mobileImage;
    }
    /**
     * @return 路径
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path 路径
     */
    public void setPath(String path) {
        this.path = path;
    }
    /**
     * @return 深度
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     * @param depth 深度
     */
    public void setDepth(Integer depth) {
        this.depth = depth;
    }
    /**
     * @return 叶子节点
     */
    public Integer getLeaf() {
        return leaf;
    }

    /**
     * @param leaf 叶子节点
     */
    public void setLeaf(Integer leaf) {
        this.leaf = leaf;
    }
    /**
     * @return 排序
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /**
     * @param displayOrder 排序
     */
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<MpCategories> getChildren() {
        return children;
    }

    public void setChildren(List<MpCategories> children) {
        this.children = children;
    }

}
