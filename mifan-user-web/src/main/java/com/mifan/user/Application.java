package com.mifan.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * <p>The @SpringBootApplication annotation is equivalent to using @Configuration, @EnableAutoConfiguration and @ComponentScan with their default attributes:</p>
 * <p>@SpringBootApplication same as @Configuration @EnableAutoConfiguration @ComponentScan</p>
 * <p>Many Spring configuration examples have been published on the Internet that use XML configuration. Always try to use the equivalent Java-base configuration if possible. Searching for enable* annotations can be a good starting point.</p>
 * <p>You don’t need to put all your @Configuration into a single class. The @Import annotation can be used to import additional configuration classes. Alternatively, you can use @ComponentScan to automatically pickup all Spring components, including @Configuration classes.</p>
 * <p>If you absolutely must use XML based configuration, we recommend that you still start with a @Configuration class. You can then use an additional @ImportResource annotation to load XML configuration files.</p>
 * <p>Spring Boot auto-configuration attempts to automatically configure your Spring application based on the jar dependencies that you have added.</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2018/01/17
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);
    }

}
