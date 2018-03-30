package com.mifan.batch.spider;

import com.mifan.batch.AbstractTests;
import com.mifan.batch.spider.attachment.AttachmentClient;
import com.mifan.batch.spider.attachment.downloader.DownloaderClient;
import com.mifan.batch.spider.domain.Attachments;
import org.junit.Test;
import org.moonframework.amqp.Resource;
import org.moonframework.core.amqp.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LiuKai on 2017/9/20.
 */
public class AmqpTests extends AbstractTests {
    @Autowired
    private RabbitTemplate template;
    @Value("${moon.amqp.rabbit.client-data-queue}")
    private  String client_data_queue;
    @Value("${moon.amqp.rabbit.client-data-exchange}")
    private  String client_data_exchange;
    @Value("${moon.amqp.rabbit.client-event-exchange}")
    private  String client_event_exchange;
    @Value("${moon.amqp.rabbit.client-event-queue}")
    private String  client_event_queue;
    @Value("${moon.amqp.rabbit.server-data-queue}")
    private  String server_data_queue;
    @Value("${moon.amqp.rabbit.server-data-exchange}")
    private  String server_data_exchange;

    @Autowired
    private Environment env;

    @Autowired
    private Message message;
    @Autowired
    private AttachmentClient attachmentClient;
    @Autowired
    private DownloaderClient downloaderClient;

    /**
     * <p>发送获取图片为enable=0,重试次数小于3的事件</p>
     *
     * @throws Exception
     */
    @Test
    public void testSendEvent() throws Exception {
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(UUID.randomUUID().toString());

        Map<String, Object> meta = new HashMap<>();
        meta.put(Resource.META_DATETIME, LocalDateTime.now());
        meta.put(Resource.META_EVENT, "trigger_post_attachments");

        Resource resource = new Resource(meta, null);
        template.convertAndSend(client_data_exchange, client_data_queue, resource, correlationData);
    }

    @Test
    public  void testPicAgainSpider(){
        Long id =0l;
        Map<String, String> meta=getMeta(id.toString());
        Map<String, Object> result = message.sendAndReceive(meta,null, null);
        Map<String, Object> map001=new HashMap<>();
        int size=0;
        if (result.get("1") instanceof Map) {
            map001 = ((Map<String, Object>) result.get("1"));
            size=map001.size();
        }else{
            System.out.println("没有图片需要下载");
            size=0;
        }
        while (size>0) {
            Map<String, Object> map002= (Map<String, Object>) result.get("1");
            for (int i = 0; i < size - 1; i++) {
                Attachments attachment = new Attachments();
                Map<String, Object> attributes= (Map<String, Object>) map002.get(i);;
                Map<String, Object> from= (Map<String, Object>) attributes.get("from");
                attachment.setGroupId(Integer.parseInt(attributes.get("groupId").toString()));
                attachment.setFilename(attributes.get("filename").toString());
                attachment.setId(Long.parseLong(attributes.get("id").toString()));
                attachment.setCreator(Long.parseLong(attributes.get("creator").toString()));
                if (attributes.get("description")!=null)
                    attachment.setDescription(attributes.get("description").toString());
                attachment.setEnabled(0);
                attachment.setExtension(attributes.get("extension").toString());
                if (attributes.get("title")!=null)
                    attachment.setTitle(attributes.get("title").toString());
                attachment.setModified(new Date());
                attachment.setModifier(0L);
                attachment.setGroupId(Integer.parseInt(attributes.get("groupId").toString()));
                attachment.setRetry(Integer.parseInt(attributes.get("retry").toString()));
                if (attributes.get("extra")!=null)
                    attachment.setExtra(attributes.get("extra").toString());
                attachment.setDownload(Integer.parseInt(attributes.get("download").toString()));
                if (attributes.get("filesize")!=null)
                    attachment.setFilesize(Integer.parseInt(attributes.get("filesize").toString()));
                attachment.setMime(attributes.get("mime").toString());
                if (from!=null&&from.get("origin")!=null) {
                    attachment.setOrigin(from.get("origin").toString());
                    attachment.setOriginHash(Long.parseLong(from.get("originHash").toString()));
                }
                attachment.setAgencyIp("127.0.0.1");
                attachment.setAgencyIpPort(9999);
                if(attachment.getOrigin()!=null) {
                    attachmentClient.execute(attachment);
                }else{
                    System.out.println(attachment.getId());
                }
            }
            result = message.sendAndReceive(getMeta(((Map<String, Object>) map001.get(size - 1)).get("id").toString()), null, null);
            if (result.get("1") instanceof Map){
                size=((Map<String, Object>)result.get("1")).size();
            }else {
                size=0;
            }
        }
    }

    private Map<String, String> getMeta(String id){
        Map<String, String> meta = new HashMap<>(5);
        meta.put("enabled","0" );
        meta.put("retry","3");
        meta.put("pictureAgain","1");
        meta.put("id",id);
        meta.put("className", "com.mifan.article.domain.Attachments");
        return  meta;
    }
    @Test
    public void testThread001(){
        Map<Long, Object> map = new HashMap<>();
        map.put(11l,"dfdf");
        downloaderClient.add(map);
        System.out.println(downloaderClient.toString());
        System.out.println("dd");
    }
}
