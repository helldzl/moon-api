package com.mifan.batch.analyzer.hash;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/6/6
 */
public class HashTests {

    public static void a1() {
        long l = Hashing.murmur3_128().newHasher().putString("hello", Charset.defaultCharset()).hash().asLong();
        System.out.println(l);

        l = Hashing.murmur3_128().newHasher().putString("hello", Charset.defaultCharset()).hash().asLong();
        System.out.println(l);
    }

    public static float idf(long docFreq, long numDocs) {
        return (float) (Math.log((double) numDocs / (double) (docFreq + 1L)) + 1.0D);
    }

    public static void main(String[] args) {
        System.out.println(idf(10, 100));
        System.out.println(Math.log(10));
    }

}
