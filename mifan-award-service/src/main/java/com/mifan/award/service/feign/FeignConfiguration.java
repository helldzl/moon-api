package com.mifan.award.service.feign;

import feign.Feign;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.hystrix.HystrixFeign;
import org.moonframework.fragment.security.autoconfigure.TokenProperties;
import org.moonframework.web.feign.DataDecoder;
import org.moonframework.web.feign.Oauth2FeignRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.support.ResponseEntityDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>FooConfiguration does not need to be annotated with @Configuration.</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2018/1/10
 */
@Configuration
public class FeignConfiguration {

    private final TokenProperties properties;

    @Autowired
    public FeignConfiguration(TokenProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return new Oauth2FeignRequestInterceptor(properties.getSalt());
    }

    @Bean
    public Decoder feignDecoder() {
        return new ResponseEntityDecoder(new DataDecoder());
    }

    @Bean
    public Feign.Builder feignBuilder() {
        return new HystrixFeign.Builder().decode404();
    }

}
