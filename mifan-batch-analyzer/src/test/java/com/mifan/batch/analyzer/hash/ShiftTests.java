package com.mifan.batch.analyzer.hash;

import java.util.function.Consumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/6/16
 */
public class ShiftTests {

    // private static int[] and = {0x1FFF, 0x1FFF, 0x1FFF, 0x1FFF, 0x0FFF};
    private static int[] and = {0xFFFF, 0xFFFF, 0xFFFF, 0xFFFF};

    private static void block(long n, Consumer<String> consumer) {
        // IntStream.range(0, 4).forEach(block -> consumer.accept(key(block, (int) (n >> (block * 16) & 0xFFFF))));
        for (int shift = 0, i = 0; i < and.length; shift += Long.bitCount(and[i]), i++) {
            consumer.accept(key(i, (int) (n >> shift & and[i])));
        }
    }

    private static String key(int index, int query) {
        return String.format("%s.block.%s.%s", "product", index, query);
    }

    public static void main(String[] args) {
        long l = 1234567890101112L;
        String s = Long.toBinaryString(l);
        System.out.println(s);
        block(l, s1 -> {
            String substring = s1.substring(s1.lastIndexOf(".") + 1);
            String s2 = Integer.toBinaryString(Integer.parseInt(substring));
            System.out.println(s2);
        });
    }

}
