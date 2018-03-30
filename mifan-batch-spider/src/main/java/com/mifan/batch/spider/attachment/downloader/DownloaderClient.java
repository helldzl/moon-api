package com.mifan.batch.spider.attachment.downloader;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.core.amqp.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by LiuKai on 2017/12/7.
 */
@Service
public class DownloaderClient {
    private final Log logger = LogFactory.getLog(DownloaderClient.class);
    private Message message;
    private static ExecutorService pool = Executors.newFixedThreadPool(10);
    @Autowired
    private StringRedisTemplate template;
    public DownloaderClient(Message message) {
        this.message = message;
    }



    public Message getMessage() {
        return message;
    }

    public LinkedBlockingQueue<Map<Long, Object>> queue1 = new LinkedBlockingQueue<>();

    public void add(Map<Long, Object> map) {
        queue1.add(map);
    }

    public void xiaofei() throws InterruptedException {
        while (true){
            execute(queue1.take());
        }
    }
    public  void execute(Map<Long, Object> map) {
        Callable c = new DownloaderImage(message,template,map);
        Future future=pool.submit(c);
        try {
            logger.info("future返回值："+future.get(20,TimeUnit.SECONDS));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            logger.info("返回超时");
        }
    }
}
