package com.mifan.batch.spider.attachment.downloader;

import com.mifan.batch.spider.attachment.ImageColor;
import com.mifan.batch.spider.domain.Attachments;
import com.mifan.batch.spider.service.AttachmentsService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.moonframework.core.amqp.Message;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.core.util.ObjectMapperFactory;
import org.moonframework.crawler.util.HttpClientUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiuKai on 2017/12/7.
 */
public class DownloaderHandler {
    private Message message;
    private final Log logger = LogFactory.getLog(DownloaderHandler.class);


    private StringRedisTemplate template;

    public void setTemplate(StringRedisTemplate template) {
        this.template = template;
    }

    @Autowired
    private AttachmentsService attachmentsService;

    public DownloaderHandler(Message message, StringRedisTemplate template) {
        this.message = message;
        this.template = template;
    }

    public DownloaderHandler(Message message) {
        this.message = message;
    }

    public Long download(List<Attachments> attachmentsList) {
        for (Attachments attachment : attachmentsList) {
            long start = System.currentTimeMillis();
            HttpGet request = new HttpGet(HttpClientUtils.newInstance(attachment.getOrigin()));
            if (attachment.getAgencyIp() != null && attachment.getAgencyIpPort() != null) {
                HttpHost proxy = new HttpHost(attachment.getAgencyIp(), attachment.getAgencyIpPort());
                RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
                request.setConfig(config);
            }
            HttpClientUtils.execute(request, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(HttpResponse response) {
                    try {
                        int status = response.getStatusLine().getStatusCode();
                        if (status >= 200 && status < 300) {
                            byte[] bytes = IOUtils.toByteArray(response.getEntity().getContent());
                            try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                                BufferedImage bufferedImage = ImageIO.read(inputStream);
                                if (bufferedImage == null) {
                                    throw new IllegalStateException("Status Code:" + status + " 实际无图片内容:" + attachment.getId() + ":" + attachment.getOrigin());
                                }
                                attachment.setDescription("{\"height\":" + bufferedImage.getHeight() + ",\"width\":" + bufferedImage.getWidth() + "}");
                                attachment.setEnabled(1);
                                // 图片颜色距离相似度评分
                                attachment.setExtra(ObjectMapperFactory.writeValueAsString(ImageColor.score(bufferedImage, DownloaderHandler::distance)));
                                attachment.setBytes(bytes);

                                // [状态持久化] 与 [上传文件] 功能合并
                                Long returnLong = saveOrUpdate(attachment);
                            }
                            if (logger.isInfoEnabled()) {
                                logger.info(String.format("Time: (%s)ms -> %s -> %s", (System.currentTimeMillis() - start), request.getRequestLine(), response.getStatusLine()));
                            }
                        } else {
                            throw new IllegalStateException("Status Code: " + status + " Url:" + attachment.getOrigin());
                        }
                    } catch (Exception e) {
                        error(attachment);
                        if (logger.isErrorEnabled()) {
                            logger.error("error", e);
                        }
                    }
                }

                @Override
                public void failed(Exception e) {

                }

                @Override
                public void cancelled() {

                }
            });
        }

        //System.out.println(Thread.currentThread().getName());
        //返回1l,表示图片列表已经从堆类中取出，开始正式下载了，topic的enabled可以自动设置为1,进行重新更新了。
        // 但是有个问题，如果到了这步突然中断了，导致这个文章没有把enabled置为1，如何处理呢？
        return 1L;
    }

    public static double distance(int r1, int g1, int b1, int r2, int g2, int b2) {
        long v = ((long) r1 + (long) r2) / 2;
        long r = r1 - r2;
        long g = g1 - g2;
        long b = b1 - b2;
        return Math.sqrt((((512 + v) * r * r) >> 8) + 4 * g * g + (((767 - v) * b * b) >> 8));
    }

    private void error(Attachments attachment) {
        attachment.setRetry(attachment.getRetry() + 1);
        attachment.setEnabled(0);
        saveOrUpdate(attachment);
    }

    private Long saveOrUpdate(Attachments attachment) {
        Map<String, String> meta = new HashMap<>(3);
        meta.put("name", "images");
        meta.put("origin", attachment.getOrigin());
        meta.put("className", "com.mifan.article.domain.Attachments");

        Map<String, Object> result = message.sendAndReceive(meta, BeanUtils.toMap(attachment), null);

        Attachments target = new Attachments();
        BeanWrapper wrapper = new BeanWrapperImpl(target);
        wrapper.setPropertyValues(result);

        URI uri = URI.create(attachment.getOrigin());
        template.opsForSet().add(uri.getHost() + ":" + "image", attachment.getOrigin());
        return target.getId();
    }
}
