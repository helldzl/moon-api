package com.mifan.batch.analyzer.clustering;

import com.mifan.batch.analyzer.support.Classifiable;
import com.mifan.batch.analyzer.support.Publisher;
import org.moonframework.amqp.Resource;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

import static org.moonframework.amqp.Resource.Method;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/17
 */
public class ClusteringPatch extends ClusteringComponent {

    public ClusteringPatch(RedisTemplate<String, String> template, FeatureVector featureVector, Publisher publisher, Map<String, Clustering> map) {
        super(template, featureVector, publisher, map);
    }

    @Override
    public void accept(Resource resource) {
        Classifiable classifiable = resource.get(Classifiable.class);
        Map<String, Object> meta = resource.getMeta();
        int source = (int) meta.get("source");
        if (!classifiable.isEnabled() || source == 0) {
            return;
        }

        int id = Integer.parseInt(classifiable.getId());
        String title = (String) meta.get("title");

        List<Similarity> similarities = computeIfAbsent(classifiable.getModel().getName(), null)
                .detect(source, id, title, classifiable.getValue());
        if (!similarities.isEmpty()) {
            // override save semantics to -> save or update semantics
            similarities.forEach(similarity -> {
                String table = (String) meta.get("target_cluster");
                getPublisher().send(Method.POST, null, table, map -> map(map, id, similarity.getId(), similarity.getScore()));
                getPublisher().send(Method.POST, null, table, map -> map(map, similarity.getId(), id, similarity.getScore()));
            });
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Clustering result:%s", similarities));
            }
        }
    }

    private void map(Map<String, Object> map, long x, long y, double score) {
        map.put("topicId", x);
        map.put("targetId", y);
        map.put("score", score);
        map.put("type", 0);
        map.put("enabled", 1);
    }
}
