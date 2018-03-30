package com.mifan.article.article;

import com.mifan.article.domain.TopicsClustering;
import org.moonframework.model.mybatis.domain.BaseEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/19
 */
public class BitSetTests {


    private static void a() {
        bitSet.set(1);
        bitSet.set(2);
        System.out.println();
        bitSet.clear();
        System.out.println();
    }

    public static void c() {
        Map<Long, Set<Long>> map = new LinkedHashMap<>();
        for (long i = 4; i > 0; i--) {
            LinkedHashSet<Long> set = new LinkedHashSet<>();
            set.add(3L);
            set.add(1L);
            map.put(i, set);
        }
    }

    private static BitSet bitSet = new BitSet();

    public static void main(String[] args) {
        List<TopicsClustering> list = new ArrayList<>();
        list.add(clustering(71274L, 0.2, 24048));
        list.add(clustering(71274L, 0.2, 18139));
        list.add(clustering(71274L, 0.2, 17331));
        list.add(clustering(71274L, 0.2, 11657));

        list.add(clustering(52423, 1, 216184));
        list.add(clustering(52423, 1, 10722));

        list.add(clustering(10470, 1, 295318));
        list.add(clustering(10470, 1, 71274L));

        list.add(clustering(4402, 1, 10470));
        list.add(clustering(4402, 1, 31234));

        List<Long> ids = list.stream().map(BaseEntity::getId).collect(Collectors.toList());
        list.stream().peek(clustering -> {
            System.out.println(clustering.getId());
        }).collect(Collectors.toMap(BaseEntity::getId, o -> {
            System.out.println("SYS:" + o.getTopicId());
            return o;
        }));

        Map<Long, List<Long>> map = new LinkedHashMap<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            TopicsClustering clustering = list.get(i);
            Long k = clustering.getTopicId();
            Long v = clustering.getTargetId();
            // 过滤掉已处理的聚合数据
            if (bitSet.get(k.intValue())) {
                continue;
            }
            // 初始化
            if (!map.containsKey(k))
                map.put(k, new LinkedList<>());
            map.get(k).add(0, v);
            // 设置位集合
            bitSet.set(v.intValue());
        }
        System.out.println(map);
        Set<Long> collect = map.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(Collectors.toSet());
        System.out.println(collect);

//        list.stream().peek(clustering -> {
//            Long a1 = clustering.getTopicId();
//            Long a2 = clustering.getTargetId();
//            bitSet.set(a1.intValue());
//            bitSet.set(a2.intValue());
//            System.out.println(a1 + " " + a2);
//        }).filter(clustering -> !bitSet.get(clustering.getTopicId().intValue())).forEach(clustering -> {
//            System.out.println(clustering.getTopicId());
//        });
    }

    private static TopicsClustering clustering(long topicId, double score, long targetId) {
        TopicsClustering clustering = new TopicsClustering();
        clustering.setTopicId(topicId);
        clustering.setEnabled(1);
        clustering.setScore(score);
        clustering.setTargetId(targetId);
        return clustering;
    }

}
