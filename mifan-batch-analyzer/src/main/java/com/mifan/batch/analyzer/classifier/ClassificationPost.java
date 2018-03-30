package com.mifan.batch.analyzer.classifier;

import com.mifan.batch.analyzer.support.Classifiable;
import com.mifan.batch.analyzer.support.Model;
import com.mifan.batch.analyzer.support.Publisher;
import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.analysis.Analyzer;
import org.moonframework.amqp.Resource;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>训练模型, 只要有数据, 不管之前存在不存在, 都覆盖并重新进行模型训练</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/11/16
 */
public class ClassificationPost extends ClassificationComponent {

    private ExecutorService executor = Executors.newFixedThreadPool(2);

    public ClassificationPost(Configuration configuration, Analyzer analyzer, Publisher publisher, Map<String, Classification> map) {
        super(configuration, analyzer, publisher, map);
    }

    @Override
    public void accept(Resource resource) {
        Classifiable classifiable = resource.get(Classifiable.class);
        Model model = classifiable.getModel();
        Classification classification = computeIfAbsent(model.getName(), classifier -> {
            consumer(model.getId(), classifier);

            return classifier;
        });
        classification.put(classifiable);
        if (classification.runnable()) {
            executor.execute(classification);
        }
        logger.info(classifiable);
        super.accept(resource);
    }

}
