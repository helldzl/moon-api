package com.mifan.article.domain.support;

import org.moonframework.core.util.ObjectMapperFactory;

import java.util.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/2
 */
public interface Multilingual {

    Multilingual.Language[] DEFAULT_LANGUAGES = {Multilingual.Language.CN, Multilingual.Language.EN};

    /**
     * <p>将多语言JSON转换为List</p>
     *
     * @param json json
     * @param <T>  Map
     * @return List
     */
    @SuppressWarnings("unchecked")
    static <T> List<T> getList(String json) {
        List<T> list = null;
        if (json != null && !"".equals(json)) {
            list = ObjectMapperFactory.readValue(json, List.class);
        }

        if (list == null) {
            list = Collections.emptyList();
        }
        return list;
    }

    static <T> List<T> getList(List<Map<String, T>> list, Language... languages) {
        List<T> result = new ArrayList<>(list.size());
        for (Map<String, T> map : list) {
            T obj = getValue(map, 0, languages);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }

    static <T> T getValue(List<Map<String, T>> list, Language... languages) {
        for (Map<String, T> map : list) {
            T obj = getValue(map, 0, languages);
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }

    /**
     * <p>从给定的多语言数组中按index升序查找MAP集合中第一个不为NULL的值</p>
     *
     * @param map       多语言MAP集合
     * @param index     languages数组的下标
     * @param languages 多语言数组
     * @param <T>       T
     * @return T
     */
    static <T> T getValue(Map<String, T> map, int index, Language... languages) {
        if (index >= languages.length) {
            return null;
        }

        T obj = map.get(languages[index].getName());
        if (obj == null) {
            return getValue(map, index + 1, languages);
        } else {
            return obj;
        }
    }

    enum Language {

        DEFAULT(0, "default"),
        CN(1, "cn"),
        EN(2, "en");

        private static Map<Integer, Language> map = new HashMap<>(Language.values().length);

        static {
            for (Language language : Language.values()) {
                map.put(language.getIndex(), language);
            }
        }

        public static Language from(Integer index) {
            return map.get(index);
        }

        public static void translate(Map<String, String> dictionary, Map<String, String> content) {
            content.computeIfAbsent(CN.name, key -> getValue(dictionary, content.get(EN.name)));
            content.computeIfAbsent(EN.name, key -> getValue(dictionary, content.get(CN.name)));
        }

        public static String getValue(Map<String, String> dictionary, String key) {
            if (key == null) {
                return null;
            }

            String value = dictionary.get(key);
            if (value == null) {
                value = dictionary.get(key.toLowerCase());
            }
            return value;
        }

        private final int index;
        private final String name;

        Language(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public Map<String, String> bilingual(Map<String, String> dictionary, String key) {
            if (dictionary == null) {
                return Collections.emptyMap();
            }

            Map<String, String> result = new HashMap<>(2);
            String value = getValue(dictionary, key);
            if (value == null) {
                value = key;
            }

            result.put(this.getName(), key);
            if (this == CN) {
                result.put(Language.EN.getName(), value);
            } else {
                result.put(Language.CN.getName(), value);
            }

            return result;
        }

    }

}
