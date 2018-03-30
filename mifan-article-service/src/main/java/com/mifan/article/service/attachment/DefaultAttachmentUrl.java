package com.mifan.article.service.attachment;

import org.apache.commons.codec.digest.DigestUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/10/10
 */
public class DefaultAttachmentUrl implements AttachmentUrl {

    @Override
    public String apply(String baseUrl, String source) {
        try {
            return url(baseUrl, Long.valueOf(source));
        } catch (Exception e) {
            return url(baseUrl, source);
        }
    }

    public String url(String baseUrl, String source) {
        int index = source.lastIndexOf(".");
        if (index == -1) {
            return null;
        }

        String value = DigestUtils.md5Hex(source).toUpperCase();
        String suffix = source.substring(index).toLowerCase();
        String filename = value + suffix;

        //
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        sb.append(new SimpleDateFormat("yyyyMM/dd/").format(new Date()));

        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i += 2) {
            sb.append(chars[i]);
            sb.append(chars[i + 1]);
            sb.append("/");
        }
        return sb.append(filename).toString();
    }

    public String url(String baseUrl, Long id) {
        StringBuilder sb = new StringBuilder(baseUrl);
        long n = id;
        while ((n >>= 8) > 0) {
            sb.append(n & 0xFF).append('/');
        }
        sb.append(id).append(".jpg");
        return sb.toString();
    }

}
