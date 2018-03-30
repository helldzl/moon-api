package com.mifan.batch.analyzer.classifier;

import com.mifan.batch.analyzer.support.Model;
import com.mifan.batch.analyzer.support.Publisher;
import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.analysis.Analyzer;
import org.moonframework.amqp.Resource;

import java.util.Map;

/**
 * <p>删除模型和其在hadoop上的相关文件</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/11/16
 */
public class ClassificationDelete extends ClassificationComponent {

    public ClassificationDelete(Configuration configuration, Analyzer analyzer, Publisher publisher, Map<String, Classification> map) {
        super(configuration, analyzer, publisher, map);
    }

    @Override
    public void accept(Resource resource) {
        Model model = resource.get(Model.class);
        computeIfAbsent(model.getName(), classification -> {
            consumer(model.getId(), classification);
            return classification;
        }).delete();
        super.accept(resource);
    }

}
