package com.mifan.article.domain.support;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/5/26
 */
public final class HtmlUtils {

    private static final HtmlCompressor HTML_COMPRESSOR = new HtmlCompressor();

    static {
        // default replace all multiple whitespace characters with single spaces.
        // Then compress remove all inter-tag whitespace characters <code>&amp;nbsp;</code>
        HTML_COMPRESSOR.setRemoveIntertagSpaces(true);
    }

    public static String compress(String str) {
        return compress(str, false);
    }

    public static String compress(String str, boolean format) {
        if (str == null) {
            return null;
        }
        String result = HTML_COMPRESSOR.compress(Jsoup.clean(str, Whitelist.none()));
        return format ? result.replaceAll("&\\w+;", "") : result;
    }

}
