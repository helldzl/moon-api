package com.mifan.batch.analyzer.clustering;

import com.mifan.batch.analyzer.support.ComponentAdapter;
import com.mifan.batch.analyzer.support.Publisher;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/17
 */
public abstract class ClusteringComponent extends ComponentAdapter<Clustering> {

    private RedisTemplate<String, String> template;
    private FeatureVector featureVector;

    public ClusteringComponent(RedisTemplate<String, String> template, FeatureVector featureVector, Publisher publisher, Map<String, Clustering> map) {
        super(publisher, map);
        this.template = template;
        this.featureVector = featureVector;
    }

    @Override
    protected Clustering newInstance(String name) {
        return new Clustering(name, template, featureVector);
    }

}
