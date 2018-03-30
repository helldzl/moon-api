package com.mifan.article;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.config.ChannelsEndpointAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>ChannelsEndpointAutoConfiguration中的Endpoint与业务bean的/channels同名, 需要手工exclude掉, 或重写换个名称</p>
 *
 * @author quzile
 * @version 2.0
 * @since 2018/01/17
 */
@EnableScheduling
@EnableFeignClients
@SpringBootApplication(exclude = ChannelsEndpointAutoConfiguration.class)
@EnableDiscoveryClient
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);
    }

}
