package com.mifan.article.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/7
 */
@Aspect
@Component
public class QuartzAspect {

    @Value("${application.admin.quartz.enable}")
    private boolean adminQuartzEnable;

    @Pointcut("execution(* com.mifan.article.service.QuartzJobsService.*(..))")
    public void check() {
    }

    @Before("com.mifan.article.aspect.QuartzAspect.check()")
    public void doAccessCheck() {
        if (!adminQuartzEnable) {
            throw new IllegalStateException("调度中心管理没有开启, 不能访问, 请检查[application.admin.quartz.enable]参数!");
        }
    }

}
