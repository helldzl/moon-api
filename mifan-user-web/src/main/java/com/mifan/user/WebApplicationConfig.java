package com.mifan.user;


import org.moonframework.core.factory.SecureRandomFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * <p>Web Application Configuration</p>
 * <p>To enable @AspectJ support with Java @Configuration add the @EnableAspectJAutoProxy annotation:</p>
 * <p>To enable support for @Scheduled and @Async annotations add @EnableScheduling and @EnableAsync to one of your @Configuration classes:</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2016/06/13
 */
@Configuration
//@EnableAspectJAutoProxy
//@EnableAsync(proxyTargetClass = true)
@EnableScheduling
public class WebApplicationConfig {

    private final Environment environment;

    @Autowired
    public WebApplicationConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4, SecureRandomFactory.getInstance());
    }

}
