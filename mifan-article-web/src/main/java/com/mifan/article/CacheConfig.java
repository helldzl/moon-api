package com.mifan.article;

import net.sf.ehcache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/15
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
public class CacheConfig {

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation(new ClassPathResource("ehcache/ehcache.xml"));
        factoryBean.setCacheManagerName("applicationCacheManager");
        return factoryBean;
    }

    @Bean
    public EhCacheCacheManager ehCacheCacheManager(CacheManager cacheManager) {
        EhCacheCacheManager manager = new EhCacheCacheManager();
        manager.setCacheManager(cacheManager);
        return manager;
    }

//    @Primary
//    @Bean
//    public RedisCacheManager cacheManager(RedisTemplate redisTemplate) {
//        RedisCacheManager manager = new RedisCacheManager(redisTemplate);
//        manager.setUsePrefix(true);
//        return manager;
//    }

//    @Bean
//    public ConcurrentMapCacheFactoryBean concurrentMapCacheFactoryBean() {
//        ConcurrentMapCacheFactoryBean bean = new ConcurrentMapCacheFactoryBean();
//        bean.setName("article:seeds");
//        return bean;
//    }
//
//    @Bean
//    public SimpleCacheManager simpleCacheManager() {
//        SimpleCacheManager manager = new SimpleCacheManager();
//        manager.setCaches(Arrays.asList(concurrentMapCacheFactoryBean().getObject()));
//        return manager;
//    }

}
