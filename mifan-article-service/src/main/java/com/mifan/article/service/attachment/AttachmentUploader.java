package com.mifan.article.service.attachment;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.mifan.article.domain.Attachments;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.concurrent.pool.AbstractPool;
import org.moonframework.concurrent.pool.Task;
import org.moonframework.jsch.JschFactoryBean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/7/21
 */
@Component
public class AttachmentUploader extends AbstractPool<AttachmentUploader.UploadFile, Boolean> implements DisposableBean {

    private final Log logger = LogFactory.getLog(AttachmentUploader.class);

    /**
     * 用于生成SSH会话的factory bean
     */
    private JschFactoryBean jschFactoryBean;

    /**
     * 当前会话
     */
    private Session session;

    /**
     * channel
     */
    private Channel channel;

    @Autowired
    public AttachmentUploader(JschFactoryBean jschFactoryBean) {
        super(1, -1, false);
        this.jschFactoryBean = jschFactoryBean;
    }

    @Override
    public void destroy() throws Exception {
        close();
    }

    /**
     * <p>调用图片上传器, 上传图片到服务器</p>
     *
     * @param is    Input stream
     * @param attachment  Attachments
     */
    public void execute(InputStream is, Attachments attachment, Consumer<Attachments> consumer) throws Exception {
        String link = attachment.getFilename();
        int index = link.lastIndexOf("/");
        if (index != -1) {
            String filename = link.substring(index + 1);
            String[] dirs = link.substring(7, index).split("/");
            dirs[0] = "/" + dirs[0];

            AttachmentUploader.UploadFile uploadFile = new AttachmentUploader.UploadFile();
            uploadFile.setFilename(filename);
            uploadFile.setDirs(Arrays.asList(dirs));
            uploadFile.setInputStream(is);

            Future<Boolean> future = submit(uploadFile);
            if (!Boolean.TRUE.equals(future.get())) {
                // TODO: 2017/9/10 不知道为啥图片传着传着就报错了，所以需要暂时new JschFactoryBean一下
                this.jschFactoryBean = new JschFactoryBean();
                throw new IllegalStateException("upload image error! LINK:" + link);
            }
            else if (consumer != null) {
                consumer.accept(attachment);
            }
        } else {
            throw new IllegalArgumentException("image url formatted error! LINK:" + link);
        }
    }

    public void setJschFactoryBean(JschFactoryBean jschFactoryBean) {
        this.jschFactoryBean = jschFactoryBean;
    }

    @Override
    protected Task<UploadFile, Boolean> newTask() {
        return new AttachmentUploaderHandler(this);
    }

    protected ChannelSftp getChannel() throws Exception {
        if (session == null) {
            session = jschFactoryBean.getObject();
        }
        if (!session.isConnected()) {
            session.connect();
        }
        if (channel == null) {
            channel = session.openChannel("sftp");
        }
        if (!channel.isConnected()) {
            channel.connect();
        }
        return (ChannelSftp) channel;
    }

    /**
     * 上传文件实体
     */
    public static class UploadFile {

        private String filename;
        private Iterable<String> dirs;
        private InputStream inputStream;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public Iterable<String> getDirs() {
            return dirs;
        }

        public void setDirs(Iterable<String> dirs) {
            this.dirs = dirs;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

    }

}
