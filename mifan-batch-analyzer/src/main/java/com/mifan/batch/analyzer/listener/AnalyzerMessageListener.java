package com.mifan.batch.analyzer.listener;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mifan.batch.analyzer.classifier.Classification;
import com.mifan.batch.analyzer.classifier.ClassificationDelete;
import com.mifan.batch.analyzer.classifier.ClassificationPatch;
import com.mifan.batch.analyzer.classifier.ClassificationPost;
import com.mifan.batch.analyzer.clustering.*;
import com.mifan.batch.analyzer.support.Publisher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.analysis.Analyzer;
import org.apache.mahout.common.lucene.AnalyzerUtils;
import org.moonframework.amqp.AbstractResourceMessageListener;
import org.moonframework.amqp.MessagePublisher;
import org.moonframework.amqp.autoconfigure.RabbitProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.moonframework.amqp.Resource.META_METHOD;
import static org.moonframework.amqp.Resource.Method;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/14
 */
@Component
public class AnalyzerMessageListener extends AbstractResourceMessageListener implements Publisher {

    private static final Log logger = LogFactory.getLog(AnalyzerMessageListener.class);

    /**
     * <ul>
     * <li>org.apache.lucene.analysis.standard.StandardAnalyzer</li>
     * <li>org.apache.lucene.analysis.en.EnglishAnalyzer</li>
     * <li>org.wltea.analyzer.lucene.IKAnalyzer</li>
     * <li>org.apache.mahout.analysis.SmartIKAnalyzer</li>
     * </ul>
     */
    private static final String DEFAULT_ANALYZER = "org.apache.mahout.analysis.SmartIKAnalyzer";

    private RabbitProperties properties;
    private MessagePublisher publisher;

    /**
     * <p>监听服务端发送消息的队列, 一般是待分析处理的数据</p>
     *
     * @param redisTemplate  redis template
     * @param rabbitTemplate rabbit template
     * @param properties     rabbit properties
     * @param publisher      message publisher
     */
    @Autowired
    public AnalyzerMessageListener(
            RedisTemplate<String, String> redisTemplate,
            RabbitTemplate rabbitTemplate,
            RabbitProperties properties,
            @Value("${moon.amqp.rabbit.analyzer-queue}") String queue,
            MessagePublisher publisher) {
        super(rabbitTemplate, queue);
        this.properties = properties;
        this.publisher = publisher;

        Analyzer analyzer;
        try {
            analyzer = AnalyzerUtils.createAnalyzer(DEFAULT_ANALYZER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Configuration configuration = new Configuration();
        Map<String, Classification> classifications = new ConcurrentHashMap<>();
        Map<String, Clustering> clustering = new ConcurrentHashMap<>();

        ClassificationPost post = new ClassificationPost(configuration, analyzer, this, classifications);
        ClassificationDelete delete = new ClassificationDelete(configuration, analyzer, this, classifications);
        ClassificationPatch patch = new ClassificationPatch(configuration, analyzer, this, classifications);

        HashFunction hashFunction = Hashing.murmur3_128();
        FeatureVector featureVector = new FeatureVector(analyzer, hashFunction, 12);

        post.next(new ClusteringPost(redisTemplate, featureVector, this, clustering));
        delete.next(new ClusteringDelete(redisTemplate, featureVector, this, clustering));
        patch.next(new ClusteringPatch(redisTemplate, featureVector, this, clustering));

        put(Method.POST, post);
        put(Method.DELETE, delete);
        put(Method.PATCH, patch);
    }

    /**
     * <p>线下/离线任务将处理结果发送到消息队列中, 线上生产服务会将结果持久化(PATCH语义的Services.update操作), 并进行使用.</p>
     * <p>其中data consumer 应该(MAY)可以设置enabled字段来过滤分析结果, 0:禁用, 1:启用. 缺省值为1</p>
     *
     * @param id       资源唯一标识符
     * @param type     资源类型
     * @param consumer data consumer
     */
    @Override
    public void send(String id, String type, Consumer<Map<String, Object>> consumer) {
        send(Method.PATCH, id, type, consumer);
    }

    @Override
    public void send(Method method, String id, String type, Consumer<Map<String, Object>> consumer) {
        Map<String, Object> data = new HashMap<>(16);
        if (id != null) {
            data.put("id", Long.valueOf(id));
        }
        consumer.accept(data);
        publisher.send(
                properties.getClientDataExchange(),
                properties.getClientDataQueue(),
                data, id, type, meta -> meta.put(META_METHOD, method));

        if (logger.isDebugEnabled()) {
            logger.debug("Send message:" + data);
        }
    }
}
