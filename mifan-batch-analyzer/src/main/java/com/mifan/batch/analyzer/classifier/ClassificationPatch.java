package com.mifan.batch.analyzer.classifier;

import com.mifan.batch.analyzer.support.Classifiable;
import com.mifan.batch.analyzer.support.Publisher;
import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.analysis.Analyzer;
import org.moonframework.amqp.Data;
import org.moonframework.amqp.Resource;
import org.moonframework.core.util.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>模型分类, 要判断模型文件存在与否</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/11/16
 */
public class ClassificationPatch extends ClassificationComponent {

    private static final String TARGET_CLASSIFICATION = "target_classification";
    private static final String FIELD_TARGET_VARIABLE = "targetVariable";
    private static final String FIELD_ENABLED = "enabled";

    public ClassificationPatch(Configuration configuration, Analyzer analyzer, Publisher publisher, Map<String, Classification> map) {
        super(configuration, analyzer, publisher, map);
    }

    @Override
    public void accept(Resource resource) {
        Classifiable classifiable = resource.get(Classifiable.class);
        Classification model = computeIfAbsent(classifiable.getModel().getName(), classification -> {
            if (classification.load()) {
                consumer(classifiable.getModel().getId(), classification);
                return classification;
            } else {
                return null;
            }
        });

        // Return if model is null
        if (model == null) {
            if (logger.isErrorEnabled()) {
                logger.error("模型未初始化:" + classifiable.getModel());
            }
            return;
        }

        // Classify
        Data data = resource.getData();
        Map<String, Object> meta = resource.getMeta();

        // Send Result
        model.classify(classifiable);
        getPublisher().send(classifiable.getId(), (String) meta.get(TARGET_CLASSIFICATION), map -> {
            map.put(FIELD_TARGET_VARIABLE, label(meta, classifiable.getLabel()));
            map.put(FIELD_ENABLED, classifiable.isEnabled() ? 1 : 0);
        });

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Classify type:%s, id:%s, target variable:%s", data.getType(), data.getId(), classifiable.getLabel()));
        }
        super.accept(resource);
    }

    @SuppressWarnings("unchecked")
    private String label(Map<String, Object> meta, String label) {
        String language = (String) meta.get("language");
        List<Map<String, String>> list = (List<Map<String, String>>) meta.get("categories");
        if (CollectionUtils.isEmpty(list)) {
            Map<String, String> map = new HashMap<>(16);
            map.put(language, label);
            return BeanUtils.writeValueAsString(Collections.singletonList(map));
        } else {
            Map<String, String> map = list.get(0);
            map.clear();
            map.put(language, label);
            return BeanUtils.writeValueAsString(list);
        }
    }
}
