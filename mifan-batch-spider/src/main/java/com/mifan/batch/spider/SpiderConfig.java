package com.mifan.batch.spider;

import com.mifan.batch.spider.attachment.AttachmentClient;
import com.mifan.batch.spider.attachment.AttachmentDownloader;
import com.mifan.batch.spider.attachment.downloader.DownloaderClient;
import com.mifan.batch.spider.filter.RedisFilterAdapter;
import com.mifan.batch.spider.parser.WebParser;
import com.mifan.batch.spider.service.AttachmentsService;
import com.mifan.batch.spider.storage.DataPersistence;
import org.moonframework.core.amqp.Message;
import org.moonframework.crawler.facade.Spider;
import org.moonframework.crawler.fetcher.Fetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/10
 */
@Configuration
@EnableAspectJAutoProxy
@EnableAsync
@EnableScheduling
public class SpiderConfig {

    @Autowired
    private Environment environment;
    /**
     * <p>下载器</p>
     */
    @Bean
    public AttachmentClient attachmentClient(AttachmentsService attachmentsService) {
        AttachmentDownloader attachmentDownloader = new AttachmentDownloader(1, Integer.parseInt(environment.getProperty("application.permits.downloader")));
        attachmentDownloader.setAttachmentsService(attachmentsService);
        return new AttachmentClient(attachmentDownloader);
    }
    @Bean
    public DownloaderClient downloaderClient(Message message){
        DownloaderClient downloaderClient =new DownloaderClient(message);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    downloaderClient.xiaofei();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return downloaderClient;
    }

    /**
     * <p>爬虫配置</p>
     * <ol>
     * <li>下载器</li>
     * <li>解析器</li>
     * <li>过滤器</li>
     * <li>存储器</li>
     * </ol>
     *
     * @param template template
     * @return Spider
     */

    @Bean
    public Spider spider(StringRedisTemplate template, Message message, AttachmentClient attachmentClient, AttachmentsService attachmentsService, DownloaderClient downloaderClient) {
        // fetcher
        Fetcher fetcher = new Fetcher(1, Integer.parseInt(environment.getProperty("application.permits.fetcher")));


        //
        DataPersistence persistence = new DataPersistence(template);
        persistence.setMessage(message);

        // parser
        WebParser parser = new WebParser(1, 1000);
        parser.setLinkFilter(new RedisFilterAdapter(template) );
        parser.setFetcher(fetcher);
        parser.setAttachmentsClient(attachmentClient);
        parser.setPersistence(persistence);
        parser.setAttachmentsService(attachmentsService);
        parser.setDownloaderClient(downloaderClient);
        // parser.setPersistence(new DefaultPersistence()); // 测试用
        // parser.setAnalyzer(new DefaultPageAnalyzer()); // 这是分析器

        // filters : 1 robots.txt filter, 2 url filter
        fetcher.setParser(parser);
        fetcher
                // .addFilter(new RobotFilterAdapter())
                .addFilter(new RedisFilterAdapter(template));
        return new Spider(fetcher, parser);
    }



}
