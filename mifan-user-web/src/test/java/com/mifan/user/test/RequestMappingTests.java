package com.mifan.user.test;

import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/25
 */
public class RequestMappingTests {

    private static final String PACKAGE = "com.mifan.user.web.";
    private static final String PATH = "C:\\Users\\quzile\\IdeaProjects\\mifan\\code\\mifan-api\\branches\\branch-2.3.0\\mifan-user-web\\src\\main\\java\\com\\mifan\\user\\web";
    private static final String MICRO_ROUTE = "/user";
    private static final Set<String> excludes = new LinkedHashSet<String>() {{
        add("com.mifan.user.web.FileUploadController");
    }};

    public static void main(String[] args) throws ClassNotFoundException {
        File parent = new File(PATH);
        Set<String> set = new LinkedHashSet<>();
        Set<String> error = new LinkedHashSet<>();
        for (File file : parent.listFiles()) {
            String name = file.getName().substring(0, file.getName().indexOf("."));
            name = PACKAGE + name;
            if (excludes.contains(name)) {
                continue;
            }

            Class<?> clazz = Class.forName(name);
            boolean present = clazz.isAnnotationPresent(RequestMapping.class);
            String prefix = "";
            if (present) {
                RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
                String[] value = annotation.value();
                if (value.length != 0) {
                    prefix = value[0];
                }
            } else {
                error.add("RequestMapping not present !!!" + name);
            }

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(RequestMapping.class)) {

                    RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                    String[] value = annotation.value();
                    String mapping;
                    if (value.length != 0) {
                        if (!value[0].startsWith("/")) {
                            throw new IllegalStateException();
                        }
                        mapping = prefix + value[0];
                    } else {
                        mapping = prefix;
                    }

                    if (!set.contains(mapping)) {
                        set.add(mapping);
                    }
                }
            }
        }

        // old api
        for (String s : set) {
            System.out.println(s);
        }

        System.out.println();

        // new api
        for (String s : set) {
            String result;
            if (s.startsWith(MICRO_ROUTE)) {
                result = String.format("/{version}%s", s);
            } else {
                result = String.format("/{version}%s%s", MICRO_ROUTE, s);
            }
            System.out.println(result);
        }

        System.out.println();

        // error
        for (String s : error) {
            System.out.println(s);
        }
    }

}
