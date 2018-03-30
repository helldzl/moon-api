package com.mifan.article.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mifan.article.service.SpiderService;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by LiuKai on 2017/11/17.
 */
@Service
public class SpiderServiceImpl implements SpiderService {
    @Value("${spring.rabbitmq.host}")
    private String spring_rabbitmq_host_intranet;
    @Value("${spring.rabbitmq.username}")
    private String spring_rabbitmq_username;
    @Value("${spring.rabbitmq.password}")
    private String spring_rabbitmq_password;

    @Override
    public JSONArray getRabbitMqConnectionsInfo() {
        JSONArray jsonLottery = (JSONArray) JSONObject.parse(getHttpClientBasicAuthen());
        return jsonLottery;
    }

    private String getHttpClientBasicAuthen() {
        String str = "";
        // 凭据提供器
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                // 认证范围
                new AuthScope(spring_rabbitmq_host_intranet, 15672),
                // 认证用户名和密码
                new UsernamePasswordCredentials(spring_rabbitmq_username, spring_rabbitmq_password));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            //GET请求
            HttpGet httpget = new HttpGet("http://" + spring_rabbitmq_host_intranet + ":15672/api/connections");
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    str = EntityUtils.toString(response.getEntity());
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }
}
