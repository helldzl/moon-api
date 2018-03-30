package com.mifan.article.service.util;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mifan.article.domain.AttachmentsFetch;

import java.nio.charset.Charset;

/**
 * Created by quzile on 2016/9/6.
 */
public final class EntityUtils {

    private static final HashFunction CRC32 = Hashing.crc32();
    private static final HashFunction MD5 = Hashing.md5();

    public static AttachmentsFetch newImages(String origin) {
        AttachmentsFetch attachmentsFetch = new AttachmentsFetch();
        attachmentsFetch.setOrigin(origin);
        attachmentsFetch.setOriginHash(asLong(origin));
        return attachmentsFetch;
    }

    public static int asInt(String s) {
        return CRC32.newHasher().putString(s, Charset.defaultCharset()).hash().asInt();
    }

    public static long asLong(String s) {
        return MD5.newHasher().putString(s, Charset.defaultCharset()).hash().asLong();
    }

}
