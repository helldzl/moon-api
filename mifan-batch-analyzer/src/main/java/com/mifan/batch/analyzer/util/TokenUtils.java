package com.mifan.batch.analyzer.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/11/8
 */
public final class TokenUtils {

    public static void token(Analyzer analyzer, Collection<String> collection, Reader in) throws IOException {
        token(analyzer, in, collection::add);
    }

    public static void token(Analyzer analyzer, Reader in, Consumer<String> consumer) throws IOException {
        TokenStream tokenStream = analyzer.tokenStream("text", in);
        CharTermAttribute termAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            if (termAttribute.length() > 0) {
                consumer.accept(tokenStream.getAttribute(CharTermAttribute.class).toString());
            }
        }
        tokenStream.end();
        tokenStream.close();
    }

}
