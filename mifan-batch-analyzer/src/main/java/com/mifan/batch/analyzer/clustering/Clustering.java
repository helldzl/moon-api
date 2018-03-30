package com.mifan.batch.analyzer.clustering;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

/**
 * <p><a href='http://www2007.org/papers/paper215.pdf'>参考论文</a></p>
 * <p><a href='http://blog.csdn.net/heiyeshuwu/article/details/44117473'>参考基于该论文的相关文章</a></p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/6/9
 */
public class Clustering {

    // 海明距离4, 分5块
    private static int[] BLOCKS = {0x1FFF, 0x1FFF, 0x1FFF, 0x1FFF, 0x0FFF};

    // 海明距离3, 分4块
    // private static int[] BLOCKS = {0xFFFF, 0xFFFF, 0xFFFF, 0xFFFF};

    private static int distance = BLOCKS.length - 1;

    private RedisTemplate<String, String> template;
    private SetOperations<String, String> opsForSet;
    private FeatureVector featureVector;

    private String type;

    public Clustering(String type, RedisTemplate<String, String> template, FeatureVector featureVector) {
        this.type = type;
        this.template = template;
        this.opsForSet = template.opsForSet();
        this.featureVector = featureVector;
    }

    public void delete() {
        template.delete(template.keys(type + ".block.*"));
        template.delete(template.keys(type + ".fingerprint.*"));
        template.delete(template.keys(type + ".title.*"));
    }

