package com.mifan.batch.analyzer.redis;

import com.mifan.batch.analyzer.AbstractTests;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/6/9
 */
public class RedisTests extends AbstractTests {

    @Autowired
    private RedisTemplate<String, String> template;
    private Random random = new Random(47);

    /**
     * @throws Exception
     */
    @Ignore
    @Test
    public void testRemove() {
        template.delete(template.keys("product.block.*"));
        template.delete(template.keys("product.fingerprint.*"));
        template.delete(template.keys("product.title.*"));
        System.out.println("finished!");
    }

    @Test
    public void testRedis() throws Exception {
        System.out.println(template);
        find(-5033825274058570474L);
    }

    @Test
    public void testAdd() throws Exception {
        index(3456789234L);
        find(3456789234L);
        System.out.println();

    }

    private void index(long fingerprint) {
        //
        block(fingerprint, key -> template.opsForSet().add(key, Long.toString(fingerprint)));
    }

    private void block(long n, Consumer<String> consumer) {
        IntStream.range(0, 4).forEach(block -> consumer.accept(key(block, (int) (n >> (block * 16) & 0xFFFF))));
    }

    private String key(int block, int query) {
        return String.format("%s.%s.%s", "product", block, query);
    }

    // 数据结构一
    private Set<Long> find(String type, int block, int query) {
//        try {
//            TimeUnit.MILLISECONDS.sleep(random.nextInt(200));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        String key = String.format("%s.%s.%s", type, block, query);
        System.out.println(key);
        return template.opsForSet().members(key).stream().map(Long::valueOf).collect(Collectors.toSet());
    }

    private Set<Long> combine(Set<Long> left, Set<Long> right) {
        left.addAll(right);
        return left;
    }

    private void find(long fingerprint) throws ExecutionException, InterruptedException {
        List<CompletableFuture<Set<Long>>> list = new ArrayList<>();
        IntStream.range(0, 4).forEach(block -> {
            int query = (int) (fingerprint >> (block * 16) & 0xFFFF);
            list.add(CompletableFuture.supplyAsync(() -> find("product", block, query)));
        });
        CompletableFuture<Set<Long>> result = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            result = result.thenCombine(list.get(i), this::combine);
        }

        System.out.println(111);
        System.out.println(result.get());
        System.out.println(222);


    }

    public static void main(String[] args) {
        Stream<CompletableFuture<String>> completableFutureStream = Stream.of(
                CompletableFuture.supplyAsync(() -> "1"),
                CompletableFuture.supplyAsync(() -> "1")
        );
        System.out.println(completableFutureStream.isParallel());
    }
}
