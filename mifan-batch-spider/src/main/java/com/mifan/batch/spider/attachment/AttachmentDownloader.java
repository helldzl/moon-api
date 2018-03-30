package com.mifan.batch.spider.attachment;

import com.mifan.batch.spider.domain.Attachments;
import com.mifan.batch.spider.service.AttachmentsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moonframework.concurrent.pool.AbstractPool;
import org.moonframework.concurrent.pool.Task;
import org.moonframework.concurrent.util.LockUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by LiuKai on 2017/7/6.
 */
@Service
public class AttachmentDownloader extends AbstractPool<Attachments, Void> implements Runnable {
    private final Log logger = LogFactory.getLog(AttachmentDownloader.class);
    private AttachmentsService attachmentsService;
    private ReentrantLock lock = new ReentrantLock();

    private LinkedBlockingQueue<Attachments> queue = new LinkedBlockingQueue<>();

    public AttachmentDownloader() {
    }

    public AttachmentDownloader(int size) {
        super(size);
    }

    public AttachmentDownloader(int size, int permits) {
        super(size, permits);
    }

    public AttachmentDownloader(int size, int permits, boolean fair) {
        super(size, permits, fair);
    }


    @Override
    public void execute(Attachments attachment) {
        super.execute(attachment);
        while ((attachment = queue.poll()) != null) {
            super.execute(attachment);
        }
    }

    public boolean add(Attachments attachment) {
        return queue.add(attachment);
    }


    @Override
    public void run() {
        LockUtils.tryLock(lock, () -> {
            if (logger.isInfoEnabled()) {
                logger.info("Attachment Downloader is start");
            }

            int count = 0;
            while (!isShutdown()) {
                try {
                    if (count++ % 32 == 0) {
                        logger.info(getSemaphore());
                        execute(queue.take());
                    }
                } catch (InterruptedException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("error", e);
                    }
                }
            }
            if (logger.isInfoEnabled()) {
                logger.info("Attachment Downloader is shutdown");
            }

            return null;
        });
    }

    // get and set method

    public AttachmentsService getImagesService() {
        return attachmentsService;
    }

    public void setAttachmentsService(AttachmentsService attachmentsService) {
        this.attachmentsService = attachmentsService;
    }

    @Override
    protected Task<Attachments, Void> newTask() {
        return new AttachmentDownloadHandler(this, attachmentsService);
    }

}