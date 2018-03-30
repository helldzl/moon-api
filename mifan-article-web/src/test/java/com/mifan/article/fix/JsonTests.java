package com.mifan.article.fix;

import org.moonframework.core.util.ObjectMapperFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonTests {

    private static void b() {
        String json = "{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"#manuList ul li a\",\"fields\":[{\"name\":\"_link\",\"selector\":\"a\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"href\",\"clone\":true,\"stridePageMerge\":false}],\"next\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"div:first-child ul.paging li.next\",\"fields\":[{\"name\":\"_link\",\"selector\":\"a\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"href\",\"clone\":true,\"stridePageMerge\":false}],\"oneToManyLink\":false},{\"primary\":true,\"transfer\":true,\"filter\":true,\"ignoreOnEmpty\":true,\"selector\":\".products div[data-itemid]\",\"fields\":[{\"name\":\"_link\",\"selector\":\".product__name a\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"href\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"tag\",\"selector\":\"div[data-itemid]\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"data-itemid\",\"clone\":true,\"stridePageMerge\":false}],\"next\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"body\",\"name\":\"com.mifan.article.domain.TopicsProduct\",\"fields\":[{\"name\":\"title\",\"selector\":\".product__name>[itemprop=name]\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"category\",\"selector\":\"a[itemprop=item]\",\"index\":-1,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"brand\",\"selector\":\"[itemprop=brand] [itemprop=name]\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"description\",\"selector\":\"div[itemprop=description]\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"content\",\"selector\":\"#desc\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"priceUnit\",\"selector\":\".product-pricing price currency-symbol\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"price\",\"selector\":\"[itemprop=price]\",\"index\":0,\"type\":\"DECIMAL\",\"attr\":\"content\",\"regex\":\"(£|€|\\\\$|,|-.*| )\",\"coefficient\":\"1\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"rating\",\"selector\":\".rating__stars[data-rated]\",\"index\":0,\"type\":\"DOUBLE\",\"attr\":\"data-rated\",\"coefficient\":\"0.2\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"_json_feature\",\"selector\":\".product-specs tr\",\"index\":-1,\"type\":\"OBJECT\",\"clone\":true,\"fields\":[{\"name\":\"_name\",\"selector\":\"th\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"_value\",\"selector\":\"td\",\"index\":0,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false}],\"stridePageMerge\":false},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"1\",\"stridePageMerge\":false},{\"name\":\"images\",\"selector\":\"product-media\",\"index\":0,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"product-media\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"media-json\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"script\":\"function method(o) {var result = [];  o=o.replace(/&quot;/g,\\\"\\\\\\\"\\\");  var obj = JSON.parse(o);var images = obj.images;  for (var i = 0; i < images.length; i++) {var src = images[i].xlarge;if (src != null)  {result.push(src);}} return result.length != 0 ? result : null;}\",\"stridePageMerge\":false}],\"stridePageMerge\":false}],\"oneToManyLink\":false}],\"oneToManyLink\":false}],\"oneToManyLink\":false}]}";

        Map map = ObjectMapperFactory.readValue(json, Map.class);
        List<Map<String, Object>> hits = (List<Map<String, Object>>) map.get("buckets");
        List<String> list = new ArrayList<>();
        for (Map<String, Object> hit : hits) {
            list.add(hit.get("key").toString());
        }
        list.forEach(System.out::println);
        System.out.println();
        String collect = list.stream().collect(Collectors.joining(","));
        System.out.println(collect);
    }


    private static void a() {
        String json = "";

        Map map = ObjectMapperFactory.readValue(json, Map.class);
        List<Map<String, Object>> hits = (List<Map<String, Object>>) map.get("hits");
        List<String> list = new ArrayList<>();
        for (Map<String, Object> hit : hits) {
            list.add(hit.get("_id").toString());
        }
        list.forEach(System.out::println);
        System.out.println();
        System.out.println(list.size());
        System.out.println();
        String collect = list.stream().collect(Collectors.joining(","));
        System.out.println(collect);
    }

    public static void main(String[] args) {
        // a();

        String a = "adsf ,dfdf ,dfsf, sfe";
        int index;
        do {
            System.out.println("CAT:" + a);
        } while ((index = a.lastIndexOf(",")) != -1 && (a = a.substring(0, index).trim()).length() != 0);
        System.out.println("finished");
    }


}
