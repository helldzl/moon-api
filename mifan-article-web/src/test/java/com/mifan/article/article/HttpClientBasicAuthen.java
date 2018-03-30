package com.mifan.article.article;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Created by LiuKai on 2017/11/17.
 */
public class HttpClientBasicAuthen {
    public static void main(String[] args) throws Exception {
        // 凭据提供器
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                // 认证范围
                new AuthScope("192.168.1.138", 15672),
                // 认证用户名和密码
                new UsernamePasswordCredentials("rabbitmq", "rabbitmq"));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            //GET请求
            HttpGet httpget = new HttpGet("http://192.168.1.138:15672/api/connections");

            System.out.println("执行请求：" + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                System.out.println("----------------------------------------");
                System.out.println("响应：" + response.getStatusLine());
                System.out.println("内容：" + EntityUtils.toString(response.getEntity()));
                System.out.println("----------------------------------------");
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
}
