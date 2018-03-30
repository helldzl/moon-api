package com.mifan.batch.analyzer.hash;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.google.common.hash.Hashing;
import com.mifan.batch.analyzer.clustering.SimHash;
import com.mifan.batch.analyzer.clustering.Similarity;
import com.mifan.batch.analyzer.util.HtmlUtils;
import com.mifan.batch.analyzer.util.TokenUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.util.WordlistLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/6/7
 */
public class SimHashTests {

    public static void a() {
        Random random = new Random(47);

        List<SimHash.Feature> list = Stream.of("a", "b", "c", "d", "e", "f").map(s -> {
            return new SimHash.Feature(s, s.hashCode(), random.nextDouble());
        }).collect(Collectors.toList());
        long hash = SimHash.hash(list);

//        412316860512
//        110000000000000000000000000000001100000
//        416611827809
//        110000100000000000000000000000001100001
        System.out.println(hash);
        System.out.println(Long.toBinaryString(hash));
        System.out.println(Long.bitCount(412316860512L ^ 416611827809L));
    }

    public static long b(String text) {
        Analyzer analyzer = null;
        try (InputStream inputStream = SimHashTests.class.getClassLoader().getResourceAsStream("analyzer/stopword.txt");
             InputStreamReader reader = new InputStreamReader(inputStream)) {
//            CharArraySet wordSet = WordlistLoader.getWordSet(reader);
//            System.out.println(wordSet);

            analyzer = new EnglishAnalyzer(WordlistLoader.getWordSet(reader));
        } catch (IOException e) {
            e.printStackTrace();
        }

        text = HtmlUtils.compress(text, true);
        Multiset<String> words = HashMultiset.create();
        try {
            TokenUtils.token(analyzer, new StringReader(text), s -> {
                words.add(s);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(words);

        List<SimHash.Feature> list = new ArrayList<>();
        for (Multiset.Entry<String> entry : words.entrySet()) {
            String element = entry.getElement();
            int count = entry.getCount();

            long hash = Hashing.murmur3_128().newHasher().putString(element, Charset.defaultCharset()).hash().asLong();
            // long hash = MurmurHash.hash64(element);
            // double weight = count;
            double weight = Math.sqrt(count);
            SimHash.Feature feature = new SimHash.Feature(element, hash, weight);
            list.add(feature);
            // System.out.println(element + " " + count + " " + weight);
        }
        // Collections.shuffle(list);
        long hash = SimHash.hash(list);
        return hash;
    }

    public static void d() {
        TreeMultiset<String> map = TreeMultiset.create();
        map.add("b-hello");
        map.add("a-hello");
        map.add("a-hello");
        map.add("a-hello");
        map.add("c-hello");
        map.add("c-hello1");
        map.add("c-hello5");
        map.add("c-hello5566");
        map.add("c-hello5566");
        map.add("c-hello5566");
        map.add("c-hello33");

        List<SimHash.Feature> list = map.entrySet()
                .stream()
                .map(entry -> new SimHash.Feature(entry.getElement(), entry.getElement().hashCode(), entry.getCount()))
                .sorted((o1, o2) -> {
                    int n = o1.compareTo(o2);
                    if (n == 0) {
                        return Integer.compare(o2.getToken().length(), o1.getToken().length());
                    }
                    return n;
                })
                .limit(12)
                .collect(Collectors.toList());

        list.stream().forEach(feature -> {
            System.out.println(feature.getToken());
        });
        System.out.println();
    }

    public static void e() {
        List<Similarity> similarities = Arrays.asList(
                new Similarity(1L, 2, 0.2D),
                new Similarity(2L, 21, 0.33D),
                new Similarity(3L, 22, 0.43D)
        );
        Collections.sort(similarities);
        System.out.println(similarities);
    }

    public static void main(String[] args) {
        e();
        // System.out.println(Math.sqrt(1D));
        // System.out.println(Long.bitCount(-7511282273509797743L ^ -7043468929372482989L));
    }

}
