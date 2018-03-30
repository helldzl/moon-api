package com.mifan.batch.spider.attachment;

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
import org.moonframework.concurrent.pool.TaskAdapter;
import org.moonframework.core.util.ObjectMapperFactory;
import org.moonframework.crawler.util.HttpClientUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.UnknownHostException;

/**
 * Created by LiuKai on 2017/7/6.
 */
public class AttachmentDownloadHandler extends TaskAdapter<Attachments, Void> {
    private static Log logger = LogFactory.getLog(AttachmentDownloadHandler.class);

    /**
     * 图片下载器
     */
    private final AttachmentDownloader downloader;

    /**
     *
     */
    private final AttachmentsService attachmentsService;

    public static double distance(int r1, int g1, int b1, int r2, int g2, int b2) {
        // 灰度化算法
        // return Math.abs(ImageColor.getGray(r1, g1, b1) - ImageColor.getGray(r2, g2, b2));

        // 欧式距离算法
        // long x = r1 - r2;
        // long y = g1 - g2;
        // long z = b1 - b2;
        // return Math.sqrt(x * x + y * y + z * z);

        // 改进的加权欧式距离
        long v = ((long) r1 + (long) r2) / 2;
        long r = r1 - r2;
        long g = g1 - g2;
        long b = b1 - b2;
        return Math.sqrt((((512 + v) * r * r) >> 8) + 4 * g * g + (((767 - v) * b * b) >> 8));
    }

    public AttachmentDownloadHandler(AttachmentDownloader downloader, AttachmentsService attachmentsService) {
        this.downloader = downloader;
        this.attachmentsService = attachmentsService;
    }

    @Override
    protected Void call(Attachments attachment) {
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
                            attachment.setExtra(ObjectMapperFactory.writeValueAsString(ImageColor.score(bufferedImage, AttachmentDownloadHandler::distance)));
                            attachment.setBytes(bytes);

                            // [状态持久化] 与 [上传文件] 功能合并
                            attachmentsService.saveOrUpdate(attachment);
                        }
                        if (logger.isInfoEnabled()) {
                            logger.info(String.format("Time: (%s)ms -> %s -> %s", (System.currentTimeMillis() - start), request.getRequestLine(), response.getStatusLine()));
                        }
                    } else {
                        throw new IllegalStateException("Status Code: " + status + "  attachment_id:" + attachment.getId() + "  Url:" + attachment.getOrigin());
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    error(attachment);
                    logger.error("error", e);
                } finally {
                    downloader.release();
                }
            }

            @Override
            public void failed(Exception e) {
                downloader.release();
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Retry: (%s), %s failed", attachment.getRetry(), request.getRequestLine()), e);
                }

                if (!(e instanceof UnknownHostException) && attachment.getRetry() < Attachments.DEFAULT_RETRY) {
                    attachment.setRetry(attachment.getRetry() + 1);
                    downloader.add(attachment); // 不要使用阻塞方法, 否则在多线程下（permits > 1）有可能发生（New I/O worker）与信号量（Semaphore）之间竞争资源导致的死锁问题
                } else {
                    error(attachment);
                }
            }

            @Override
            public void cancelled() {
                downloader.release();
                if (logger.isInfoEnabled()) {
                    logger.info(request.getRequestLine() + " cancelled");
                }
            }
        });

        return null;
    }

    @Override
    protected void failed(Exception ex) {
        downloader.release();
    }

    private void error(Attachments attachment) {
        attachment.setRetry(attachment.getRetry() + 1);
        attachment.setEnabled(0);
        attachmentsService.saveOrUpdate(attachment);
    }

}