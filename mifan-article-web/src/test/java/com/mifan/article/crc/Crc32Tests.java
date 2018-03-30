package com.mifan.article.crc;

import java.nio.charset.Charset;
import java.util.zip.CRC32;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/23
 */
public class Crc32Tests {

    public static void main(String[] args) {
        long n = 1L << 32;
        for (int i = 0; i < 10000000; i++) {
            CRC32 crc32 = new CRC32();
            crc32.update(String.valueOf(i).getBytes(Charset.defaultCharset()));
            Long l = crc32.getValue();
            System.out.println(l);
            if (l < 0 || l > n) {
                throw new IllegalArgumentException();
            }
        }
    }

}
