package com.mifan.article.reflect;

import com.mifan.article.domain.Channels;
import org.moonframework.model.mybatis.annotation.Join;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/7
 */
public class ReflectTests {

    private static Map<String, Join> map = new HashMap<>();

    public static void main(String[] args) {
        String watch = "users_channels_watch";
        Channels channel = new Channels();
        for (Field field : channel.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Join.class)) {
                Join annotation = field.getAnnotation(Join.class);
                if (annotation.table().equals(watch)) {
                    map.computeIfAbsent(watch, s -> {
                        System.out.println(s);
                        return annotation;
                    });
                    map.computeIfAbsent(watch, s -> {
                        System.out.println(s);
                        return annotation;
                    });
                }

                System.out.println(annotation);
            }
        }
    }


}
