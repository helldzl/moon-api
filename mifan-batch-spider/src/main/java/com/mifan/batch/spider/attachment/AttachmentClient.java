package com.mifan.batch.spider.attachment;


import com.mifan.batch.spider.domain.Attachments;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/7/21
 */

public class AttachmentClient implements AutoCloseable, InitializingBean, DisposableBean {

    private final Log logger = LogFactory.getLog(AttachmentClient.class);

    private ExecutorService service = Executors.newSingleThreadExecutor();
    private  AttachmentDownloader downloader;

    public AttachmentClient(AttachmentDownloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public void afterPropertiesSet() {
        service.submit(downloader);
    }

    @Override
    public void destroy() throws Exception {
        close();
    }

    /**
     * <p>阻塞方法</p>
     *
     * @param attachments
     * @return
     */
    public void execute(Attachments attachments) {
        downloader.execute(attachments);
    }

    /**
     * <p>非阻塞方法</p>
     *
     * @param attachments
     */
    public void add(Attachments attachments) {
        downloader.add(attachments);
    }


    @Override
    public void close() throws IOException {
        service.shutdown();
        downloader.close();
        logger.info("Attachment client closed");
    }

}
