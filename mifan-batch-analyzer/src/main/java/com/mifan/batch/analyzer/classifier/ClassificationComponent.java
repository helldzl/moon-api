package com.mifan.batch.analyzer.classifier;

import com.mifan.batch.analyzer.support.ComponentAdapter;
import com.mifan.batch.analyzer.support.Publisher;
import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.analysis.Analyzer;

import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/16
 */
public abstract class ClassificationComponent extends ComponentAdapter<Classification> {

    private static final String CLASS_NAME = "com.mifan.article.domain.TopicsModel";
    private static final String FIELD_RESULT_TEXT = "resultText";
    private static final String FIELD_MODEL_STATUS = "modelStatus";
    private static final String STATUS_DONE = "DONE";

    protected Configuration configuration;
    protected Analyzer analyzer;

    public ClassificationComponent(Configuration configuration, Analyzer analyzer, Publisher publisher, Map<String, Classification> map) {
        super(publisher, map);
        this.configuration = configuration;
        this.analyzer = analyzer;
    }

    @Override
    protected Classification newInstance(String name) {
        return new Classification(configuration, analyzer, name);
    }

    protected void consumer(String id, Classification classification) {
        classification.setConsumer(result ->
                getPublisher().send(id, CLASS_NAME, map -> {
                    map.put(FIELD_RESULT_TEXT, result);
                    map.put(FIELD_MODEL_STATUS, STATUS_DONE);
                }));
    }

}
