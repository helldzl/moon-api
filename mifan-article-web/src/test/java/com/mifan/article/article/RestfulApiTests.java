package com.mifan.article.article;

import com.mifan.article.AbstractTests;
import com.mifan.article.domain.Attachments;
import com.mifan.article.domain.support.Content;
import com.mifan.article.service.RestService;
import org.junit.Test;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/4
 */
public class RestfulApiTests extends AbstractTests {

    @Autowired
    private RestService restService;

    /**
     * <p>测试机器翻译</p>
     *
     * @throws Exception
     */
    @Test
    public void testRest() {
        Map<String, String> map = new HashMap<>();
        map.put("content", "I am the test for google translate");
        Data<Content> contentData = this.restService.postForEntity(Content.class, "/api/translate", RestService.requestAsMap(map));
        System.out.println();
    }

    @Test
    public void testWatermark() {
        Map<String, String> map = new HashMap<>();
        map.put("image", "http://static.budee.com/yyren/image/7/1987.jpg");
        Data<Map> contentData = this.restService.postForEntity(Map.class, "/api/watermark", RestService.requestAsMap(map));
        System.out.println();
    }

    @Test
    public void testWatermark1() {
        Date from = Date.from(LocalDateTime.of(2016, 7, 7, 1, 1, 1).atZone(ZoneId.systemDefault()).toInstant());
        Services.doList(Attachments.class,
                from,
                new Date(),
                Fields.builder().add("id").add("filename").build(),
                Restrictions.eq(Attachments.MIME, "image/jpeg"),
                list -> {
                    for (Attachments entity : list.getContent()) {
                        System.out.println(entity.getId() + " " + entity.getFilename());
                    }
                });
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            if (!queue.offer("i:" + i, 5, TimeUnit.SECONDS)) {
                System.out.println("error");
            }
        }
        System.out.println("finished");
    }

}
