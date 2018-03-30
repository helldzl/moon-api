package com.mifan.article.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/18
 */
public class CompletableFutureTests {

    public void a() {
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("hello");
                }),
                CompletableFuture.runAsync(() -> System.out.println("world"))
        ).join();
    }

    public static void main(String[] args) {
        CompletableFutureTests test = new CompletableFutureTests();
        test.a();
    }

}
