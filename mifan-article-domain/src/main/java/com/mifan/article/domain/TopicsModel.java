package com.mifan.article.domain;

import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-11-08
 */
public class TopicsModel extends BaseEntity {

    public static final String TABLE_NAME = "topics_model";

    public static final String FORUM_ID = "forum_id";
    public static final String MODEL_STATUS = "model_status";
    public static final String MODEL_NAME = "model_name";
    public static final String PRIORITY = "priority";
    public static final String CONF_RANDOM_SELECTION = "conf_random_selection";
    public static final String CONF_OVERWRITE = "conf_overwrite";
    public static final String RESULT_SAMPLES = "result_samples";
    public static final String RESULT_TEXT = "result_text";

    private static final long serialVersionUID = -2643022354538494881L;

    @NotNull(groups = {Post.class}, message = "{NotNull.TopicsModel.forumId}")
    @Null(groups = {Patch.class}, message = "{Null.TopicsModel.forumId}")
    private Long forumId;

    @Null(groups = {Post.class}, message = "{Null.TopicsModel.modelStatus}")
    private String modelStatus;

    @NotNull(groups = {Post.class}, message = "{NotNull.TopicsModel.modelName}")
    @Null(groups = {Patch.class}, message = "{Null.TopicsModel.modelName}")
    @Size(groups = {Post.class, Patch.class}, message = "{Size.TopicsModel.modelName}", min = 1, max = 80)
    private String modelName;

    @Range(groups = {Post.class, Patch.class}, message = "{Range.TopicsModel.priority}", max = 100)
    private Integer priority;

    @Range(groups = {Post.class, Patch.class}, message = "{Range.TopicsModel.confRandomSelection}", min = 5, max = 50)
    private Integer confRandomSelection;

    @Range(groups = {Post.class, Patch.class}, message = "{Range.TopicsModel.confOverwrite}", min = 0, max = 1)
    private Integer confOverwrite;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.TopicsModel.resultSamples}")
    private Integer resultSamples;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.TopicsModel.resultText}")
    private String resultText;

    public TopicsModel() {
    }

    public TopicsModel(Long id) {
        super(id);
    }

    /**
     * <p>获取训练数据/分类所需要的数据</p>
     *
     * @param id      唯一标识符
     * @param label   目标变量, 可为空
     * @param value   分析文本
     * @param enabled 是否enabled
     * @return map
     */
    public Map<String, Object> data(String id, String label, String value, int enabled) {
        Map<String, Object> data = new HashMap<>(16);
        data.put("model", model());
        data.put("id", id);
        data.put("label", label);
        data.put("value", value);
        data.put("enabled", enabled != 0);
        return data;
    }

    public Map<String, Object> model() {
        Map<String, Object> model = new HashMap<>(16);
        model.put("id", getId().toString());
        model.put("name", getModelName());
        return model;
    }

    public String getModelStatusValue() {
        if (modelStatus == null) {
            return null;
        }
        return ModelStatus.valueOf(modelStatus).getName();
    }

    public void setModelStatus(ModelStatus modelStatus) {
        this.modelStatus = modelStatus.name();
    }

    /**
     * @return 版块ID
     */
    public Long getForumId() {
        return forumId;
    }

    /**
     * @param forumId 版块ID
     */
    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }

    /**
     * @return NEW:新创建的模型
     * RUNNABLE:正在执行训练的模型
     * CLASSIFICATION:正在执行全量分类
     * WAITING:等待
     * DONE:完成
     * DELETE:已经被删除, 无法回滚
     * TERMINATED:训练中的模型被终止
     */
    public String getModelStatus() {
        return modelStatus;
    }

    /**
     * @param modelStatus NEW:新创建的模型
     *                    RUNNABLE:正在执行训练的模型
     *                    CLASSIFICATION:正在执行全量分类
     *                    WAITING:等待
     *                    DONE:训练
     *                    DELETE:已经被删除, 无法回滚
     *                    TERMINATED:训练中的模型被终止
     */
    public void setModelStatus(String modelStatus) {
        this.modelStatus = modelStatus;
    }

    /**
     * @return 模型名称, e.g : topic-1
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * @param modelName 模型名称, e.g : topic-1
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * @return 每个forum_id下, 使用一个优先级最高的模型用于分类
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority 每个forum_id下, 使用一个优先级最高的模型用于分类
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return (1-100) n%用于测试数据
     */
    public Integer getConfRandomSelection() {
        return confRandomSelection;
    }

    /**
     * @param confRandomSelection (1-100) n%用于测试数据
     */
    public void setConfRandomSelection(Integer confRandomSelection) {
        this.confRandomSelection = confRandomSelection;
    }

    /**
     * @return 是否覆盖
     */
    public Integer getConfOverwrite() {
        return confOverwrite;
    }

    /**
     * @param confOverwrite 是否覆盖
     */
    public void setConfOverwrite(Integer confOverwrite) {
        this.confOverwrite = confOverwrite;
    }

    /**
     * @return 训练样本数量
     */
    public Integer getResultSamples() {
        return resultSamples;
    }

    /**
     * @param resultSamples 训练样本数量
     */
    public void setResultSamples(Integer resultSamples) {
        this.resultSamples = resultSamples;
    }

    /**
     * @return 模型结果:测试结果或异常结果
     */
    public String getResultText() {
        return resultText;
    }

    /**
     * @param resultText 模型结果:测试结果或异常结果
     */
    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    public enum ModelStatus {

        NEW("新建", Type.AUTO),
        RUNNABLE("训练", Type.MANUAL),
        CLASSIFICATION("分类", Type.MANUAL),
        WAITING("等待", Type.AUTO),
        DONE("已完成", Type.AUTO),
        DELETE("已删除", Type.AUTO),
        TERMINATED("已终止", Type.AUTO);

        public static List<ModelStatus> from(Type type) {
            return Stream.of(ModelStatus.values()).filter(model -> model.type == type).collect(Collectors.toList());
        }

        public static List<Map<String, String>> from(Set<Type> set) {
            final Set<Type> types;
            if (CollectionUtils.isEmpty(set)) {
                types = Stream.of(Type.values()).collect(Collectors.toSet());
            } else {
                types = set;
            }
            return Stream.of(ModelStatus.values()).filter(model -> types.contains(model.getType()))
                    .map(status -> {
                        Map<String, String> map = new LinkedHashMap<>();
                        // map.put("type", status.getType().name());
                        map.put("name", status.getName());
                        map.put("value", status.name());
                        return map;
                    })
                    .collect(Collectors.toList());
        }

        private final String name;
        private final Type type;

        ModelStatus(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }
    }

    public enum Type {
        MANUAL,
        AUTO
    }

}
