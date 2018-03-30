package com.mifan.reward;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket rewardApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(true)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mifan.reward.web"))
                .build()
                .apiInfo(rewardApiInfo());
    }

    private ApiInfo rewardApiInfo() {
        return new ApiInfoBuilder()
                .title("米饭夺宝系统数据接口文档")
                .termsOfServiceUrl("http://www.mifanxing.com")
                .description("文档描述。。。")
                .contact(new Contact("mifanxing", "http://www.mifanxing.com", "bd@mifanxing.com"))
                .version("2.3.0")
                .build();
    }

}
