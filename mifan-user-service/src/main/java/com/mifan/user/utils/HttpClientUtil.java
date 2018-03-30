/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.utils.HttpClientUtil
 *
 * @description:httpclient 用于请求短信服务商
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月11日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.user.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;


/**
 * @author ZYW
 *
 */
public class HttpClientUtil {
    private static CloseableHttpClient httpClient;
    
    static {
        httpClient = newInstance();
    }
    
    public static CloseableHttpClient getHttpClient(){
        return httpClient;
    }
    private static CloseableHttpClient newInstance() {
        // 创建requestConfig
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)//连接超时时间
                .setConnectionRequestTimeout(5000)//从池中获取连接超时时间
                .setSocketTimeout(10000)//读取超时时间（等待数据超时时间）
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(300);//设置连接池最大数量 
        cm.setDefaultMaxPerRoute(80);//设置单个路由最大连接数量(如果值访问同一个连接，则可设置为与maxtotal相同)
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
                                                    .setDefaultRequestConfig(requestConfig)
                                                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))//设置重试次数为0
                                                    .build();
        return httpClient;
    }
    public static String execute(HttpUriRequest request) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(request);
            HttpEntity entity = httpResponse.getEntity();
            if(entity != null) {//注意：这里默认只有entity不为空的情况下才请求成功，因为我们访问的服务器都有返回信息。但是有些服务器没有entity也是执行成功，所以以后如果要用这个方法，请注意
                StatusLine status = httpResponse.getStatusLine();
                if (!(status.getStatusCode() >= 200 && status.getStatusCode() < 300)) {
                    EntityUtils.consumeQuietly(entity);
                    return null;
                }
                String json = EntityUtils.toString(entity, "UTF-8");
                return json; 
            }else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
