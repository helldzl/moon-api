package com.mifan.batch.spider;

import com.mifan.batch.AbstractTests;
import com.mifan.batch.spider.attachment.AttachmentClient;
import com.mifan.batch.spider.attachment.downloader.DownloaderClient;
import com.mifan.batch.spider.domain.Attachments;
import com.mifan.batch.spider.domain.Seeds;
import com.mifan.batch.spider.service.impl.FetcherServiceImpl;
import org.junit.Test;
import org.moonframework.core.amqp.Message;
import org.moonframework.core.util.ObjectMapperFactory;
import org.moonframework.crawler.facade.Spider;
import org.moonframework.crawler.storage.WebPage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LiuKai on 2018/1/23.
 */
public class HttpServerTest extends AbstractTests {
    @Autowired
    private FetcherServiceImpl fetcherService;

    @Autowired
    private Spider spider;
    @Autowired
    private Message message;
    @Autowired
    private AttachmentClient attachmentClient;
    @Autowired
    private DownloaderClient downloaderClient;

    @Test
    public void httpServerTest() {
        try {
            ServerSocket ss = new ServerSocket(8888);
            System.out.println("在8888口监听");
            Socket socket = null;
            while (true) {
                System.out.println("开始获取了");
                socket = ss.accept();
                BufferedReader bd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String requestHeader;
                int contentLength = 0;
                while ((requestHeader = bd.readLine()) != null && !requestHeader.isEmpty()) {
                    System.out.println(requestHeader);
                    Long id = null;
                    String patternIds = "(?<=&idid=)\\d{3}";
                    Pattern p = Pattern.compile(patternIds);
                    Matcher m = p.matcher(requestHeader);
                    if (m.find()) {
                        id = Long.parseLong(m.group(0));
                    }
                    if (requestHeader.indexOf("link") != -1) {
                        Seeds seeds = new Seeds();
                        seeds.setId(id);
                        seeds.setUrl(requestHeader.replaceAll("POST \\/\\?link=| HTTP/1.1", ""));
                        seeds.setSource("公众号");
                        seeds.setConf("{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"html\",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"fields\":[{\"name\":\"title\",\"selector\":\"h2\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"postDate\",\"selector\":\"#post-date\",\"index\":0,\"type\":\"DATE\",\"dateFormat\":\"yyyy-MM-dd\",\"locale\":\"zh\",\"regex\":\"\\\\d{4}-\\\\d{1,2}-\\\\d{1,2}\",\"regexBoolean\":false,\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"author\",\"selector\":\"#post-user\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"officialAccounts\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"true\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"content\",\"selector\":\"#js_content\",\"index\":0,\"type\":\"HTML\",\"regex\":\"line-height: {0,}\\\\d{0,}.\\\\d{0,}px;|font-size: {0,}\\\\d{1,}px;\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{\"ADD\":[\"section\"]},\"tagsAtrrName\":{\"ADD\":{\"p\":[\"style\"],\"img\":[\"data-src\"],\"section\":[\"style\"],\"iframe\":[\"class\"],\"span\":[\"style\"]}},\"tagsAttrNameReplace\":{\"iframe\":{\"data-src\":\"src\"}},\"addTagsAttr\":{\"img\":{\"class\":\"mifanxinggzhimg\"},\"iframe\":{\"class\":\"mifanxingplayer\"}},\"imgAttrReplace\":\"data-src\"},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"3\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"images\",\"selector\":\"#js_content img\",\"index\":-1,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"data-src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}}],\"oneToManyLink\":false}]}\n");
                        seeds.setName("News");
                        spider.fetchUrl(page(seeds));
                    }

                    /**
                     * 获得GET参数
                     */
                    if (requestHeader.startsWith("GET")) {
                        int begin = requestHeader.indexOf("/?") + 2;
                        int end = requestHeader.indexOf("HTTP/");
                        String condition = requestHeader.substring(begin, end);
                        System.out.println("GET参数是：" + condition);
                    }
                    /**
                     * 获得POST参数
                     * 1.获取请求内容长度
                     */
                    if (requestHeader.startsWith("Content-Length")) {
                        int begin = requestHeader.indexOf("Content-Lengh:") + "Content-Length:".length() + 1;
                        String postParamterLength = requestHeader.substring(begin).trim();
                        contentLength = Integer.parseInt(postParamterLength);
                        System.out.println("POST参数长度是：" + Integer.parseInt(postParamterLength));
                    }
                }
                StringBuffer sb = new StringBuffer();
                if (contentLength > 0) {
                    for (int i = 0; i < contentLength; i++) {
                        sb.append((char) bd.read());
                    }
                    System.out.println("POST参数是：" + sb.toString());
                }
                //发送回执
                PrintWriter pw = new PrintWriter(socket.getOutputStream());

                pw.println("HTTP/1.1 200 OK");
                pw.println("Content-type:text/html");
                pw.println();
                pw.println("<h1>访问成功！</h1>");

                pw.flush();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private WebPage page(Seeds seeds) {
        try {
            URI uri = URI.create(seeds.getUrl());

            WebPage webPage = ObjectMapperFactory.readValue(seeds.getConf(), WebPage.class);
            webPage.setName(seeds.getName());
            webPage.setUri(uri);
            webPage.setHost(uri.getScheme() + "://" + uri.getHost());
            webPage.setCharset(seeds.getCharset());
            if (seeds.getCharset() == null) {
                webPage.setCharset("utf-8");
            }
            webPage.setAgencyIp(seeds.getAgencyIp());
            webPage.setAgencyIpPort(seeds.getAgencyIpPort());
            // persist filed
            Map<String, Object> data = new HashMap<>();
            data.put("seedId", seeds.getId());
            webPage.setData(data);
            return webPage;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    //只更新文章的阅读数和点赞数
    @Test
    public void testYueDuShuAndDianZanSpider() {
        Map<String, String> meta = getMeta();
        Map<String, Object> result = message.sendAndReceive(meta, null, null);

    }

    private Map<String, String> getMeta() {
        Map<String, String> meta = new HashMap<>(5);
        meta.put("enabled", "1");
        meta.put("seed_id", "125,126");
        meta.put("days", "30");
        meta.put("id", "10000");
        meta.put("className", "com.mifan.article.domain.TopicsFetch");
        return meta;
    }
}

