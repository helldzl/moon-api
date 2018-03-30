package com.mifan.reward;

import org.moonframework.core.toolkit.idworker.IdWorker;
import org.moonframework.core.toolkit.idworker.IdWorkerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableAspectJAutoProxy
@EnableAsync
@EnableScheduling
@EnableSwagger2
public class WebApplicationConfig {

    private final Environment environment;

    @Autowired
    public WebApplicationConfig(Environment environment) {
        this.environment = environment;
    }

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
