package com.mifan.article.sku;

import com.google.common.collect.HashMultimap;
import com.mifan.article.domain.support.Users;
import org.junit.Test;
import org.moonframework.model.mybatis.annotation.OneToManyArray;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

//import com.mifan.sku.domain.Brands;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/16
 */
public class JsonTests {

    @Test
    public void test2() {
        Field[] fields = Users.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(OneToManyArray.class)) {
                Annotation[] annotations = field.getAnnotations();
                System.out.println(field);
                for (Annotation annotation : annotations) {
                    System.out.println(annotation.toString());
                }
                System.out.println();
            }
        }
    }

    @Test
    public void a() {
        String name = "user_name_dd_d_d";
        System.out.println(field(name));
    }

    private String field(String field) {
        StringBuilder sb = new StringBuilder();
        char[] chars = field.toCharArray();
        for (int i = 0; i < chars.length; i++)
            sb.append(chars[i] == '_' ? Character.toUpperCase(chars[++i]) : chars[i]);
        return sb.toString();
    }

    public static String getName(String name, boolean convert) {
        String n = name.toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (char c : n.toCharArray()) {
            if ('_' == c) {
                convert = true;
            } else {
                sb.append(convert ? Character.toUpperCase(c) : c);
                convert = false;
            }
        }
        return sb.toString();
    }

    @Test
    public void beanTest() {
        Users entity = new Users();
        entity.setUsername("quzile");
        BeanWrapper company = new BeanWrapperImpl(entity);
        company.setPropertyValue("id", 2L);

        System.out.println(company.getPropertyDescriptor("quzile"));
    }

    @Test
    public void name4() {
        int[] array = {1, 3, 5};
        int n = Arrays.stream(array).reduce(0, (left, right) -> left + right);
        System.out.println(n);
    }

    @Test
    public void name3() {
        Map<Long, Long> map = new HashMap<>();
        map.put(1L, 1L);
        map.put(2L, 2L);
        map.put(3L, 3L);
        map.put(4L, 4L);

        Set<Long> set = new HashSet<>();
        set.add(1L);
        set.add(3L);


        Set<Long> longs = map.keySet();
        longs.removeAll(set);

        System.out.println(map);
        System.out.println();
    }

    @Test
    public void name1() {
        Set<Long> all = new HashSet<>();
        all.add(1L);
        all.add(2L);
        all.add(3L);
        all.add(4L);
        all.add(5L);
        all.add(6L);
        all.add(7L);

        Set<Long> ids = new HashSet<>();
        ids.add(1L);
        ids.add(3L);
        ids.add(10L);

        HashSet intersection = new HashSet(ids);
        intersection.retainAll(all);

        ids.removeAll(intersection);
        all.removeAll(intersection);


        System.out.println(ids);

        // insert
        Set<Long> clone = new HashSet<>(ids);
        ids.add(100L);
        System.out.println(ids);
        System.out.println(clone);


        ids.removeAll(all);
        System.out.println(ids);

        // delete
        all.removeAll(clone);
        System.out.println(all);


        Map<Long, Long> a = new HashMap<Long, Long>();
        a.put(4L, 1L);
        a.put(5L, 2L);
        a.put(6L, 3L);
        a.put(7L, 4L);
        a.put(10L, 5L);
        a.put(11L, 6L);
        a.put(12L, 7L);

        ids = new HashSet<>();
        ids.add(4L);
        ids.add(10L);
        ids.add(17L);

        Set<Long> longs = new HashSet(a.keySet());
        System.out.println(longs);
        for (Long id : ids) {
            a.remove(id);
        }
        System.out.println(longs);
        System.out.println(a);

    }

    @Test
    public void testReflect() throws Exception {
        Users user = new Users();
        user.setId(1L);

        for (Method m : Users.class.getMethods()) {
            String name = m.getName();
            if (name.equals("setId")) {
                m.invoke(user, 2L);
            }

//            if (m.isAnnotationPresent(Test.class)) {
//                try {
//                    m.invoke(null);
//                } catch (InvocationTargetException wrappedExc) {
//                    Throwable exc = wrappedExc.getCause();
//                    System.out.println(m + " failed: " + exc);
//                } catch (Exception exc) {
//                    System.out.println("INVALID @Test: " + m);
//                }
//            }

            // Array ExceptionTest processing code - Page 174
//            if (m.isAnnotationPresent(ExceptionTest.class)) {
//                try {
//                    m.invoke(null);
//                    System.out.printf("Test %s failed: no exception%n", m);
//                } catch (Throwable wrappedExc) {
//                    Throwable exc = wrappedExc.getCause();
//                    Class<? extends Exception>[] excTypes =
//                            m.getAnnotation(ExceptionTest.class).value();
//                    int oldPassed = passed;
//                    for (Class<? extends Exception> excType : excTypes) {
//                        if (excType.isInstance(exc)) {
//                            break;
//                        }
//                    }
//                }
//            }
        }

        System.out.println(user.getId());

    }

    @Test
    public void testHash() {
        HashMultimap<String, String> map = HashMultimap.create();
        map.put("a", "a1");
        map.put("a", "a2");
        map.put("a", "a3");
        map.put("b", "1");

        Set<String> a = map.get("a");
        System.out.println();

        for (Map.Entry<String, Collection<String>> entry : map.asMap().entrySet()) {
            entry.getKey();
            Collection<String> value = entry.getValue();
            for (String s : value) {

            }
        }

    }

    @Test
    public void testJson() {
//        Brands b = new Brands();
//        b.setName("hello");
//        b.setId(11L);
//        String s = ObjectMapperFactory.writeValueAsString(b);
//        System.out.println(s);

    }
}
