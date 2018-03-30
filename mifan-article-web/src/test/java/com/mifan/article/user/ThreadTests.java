package com.mifan.article.user;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/7
 */
public class ThreadTests {

    @Test
    public void testThread() throws Exception {
        Random random = new Random();
        CompletableFuture<Integer> a = CompletableFuture.supplyAsync(() -> {
            int i = random.nextInt(10);
            System.out.println("time : " + i);
            return 1;
        });

        CompletableFuture<Integer> b = CompletableFuture.supplyAsync(() -> {
            int i = random.nextInt(10);
            System.out.println("time : " + i);
            return i;
        });

        System.out.println();
        Thread.sleep(1000 * 20);
        System.out.println(1);
    }
}
