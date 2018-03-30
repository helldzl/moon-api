package com.mifan.batch.spider.analyzer;

import org.moonframework.core.util.ObjectMapperFactory;
import org.moonframework.crawler.analysis.Analysable;
import org.moonframework.crawler.analysis.PageAnalyzer;
import org.moonframework.crawler.lucene.WordSegment;
import org.moonframework.crawler.parse.Optimizer;
import org.moonframework.intelligence.hash.SimHash;
import org.moonframework.intelligence.hash.Word;
import org.wltea.analyzer.core.Lexeme;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/8/3
 */
public class DefaultPageAnalyzer implements PageAnalyzer {

    private SimHash simHash = new SimHash();
    private int top = 12;

    @Override
    public boolean analyze(Iterable<? extends Analysable> analysable) {
        analysable.forEach(it -> {
            try {
                // get
                Map<String, Object> map = it.getContent();
                String name = (String) map.get("name");        // 可能为空
                String content = (String) map.get("content");   // 可能为空

                StringBuilder sb = new StringBuilder();
                if (name != null) {
                    sb.append(name);
                }
                if (content != null) {
                    sb.append(content);
                }

                //
                List<Lexeme> segment = WordSegment.segment(Optimizer.removeHtml(sb.toString()));
                List<Word> words = segment
                        .stream().collect(Collectors.groupingBy(lexeme -> lexeme.getLexemeText())).entrySet()
                        .stream().map(this::newInstance)
                        .sorted((w1, w2) -> Integer.compare(w2.getWeight(), w1.getWeight())).limit(top).collect(Collectors.toList());
                Collections.sort(words, (w1, w2) -> Integer.compare(w1.getBegin(), w2.getBegin()));

                // put
                map.put("featureVector", ObjectMapperFactory.writeValueAsString(words));
                map.put("fingerprint", simHash.applyAsLong(words));
            } catch (Exception e) {
            }
        });
        return true;
    }

    private Word newInstance(Map.Entry<String, List<Lexeme>> entry) {
        List<Lexeme> value = entry.getValue();
        Lexeme lexeme = value.get(0);
        int length = lexeme.getLength();
        int type = lexeme.getLexemeType();
        int freq = value.size();

        int weight;
        switch (type) {
            case 1: // TYPE_ENGLISH
                weight = 2;
                break;
            case 2: // TYPE_ARABIC
                weight = freq;
                break;
            case 3: // TYPE_LETTER
                weight = 2;
                break;
            case 4: // TYPE_CNWORD
                weight = length;
                break;
            case 8: // TYPE_OTHER_CJK
                weight = length;
                break;
            case 16: // TYPE_CNUM
                weight = length;
                break;
            case 32: // TYPE_COUNT
                weight = length;
                break;
            case 48: // TYPE_CQUAN
                weight = freq;
                break;
            case 64: // TYPE_CNCHAR
                weight = freq;
                break;
            default:
                weight = freq;
                break;
        }
        weight = (freq * 4) + (weight * 6);
        return new Word(entry.getKey(), weight, lexeme.getBegin());
    }

    // get and set method

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }
}
