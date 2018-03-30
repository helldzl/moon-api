package com.mifan.reward.service.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

public class HttpClientUtil {
    @Value("${mifan.proxy.server}")
    private static String proxyServer;

    @Value("${mifan.proxy.port}")
    private static int proxyPort;

    @Value("${mifan.proxy.isUse}")
    private static boolean isUse;

    public static String getHttpClient(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(url);
            // 上外网的代理配置
            if(isUse) {
                HttpHost proxy = new HttpHost(proxyServer, proxyPort);
                RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
                httpget.setConfig(config);
            }

            // 执行get请求.
            response = httpclient.execute(httpget);
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 打印响应内容
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                response.close();
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String postHttpClient(String url, List<NameValuePair> formparams) {
        // 创建默认的httpClient实例.    
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost    
        HttpPost httppost = new HttpPost(url);
        // 上外网的代理配置
        if(isUse) {
            HttpHost proxy = new HttpHost(proxyServer, proxyPort);
            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            httppost.setConfig(config);
        }
        // 创建参数队列
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity, "UTF-8");
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源    
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
