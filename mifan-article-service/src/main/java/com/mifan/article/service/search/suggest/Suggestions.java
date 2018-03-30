package com.mifan.article.service.search.suggest;

import com.mifan.article.util.PinyinUtils;
import com.sun.javafx.UnmodifiableArrayList;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/12/1
 */
public class Suggestions {

    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    private static List<String> category = new UnmodifiableArrayList<>(new String[]{"-"}, 1);

    static {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    public static List<String> input(String title) {
        List<String> result = new ArrayList<>();
        try {
            result.add(title.toLowerCase());

            StringBuilder n = new StringBuilder();
            StringBuilder m = new StringBuilder();
            for (char c : title.toCharArray()) {
                if (PinyinUtils.isChinese(c)) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (temp != null && temp.length != 0) {
                        n.append(temp[0].charAt(0));
                        m.append(temp[0]);
                    }
                }
            }

            if (n.length() > 0) {
                result.add(n.toString());
                result.add(m.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * <p>phrase 建议器</p>
     *
     * @param builder builder
     * @param size    size
     * @param query   query
     */
    public static void phrase(SearchRequestBuilder builder, int size, String query) {
        accept(size, query, text -> {
            boolean chinese = PinyinUtils.isChinese(text);
            builder.addSuggestion(new PhraseSuggestionBuilder("phrase")
                    .size(size)
                    .analyzer(chinese ? "ik_smart" : "english")
                    .text(text)
                    .field(chinese ? "items.titles.cn" : "items.titles.en")
                    .gramSize(2)
                    .realWordErrorLikelihood(0.9F)
                    .maxErrors(0.1F)
                    .highlight("<em>", "</em>"));
        });
    }

    /**
     * <p>completion 建议器</p>
     *
     * @param builder builder
     * @param size    size
     * @param query   query
     * @param params  params
     */
    public static void completion(SearchRequestBuilder builder, int size, String query, Map<String, String[]> params) {
        accept(size, query, text -> {
            CompletionSuggestionBuilder completion = new CompletionSuggestionBuilder("completion");
            completion.size(size).text(text).field("suggest");
            addCategory(completion, params, "filter[forumId]");
            addCategory(completion, params, "filter[category]");
            addCategory(completion, params, "filter[brand]");
            builder.addSuggestion(completion);
        });
    }

    private static void addCategory(CompletionSuggestionBuilder completion, Map<String, String[]> params, String key) {
        completion.addCategory(key.replaceAll(".*\\[|\\]", ""), function(params.get(key), s -> Arrays.asList(s.split(",")), () -> category));
    }

    private static <T, R> List<R> function(T[] array, Function<T, List<R>> function, Supplier<List<R>> supplier) {
        List<R> list = new ArrayList<>();
        if (array != null && array.length != 0) {
            for (T t : array) {
                list.addAll(function.apply(t));
            }
        }

        if (list.isEmpty()) {
            return supplier.get();
        }
        return list;
    }

    private static void accept(int size, String query, Consumer<String> consumer) {
        if (size > 0 && !StringUtils.isEmpty(query) && !"".equals(query = query.trim())) {
            consumer.accept(query);
        }
    }

}
