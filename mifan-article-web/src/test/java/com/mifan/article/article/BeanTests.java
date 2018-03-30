package com.mifan.article.article;

import com.mifan.article.domain.support.Multilingual;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/19
 */
public class BeanTests {

    public static void a() {
        Set<Object> singleton = Collections.singleton(null);
        System.out.println(singleton);

        List<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(2);
        a.add(3);
        a.add(4);
        a.add(3);
        BitSet bitSet = new BitSet(20);
        List<Integer> collect = a.stream().map(s -> s).filter(s -> {
            System.out.println("gg");
            return s != null && !bitSet.get(s);
        })
                .peek(integer -> {
                    bitSet.set(integer);
                    System.out.println(integer);
                })
                .collect(Collectors.toList());
        System.out.println(collect);
    }

    public static void b() {
        List<Map<String, String>> l = Multilingual.getList("[{\"en\":\"Synthesizers\",\"cn\":null},{\"en\":\"Keyboards & MIDI\",\"cn\":\"键盘和MIDI\"}]");

        List<String> list = Multilingual.getList(l, Multilingual.DEFAULT_LANGUAGES);
        System.out.println(list);

        List<Object> list1 = Multilingual.getList("[{\"_name\":\"Sound Engine Type(s)\",\"_value\":\"Analog Modeling\"},{\"_name\":\"Polyphony\",\"_value\":\"20 Notes-90 Notes (Depending On the Patch)\"},{\"_name\":\"Number of Presets\",\"_value\":\"512 RAM Patches, 3328 ROM Sounds\"},{\"_name\":\"Number of Effects\",\"_value\":\"192\"},{\"_name\":\"Effects Types\",\"_value\":\"Reverb, Chorus, Delay, Phaser, EQ, Ring Mod\"},{\"_name\":\"Arpeggiator\",\"_value\":\"Yes\"},{\"_name\":\"Analog Inputs\",\"_value\":\"2 x TS\"},{\"_name\":\"Analog Outputs\",\"_value\":\"6 x TS, 1 x TRS (Headphones)\"},{\"_name\":\"Digital Inputs\",\"_value\":\"1 x S/PDIF\"},{\"_name\":\"Digital Outputs\",\"_value\":\"1 x S/PDIF\"},{\"_name\":\"MIDI I/O\",\"_value\":\"In/Out/Thru/USB\"},{\"_name\":\"USB\",\"_value\":\"1 x Type B\"},{\"_name\":\"Height\",\"_value\":\"3.2\\\"\"},{\"_name\":\"Width\",\"_value\":\"18.5\\\"\"},{\"_name\":\"Depth\",\"_value\":\"7.4\\\"\"},{\"_name\":\"Weight\",\"_value\":\"7.4 lbs.\"},{\"_name\":\"Power Supply\",\"_value\":\"Power Supply Included\"},{\"_name\":\"Manufacturer Part Number\",\"_value\":\"Virus TI2 Desk\"}]");
        System.out.println(list1);
    }

    public static void main(String[] args) {
        b();
    }

}
