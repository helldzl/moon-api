package com.mifan.article.article;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/3
 */
public class StreamTests {

    private static Map<String, Object> add1() {
        Map<String, Object> map = new HashMap<>();
        map.put("vvet", "a");
        map.put("etdet", "b");
        map.put("aabg", "b");
        map.put("ccdr", "b");
        return map;
    }

    private static Map<String, Object> add2() {
        Map<String, Object> map = new HashMap<>();
        map.put("ccdr", "a");
        map.put("c", "c");
        return map;
    }

    public static void main(String[] args) {
        List<Map<String, Object>> items = new ArrayList<>();
        items.add(add1());
        items.add(add2());

        Map<String, ArrayList> box = new TreeMap<>();
        items.stream().flatMap(item -> item.keySet().stream()).forEach(key -> box.computeIfAbsent(key, k -> new ArrayList()));
        System.out.println();

        Map<String, ArrayList> collect = items.stream().flatMap(item -> item.keySet().stream()).collect(Collectors.toMap(s -> s, s -> new ArrayList()));
        System.out.println(collect);
    }


}