    /**
     * <p>返回与docId相似的文档集合</p>
     *
     * @param source 来源标识符
     * @param docId  资源唯一的文档ID
     * @param title  标题
     * @param text   已经优化格式的内容文本
     * @return 相似语义指纹集合, 如果size=0说明没有找到相似的文档.
     * @
     */
    public List<Similarity> detect(int source, int docId, String title, String text) {
        try {
            // generate SimHash
            long fingerprint = featureVector.simHash(text);

            // set high and low bit to long
            Pair binary = new Pair(source, docId);

            // redis key
            String fingerprintSetKey = key(fingerprint);
            String titleSetKey = key(title);

            // add to index
            String stringBinary = binary.getStringBinary();
            if (!opsForSet.isMember(titleSetKey, stringBinary)) {
                block(fingerprint, key -> opsForSet.add(key, Long.toString(fingerprint)));
                opsForSet.add(fingerprintSetKey, stringBinary);
                opsForSet.add(titleSetKey, stringBinary);
            }

            // find name first & find similarity second
            List<Similarity> result = opsForSet.members(titleSetKey).stream().map(Pair::new).filter(pair -> pair.filter(binary)).map(pair -> new Similarity(pair.getY(), 0L, 1D)).collect(Collectors.toList());
            if (result.isEmpty()) {
                return find(binary, fingerprint);
            } else {
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * <p>根据语义指纹并发从多个块中查询相似的记录, 并合/排序其结果, 结果中不包含文档本身</p>
     *
     * @param binary      高32位为source, 低32位为文档ID
     * @param fingerprint 语义指纹
     * @return similarities
     * @throws ExecutionException   ExecutionException
     * @throws InterruptedException InterruptedException
     */
    private List<Similarity> find(Pair binary, long fingerprint) throws ExecutionException, InterruptedException {
        List<CompletableFuture<Set<Long>>> list = new ArrayList<>();
        block(fingerprint, key -> list.add(CompletableFuture.supplyAsync(() -> opsForSet.members(key).stream().map(Long::valueOf).collect(toSet()))));
        CompletableFuture<Set<Long>> result = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            result = result.thenCombine(list.get(i), this::combine);
        }
        return similarity(binary, fingerprint, result.get());
    }

    /**
     * <p>只比较不同来源的数据</p>
     *
     * @param binary      高32位为source, 低32位为文档ID
     * @param fingerprint fingerprint
     * @param set         fingerprints which find by fingerprint
     * @return similarities
     */
    private List<Similarity> similarity(Pair binary, long fingerprint, Set<Long> set) {
        List<Similarity> similarities = new ArrayList<>();
        for (Long hash : set) {
            int count = Long.bitCount(fingerprint ^ hash);
            if (count <= distance) {
                double score = 1D / (count + 1D);
                opsForSet.members(key(hash)).stream().map(Pair::new).filter(pair -> pair.filter(binary)).map(pair -> new Similarity(pair.getY(), hash, score)).forEach(similarities::add);
            }
        }
        Collections.sort(similarities);
        return similarities;
    }

    /**
     * <p>合并从不同块查询出来的结果</p>
     *
     * @param left  left set
     * @param right right set
     * @return result
     */
    private Set<Long> combine(Set<Long> left, Set<Long> right) {
        left.addAll(right);
        return left;
    }

    /**
     * @param n        number
     * @param consumer consumer
     */
    private void block(long n, Consumer<String> consumer) {
        for (int shift = 0, i = 0; i < BLOCKS.length; shift += Long.bitCount(BLOCKS[i]), i++) {
            consumer.accept(key(i, (int) (n >> shift & BLOCKS[i])));
        }
    }

    // 三种REDIS数据结构

    /**
     * <p>数据结构:根据语义指纹分块后的索引快速查询相似的语义指纹集合</p>
     *
     * @param index 分块, [0, n]
     * @param query 每个block的查询索引
     * @return key
     */
    private String key(int index, int query) {
        return String.format("%s.block.%s.%s", type, index, query);
    }

    /**
     * <p>数据结构:根据语义指纹查找文档ID集合</p>
     *
     * @param fingerprint 语义指纹
     * @return key
     */
    private String key(long fingerprint) {
        return String.format("%s.fingerprint.%s", type, fingerprint);
    }

    /**
     * <p>数据结构:根据标题查找文档ID集合</p>
     *
     * @param title 标题
     * @return key
     */
    private String key(String title) {
        title = title.replaceAll(" ", "").trim();
        return String.format("%s.title.%s", type, DigestUtils.md5Hex(title));
    }

    private static class Pair {

        private long binary;
        private int x;
        private int y;

        public Pair(String binary) {
            this(Long.parseLong(binary));
        }

        public Pair(long binary) {
            this.binary = binary;
            this.x = (int) (0xFFFFFFFFL & binary >> 32);
            this.y = (int) (0xFFFFFFFFL & binary);
        }

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
            this.binary = ((long) x << 32) + ((long) y >= 0 ? (long) y : -(long) y);
        }

        @Override
        public String toString() {
            return String.format("{x:%d, y:%d}", x, y);
        }

        public boolean filter(Pair pair) {
            return this.x != pair.getX() && this.y != pair.getY();
        }

        public String getStringBinary() {
            return Long.toString(binary);
        }

        public long getBinary() {
            return binary;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static void main(String[] args) {
        // TEST
        int x = Integer.MAX_VALUE;
        int y = Integer.MIN_VALUE;
        Pair pair = new Pair(x, y);
        System.out.println(new Pair(pair.getBinary()).toString());

        System.out.println();

        x = Integer.MIN_VALUE;
        y = Integer.MAX_VALUE;
        pair = new Pair(x, y);
        System.out.println(new Pair(pair.getBinary()).toString());

        System.out.println();

        x = Integer.MAX_VALUE;
        y = Integer.MAX_VALUE;
        pair = new Pair(x, y);
        System.out.println(new Pair(pair.getBinary()).toString());

        System.out.println();

        x = Integer.MIN_VALUE;
        y = Integer.MIN_VALUE;
        pair = new Pair(x, y);
        System.out.println(new Pair(pair.getBinary()).toString());

        pair = new Pair(8589936355L);
        System.out.println(pair.getX());
        System.out.println(pair.getY());
    }

}
