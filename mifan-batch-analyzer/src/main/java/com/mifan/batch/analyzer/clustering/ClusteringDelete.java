package com.mifan.batch.analyzer.clustering;

import com.mifan.batch.analyzer.support.Model;
import com.mifan.batch.analyzer.support.Publisher;
import org.moonframework.amqp.Resource;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/17
 */
public class ClusteringDelete extends ClusteringComponent {

    public ClusteringDelete(RedisTemplate<String, String> template, FeatureVector featureVector, Publisher publisher, Map<String, Clustering> map) {
        super(template, featureVector, publisher, map);
    }

    @Override
    public void accept(Resource resource) {
        Model model = resource.get(Model.class);
        computeIfAbsent(model.getName(), null).delete();
    }
}
