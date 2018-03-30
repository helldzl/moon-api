package com.mifan.article.article;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/10/24
 */
public class JsoupTest {

    @Test
    public void formatting() {
        String html = "<h2>汤姆汤姆</h2>\n" +
                "<a href='a/go'>go</a>" +
                "<ul>\n" +
                "  &nbsp;\n" +
                " <li> <span>Essential Force系列</span> </li> &nbsp;\n" +
                " <li> <span>12“x 9”</span> </li> &nbsp;\n" +
                " <li> <span>颜色：棕色淡化</span> </li> &nbsp;\n" +
                " <li> <span>高光漆桶</span> </li> &nbsp;\n" +
                " <li> <span>Chrome硬件</span> </li> &nbsp;\n" +
                " <li> <span>6层100％桦木鼓壳（7.2mm）印有大量动态声音，突出的中音和高音以及确定的低音</span> </li> &nbsp;\n" +
                " <li> <span>高光漆面</span> </li> &nbsp;\n" +
                " <li> <span>与TuneSafe接头</span> </li> &nbsp;\n" +
                " <li> <span>振动中性T.A.R.汤姆安装系统（总声共振）</span> </li> \n" +
                "</ul>";
        // Document document = Jsoup.parseBodyFragment(html);
        Document document = Jsoup.parseBodyFragment(html, "http://www.google.com/");
        System.out.println(document);
        System.out.println();
        System.out.println(document.select("body>*"));
    }

}
