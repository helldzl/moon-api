package com.mifan.article;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/27
 */
@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Apache HttpClient supports gzip encoding. To use it, construct a HttpComponentsClientHttpRequestFactory like so:
        HttpClient httpClient = HttpClientBuilder.create().build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }

}
