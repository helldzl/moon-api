package com.mifan.article.domain;

import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-12-12
 */
public class Folders extends BaseEntity {

    public static final String TABLE_NAME = "folders";

    public static final String PARENT_ID = "parent_id";
    public static final String FOLDER_TYPE = "folder_type";
    public static final String FOLDER_NAME = "folder_name";
    public static final String AMOUNT = "amount";
    public static final String CAPACITY = "capacity";
    public static final String DISPLAY_ORDER = "display_order";
    public static final String CANCELLABLE = "cancellable";

    private static final long serialVersionUID = -4184662005143628559L;

    @Null(groups = {Patch.class}, message = "{Null.Folders.parentId}")
    private Long parentId;

    @NotNull(groups = {Post.class}, message = "{NotNull.Folders.folderType}")
    @Null(groups = {Patch.class}, message = "{Null.Folders.folderType}")
    private Integer folderType;

    @NotNull(groups = {
            Post.class}, message = "{NotNull.Folders.folderName}")
    @Size(groups = {
            Post.class,
            Patch.class}, message = "{Size.Folders.folderName}", min = 1, max = 200)
    private String folderName;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.Folders.amount}")
    private Integer amount;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.Folders.capacity}")
    private Integer capacity;

    private Integer displayOrder;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.Folders.cancellable}")
    @Range(min = 0, max = 1, message = "Range.Folders.cancellable")
    private Integer cancellable;

    /**
     * 基于amount的增量值
     */
    @Null(groups = {Post.class, Patch.class}, message = "{Null.Folders.increment}")
    private Integer increment;

    //

    private boolean checked;

    public Folders() {
    }

    public Folders(Long id) {
        super(id);
    }

    /**
     * @return parent ID
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId parent ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return
     */
    public Integer getFolderType() {
        return folderType;
    }

    /**
     * @param folderType
     */
    public void setFolderType(Integer folderType) {
        this.folderType = folderType;
    }

    /**
     * @return 文件夹名称
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * @param folderName 文件夹名称
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * 文件大小
     *
     * @return
     */
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    /**
     * 文件容量
     *
     * @return
     */
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * @return
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /**
     * @param displayOrder
     */
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getCancellable() {
        return cancellable;
    }

    public void setCancellable(Integer cancellable) {
        this.cancellable = cancellable;
    }

    public Integer getIncrement() {
        return increment;
    }

    public void setIncrement(Integer increment) {
        this.increment = increment;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public enum FolderType {

        COMPARE(1, "compare");

        private final int index;
        private final String name;

        private static Map<Integer, FolderType> map = new HashMap<>();

        static {
            for (FolderType folderType : FolderType.values()) {
                map.put(folderType.getIndex(), folderType);
            }
        }

        public static FolderType from(Integer index) {
            return map.get(index);
        }

        FolderType(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }

}
