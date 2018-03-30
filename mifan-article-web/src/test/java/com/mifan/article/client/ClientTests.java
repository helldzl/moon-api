package com.mifan.article.client;

import org.apache.http.impl.client.CloseableHttpClient;
import org.moonframework.crawler.util.HttpClientUtils;

public class ClientTests {


    public static void main(String[] args) {
        for (int i = 0; i < 1000000; i++) {
            long start = System.nanoTime();
            //CloseableHttpClient httpClient = HttpClientUtil.getHttpClient();
            CloseableHttpClient closeableHttpClient = HttpClientUtils.newInstance();
            System.out.println((System.nanoTime() - start));
        }


        System.out.println();
    }

}
