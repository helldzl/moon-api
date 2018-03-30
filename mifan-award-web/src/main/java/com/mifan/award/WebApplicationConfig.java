package com.mifan.award;


import org.moonframework.core.toolkit.idworker.IdWorker;
import org.moonframework.core.toolkit.idworker.IdWorkerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

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
@EnableAspectJAutoProxy
@EnableAsync
@EnableScheduling
public class WebApplicationConfig {

    @Autowired
    private Environment environment;

    @Bean
    public IdWorker idworker() {
        return new IdWorker(
                Long.parseLong(environment.getProperty("application.idworker.workerId")),
                Long.parseLong(environment.getProperty("application.idworker.datacenterId")));
    }

    @Bean
    public IdWorkerUtil idworkerutil(IdWorker idworker) {
        IdWorkerUtil idworkerutil = new IdWorkerUtil();
        idworkerutil.setIdWorker(idworker);
        return idworkerutil;
    }

}
