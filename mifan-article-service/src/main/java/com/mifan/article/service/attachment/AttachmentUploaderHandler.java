package com.mifan.article.service.attachment;

import com.jcraft.jsch.ChannelSftp;
import org.moonframework.concurrent.pool.TaskAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/9/12
 */
public class AttachmentUploaderHandler extends TaskAdapter<AttachmentUploader.UploadFile, Boolean> {

    private AttachmentUploader uploader;
    private boolean local;

    public static void inputStreamToFile(InputStream inputStream, File file) throws IOException {
        file.getParentFile().mkdirs();
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[8192];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }

    public AttachmentUploaderHandler(AttachmentUploader uploader) {
        this.uploader = uploader;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    @Override
    protected Boolean call(AttachmentUploader.UploadFile uploadFile) throws Exception {
        if (local) {
            return local(uploadFile);
        } else {
            return remote(uploadFile);
        }
    }

    @Deprecated
    protected Boolean local(AttachmentUploader.UploadFile uploadFile) throws IOException {
        try (InputStream inputStream = uploadFile.getInputStream()) {
            // test E:
            StringBuilder sb = new StringBuilder("E:");
            for (String path : uploadFile.getDirs()) {
                sb.append("\\");
                sb.append(path);
            }
            sb.append("\\");
            sb.append(uploadFile.getFilename());
            inputStreamToFile(inputStream, new File(sb.toString()));
            logger.info(sb.toString());
            return Boolean.TRUE;
        }
    }

    protected Boolean remote(AttachmentUploader.UploadFile uploadFile) throws Exception {
        try (InputStream inputStream = uploadFile.getInputStream()) {
            ChannelSftp channelSftp = uploader.getChannel();
            for (String path : uploadFile.getDirs()) {
                try {
                    channelSftp.cd(path);
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Make dir : " + path);
                    }

                    channelSftp.mkdir(path);
                    channelSftp.cd(path);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Upload file : " + uploadFile.getFilename());
            }
            channelSftp.put(inputStream, uploadFile.getFilename());
            return Boolean.TRUE;
        }
    }


}
