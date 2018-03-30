package com.mifan.batch.spider.service.impl;

import com.mifan.batch.spider.domain.Seeds;
import com.mifan.batch.spider.service.FetcherService;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.amqp.Data;
import org.moonframework.core.amqp.Message;
import org.moonframework.core.util.ObjectMapperFactory;
import org.moonframework.crawler.facade.Spider;
import org.moonframework.crawler.storage.WebPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/18
 */
@Service
public class FetcherServiceImpl implements FetcherService {
    protected static Log logger = LogFactory.getLog(FetcherServiceImpl.class);

    @Autowired
    private Spider spider;
    @Autowired
    private Message message;

    @Autowired
    private StringRedisTemplate template;

    @Scheduled(initialDelay = 1 * 1000, fixedRate = 6 * 3600 * 1000)
    @SuppressWarnings("unchecked")
    @Override
    public void fetch() {
        System.out.println(new Date());
//        fetchSeedId(124l);
//        //vpn
//       fetchSeedId(1l);
//        fetchSeedId(3l);
//        fetchSeedId(5l);
//        fetchSeedId(10l);
//        fetchSeedId(11l);
//        fetchSeedId(4l);
//        fetchSeedId(6l);
//        fetchSeedId(54l);
//        fetchSeedId(55l);
//        fetchSeedId(48l);
//        fetchSeedId(51l);
//        fetchSeedId(34l);
//        fetchSeedId(43l);
//        fetchSeedId(44l);
//        fetchSeedId(45l);
//        fetchSeedId(47l);
//        fetchSeedId(49l);
//        fetchSeedId(22l);
//        fetchSeedId(18l);  //soundonsound
//      fetchSeedId(24l);  //soundonsound
//        fetchSeedId(20l);
//        //not vpn
//        fetchSeedId(26l);
//        fetchSeedId(27l);
        //     fetchSeedId(48l);  叉烧网
//        fetchSeedId(56l);
//        fetchSeedId(80l);
//        fetchSeedId(51l);
//        fetchSeedId(16l);
//        fetchSeedId(21l);
//        fetchSeedId(41l);
//        fetchSeedId(32l);
//        fetchSeedId(36l);
//        fetchSeedId(37l);
//        fetchSeedId(38l);
//        fetchSeedId(39l);
//        fetchSeedId(40l);
//        fetchSeedId(50l);
//        fetchSeedId(52l);
//        fetchSeedId(54l);
//        fetchSeedId(57l);
//        fetchSeedId(58l);
//       fetchSeedId(59l);
        //      fetchSeedId(115l);  //YY

//        fetchSeedId(117l);  //章鱼
//        fetchSeedId(118l);  //虎牙
//        fetchSeedId(119l);  //熊猫直播
//       fetchSeedId(120l);  //主播八卦网
//        fetchSeedId(121l);  //键盘线网
//        fetchSeedId(122l);  //直播门户网

//        fetchSeedId(7l);  //tannoy

        //fetchSeedId(53l);// 太平宝迪
        //fetchSeedId(60l); //音乐实验室
        //fetchSeedId(20l);
        //fetchSeedId(22l);
        //fetchSeedId(93l);  //什么值得买

        // fetchSeedId(48l);
//        Seeds seeds = new Seeds();
//        seeds.setId(13l);
//        seeds.setUrl("https://www.bhphotovideo.com/c/product/298909-REG/WindTech_RG_58_RG_58_Universal_Replacement_Microphone.html");
//        seeds.setSource("B&H Photo Video");
//        seeds.setConf("{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"body\",\"name\":\"com.mifan.article.domain.TopicsProduct\",\"remove\":{\"clone\":false,\"selectors\":[\"img.upcImg\"]},\"fields\":[{\"name\":\"title\",\"selector\":\"[itemprop=name]\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"category\",\"selector\":\"#breadcrumbs>li:not([class])\",\"index\":-1,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"brand\",\"selector\":\"[itemprop=brand]\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"tag\",\"selector\":\".bh-mfr-numbers\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"description\",\"selector\":\".top-section-list\",\"index\":-1,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"content\",\"selector\":\"#Overview\",\"index\":0,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"rating\",\"selector\":\".review-stars-inner\",\"index\":0,\"type\":\"DOUBLE\",\"attr\":\"style\",\"regex\":\"[^\\\\d.]\",\"coefficient\":\"0.01\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"1\",\"stridePageMerge\":false},{\"name\":\"_json_feature\",\"selector\":\"table.specTable:nth-child(1) tr,table.specTable:nth-child(n+2) tr:nth-child(n+2)\",\"index\":-1,\"type\":\"OBJECT\",\"clone\":true,\"fields\":[{\"name\":\"_name\",\"selector\":\"[data-selenium=specTopic]\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false},{\"name\":\"_value\",\"selector\":\"[data-selenium=specDetail]\",\"index\":0,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false}],\"stridePageMerge\":false},{\"name\":\"images\",\"selector\":\"#mainImage\",\"index\":0,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"L\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"LARGE\",\"stridePageMerge\":false}],\"alternate\":{\"name\":\"images\",\"selector\":\"[data-selenium=smallImgLink]\",\"index\":0,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"L\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"data-src\",\"regex\":\"(thumbnails|smallimages)\",\"replacement\":\"images500x500\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"LARGE\",\"script\":\"function method(o,src){if(o!=''){return o}else{return src}}\",\"params\":[{\"name\":\"src\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"regex\":\"(thumbnails|smallimages)\",\"replacement\":\"images500x500\",\"clone\":true,\"stridePageMerge\":false}],\"stridePageMerge\":false}],\"stridePageMerge\":false},\"stridePageMerge\":false}],\"oneToManyLink\":false}]}\n");
//        seeds.setName("products");
//        seeds.setAgencyIp("127.0.0.1");
//        seeds.setAgencyIpPort(9999);
        //spider.fetchUrl(page(seeds));

        /*Seeds seeds = new Seeds();
        seeds.setId(117l);
        seeds.setUrl("http://www.zhangyu.tv/zymanager/news/newslistPage");
        seeds.setSource("章鱼");
        seeds.setConf("{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"body > div.news-wrap > div > div.news-con > div.news-list.active a\",\"fields\":[{\"name\":\"_link\",\"selector\":\"a\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"href\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"next\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"html\",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"fields\":[{\"name\":\"title\",\"selector\":\"body > div.content > div.header > h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"postDate\",\"selector\":\"body > div.content > div.header .date\",\"index\":0,\"type\":\"DATE\",\"dateFormat\":\"yyyy/MM/dd\",\"locale\":\"zh\",\"regex\":\"\\\\d{4}.\\\\d{0,2}.\\\\d{2}\",\"regexBoolean\":false,\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"author\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"章鱼\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"content\",\"selector\":\"body > div.content > div.body\",\"index\":0,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{\"ADD\":{\"p\":[\"style\"],\"img\":[\"style\",\"class\"]}},\"replaceImgClass\":\"acd\"},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"3\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"images\",\"selector\":\"body > div.content > div.body img\",\"index\":0,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"oneToManyLink\":false}],\"oneToManyLink\":false}]}\n");
        seeds.setName("News");
        spider.fetchUrl(page(seeds));*/


//        Seeds seeds = new Seeds();
//        seeds.setId(20l);
//        seeds.setUrl("https://www.prosoundweb.com/channels/live-sound/spatial-pursuits-the-evolution-of-large-scale-sound-system-optimization/");
//        seeds.setSource("ProsoundwebReview");
//        seeds.setConf("{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"html\",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"fields\":[{\"name\":\"title\",\"selector\":\".row .col-md-12 h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"description\",\"selector\":\".row .col-md-12 h2\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"postDate\",\"selector\":\".row .blog-author\",\"index\":0,\"type\":\"DATE\",\"dateFormat\":\"MMM d, yyyy\",\"locale\":\"en\",\"regex\":\"[\\\\W\\\\w]+(?=•)• \",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"author\",\"selector\":\".row .blog-author a\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"content\",\"selector\":\".row .post-type-post>*:not(.social-links):not(.blog-author):not(h2#highlight-color):not(.dotted-line):not(.tags):not(.vertical-spacer):not(.paginationWrapper):not(#endbar):not(.comments):not(#author):not([clear=both])\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"brand\",\"selector\":\".row .col-md-12 h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"script\":\"load('nashorn:mozilla_compat.js');importPackage(com.mifan.batch.spider.dictionary);function method(o,str1,str2){if(Dictionary.result(o)!=null){return Dictionary.result(o)}else if(Dictionary.result(str1)!=null){return Dictionary.result(str1)}else{return Dictionary.result(str2)}}\",\"params\":[{\"name\":\"str1\",\"selector\":\".row .col-md-12 h2\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"str2\",\"selector\":\".row .post-type-post>*:not(.social-links):not(.blog-author):not(h2#highlight-color):not(.dotted-line):not(.tags):not(.vertical-spacer):not(.paginationWrapper):not(#endbar):not(.comments)\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"4\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"images\",\"selector\":\".row .post-type-post>*:not(.social-links):not(.blog-author):not(h2#highlight-color):not(.dotted-line):not(.tags):not(.vertical-spacer):not(.paginationWrapper):not(#endbar):not(.comments):not(#author):not([clear=both]) img\",\"index\":-1,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"tag\",\"selector\":\".article-term [href*=prosoundweb]\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"_link\",\"selector\":\".paginationWrapper>.paginationItem+a\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"href\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"next\":[{\"primary\":false,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\".row \",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"fields\":[{\"name\":\"content\",\"selector\":\".post-type-post>*:not(.social-links):not(.blog-author):not(h2#highlight-color):not(.dotted-line):not(.tags):not(.vertical-spacer):not(.paginationWrapper):not(#endbar):not(.comments):not(#author):not([clear=both]):not([clear=left]):not(.image_wrapper)\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":true,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"_link\",\"selector\":\".paginationWrapper>.paginationItem+a\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"href\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"images\",\"selector\":\".post-type-post>*:not(.social-links):not(.blog-author):not(h2#highlight-color):not(.dotted-line):not(.tags):not(.vertical-spacer):not(.paginationWrapper):not(#endbar):not(.comments):not(#author):not([clear=both]):not([clear=left]):not(.image_wrapper) img\",\"index\":-1,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"oneToManyLink\":false}],\"oneToManyLink\":false}]}\n");
//        seeds.setName("productReview");
//        seeds.setAgencyIp("127.0.0.1");
//        seeds.setAgencyIpPort(9999);
//        spider.fetchUrl(page(seeds));


//        Seeds seeds = new Seeds();
//        seeds.setId(113l);  //新闻
//        seeds.setUrl("https://reverb.com/news/show-us-your-space-mad-dog-ranch-studios-in-aspen");
//        seeds.setSource("Reverb");
//        seeds.setConf("{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"html\",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"remove\":{\"clone\":true,\"selectors\":[\".hidden-desktop\",\".g-container.g-container--cycle.scaling-mtb-2\",\"[data-react-class='Reverb.ArticleEmbed']\",\"[data-react-class='Reverb.CategoryCalloutEmbed']\"]},\"fields\":[{\"name\":\"title\",\"selector\":\"body > div.body-wrapper > section > article > header > div > h1 > a\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"postDate\",\"selector\":\"body > div.page-main.js-page-main.new--wenzhang > div.left-block > div.detail-main > div.detail-content > div.detail-head > div > div.left-info > span.left-info-data\",\"index\":0,\"type\":\"DATE\",\"dateFormat\":\"MMM dd, yyyy\",\"locale\":\"en\",\"regex\":\"\\\\b\\\\s\\\\w+ \\\\d{1,2}, \\\\d{4}\\\\b\",\"regexBoolean\":false,\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"description\",\"selector\":\"[name='description']\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"content\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"author\",\"selector\":\"body > div.body-wrapper > section > article > aside > div > div > div:nth-child(1) > div.blog-post__byline__date > span > span > a\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"alternate\":{\"name\":\"author\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"ReverbNews\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"content\",\"selector\":\"body > div.body-wrapper > section > article > div.blog-post__frame > div\",\"index\":0,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{\"ADD\":{\"tr\":[\"style\"]}}},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"3\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"images\",\"selector\":\"body > div.body-wrapper > section > article > div.blog-post__frame > div img\",\"index\":0,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"oneToManyLink\":false}]}\n");
//        seeds.setName("News");
//        seeds.setAgencyIp("127.0.0.1");
//        seeds.setAgencyIpPort(9999);
//        spider.fetchUrl(page(seeds));

//        Seeds seeds = new Seeds();
//        seeds.setId(113l);  //产品
//        seeds.setUrl("https://reverb.com/news/the-best-selling-amps-of-2017");
//        seeds.setSource("Reverb");
//        seeds.setConf("{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":true,\"ignoreOnEmpty\":true,\"selector\":\"body > div.body-wrapper > section > article > div.blog-post__frame > div > table:nth-child(21) tr a,body > div.body-wrapper > section > article > div.blog-post__frame > div > table:nth-child(15) tr td a,.pb-2 tbody tr td a\",\"fields\":[{\"name\":\"_link\",\"selector\":\"a\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"href\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"next\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"body\",\"name\":\"com.mifan.article.domain.TopicsProduct\",\"remove\":{\"clone\":false,\"selectors\":[\"div.video,iframe\"]},\"fields\":[{\"name\":\"title\",\"selector\":\"body > div.body-wrapper > section > div:nth-child(3) > section > div.hydra-header__title > h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"brand\",\"selector\":\"body > div.body-wrapper > section > div:nth-child(3) > section > div.hydra-header__title > div.hydra-header__brand > a\",\"index\":0,\"type\":\"TEXT\",\"regex\":\"By \",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"content\",\"selector\":\"body > div.body-wrapper > section > div:nth-child(3) > section > div.hydra-header__body > div.hydra-header__body__description\",\"index\":0,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"priceUnit\",\"selector\":\"body > div.body-wrapper > section > div:nth-child(3) > section > div.hydra-header__body > div.hydra-header__feature > div.hydra-header__feature__buy > div\",\"index\":0,\"type\":\"TEXT\",\"regex\":\"(£|€|\\\\$|,|-.*)\",\"regexBoolean\":false,\"clone\":true,\"alternate\":{\"name\":\"priceUnit\",\"selector\":\".hydra-header__body__compare>li:nth-child(2)\",\"index\":0,\"type\":\"TEXT\",\"regex\":\"(£|€|\\\\$|,|-.*)\",\"regexBoolean\":false,\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"price\",\"selector\":\"[itemprop=price]\",\"index\":0,\"type\":\"DECIMAL\",\"attr\":\"content\",\"clone\":true,\"alternate\":{\"name\":\"price\",\"selector\":\"[itemprop='lowPrice']\",\"index\":0,\"type\":\"DECIMAL\",\"attr\":\"content\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"1\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"_json_feature\",\"selector\":\"div.body-wrapper > section > div:nth-child(3) > div > div > div.hydra-tabs > div > div.tab-pane.active > div > section > dl>div\",\"index\":-1,\"type\":\"OBJECT\",\"clone\":true,\"fields\":[{\"name\":\"_name\",\"selector\":\"dt\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"_value\",\"selector\":\"dd\",\"index\":0,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"images\",\"selector\":\"body > div.body-wrapper > section > div:nth-child(3) > section > div.hydra-header__gallery > div\",\"index\":0,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"[data-react-class='Reverb.Fotorama']\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"data-react-props\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"script\":\"function method(o) {var result = [];var obj = JSON.parse(o);\\nvar images = obj.images;\\n for (var i = 0; i < images.length; i++) \\n {var src = images[i].full;\\n if (src != null) \\n { \\n result.push(src);}}return result.length != 0 ? result : null;}\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"oneToManyLink\":false}],\"oneToManyLink\":false}]}\n");
//        seeds.setName("Product");
//        seeds.setAgencyIp("127.0.0.1");
//        seeds.setAgencyIpPort(9999);
//        spider.fetchUrl(page(seeds));

//        Seeds seeds = new Seeds();
//        seeds.setId(93l);
//        seeds.setUrl("https://test.smzdm.com/pingce/p/39396/");
//        seeds.setSource("Smzdm");
//        seeds.setConf("{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"html\",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"remove\":{\"clone\":true,\"selectors\":[\".textcenter,.embed-card,.leftWrap article [itemprop=description] blockquote:nth-child(1)\"]},\"fields\":[{\"name\":\"title\",\"selector\":\"article h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"category\",\"selector\":\".user_list .lFloat\",\"index\":0,\"type\":\"TEXT\",\"regex\":\"分类：\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"postDate\",\"selector\":\".recommend_tab .grey\",\"index\":0,\"type\":\"DATE\",\"dateFormat\":\"yyyy-MM-dd\",\"locale\":\"zh\",\"regex\":\"\\\\d{4}-\\\\d{1,2}-\\\\d{1,2}\",\"regexBoolean\":false,\"clone\":true,\"alternate\":{\"name\":\"postDate\",\"selector\":\".lrTime\",\"index\":0,\"type\":\"DATE\",\"dateFormat\":\"yyyy-MM-dd\",\"locale\":\"zh\",\"regex\":\"\\\\d{4}-\\\\d{1,2}-\\\\d{1,2}\",\"regexBoolean\":false,\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"author\",\"selector\":\".recommend_tab [itemprop='name']\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"alternate\":{\"name\":\"author\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"smzdm\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"content\",\"selector\":\".leftWrap .poster-wraper,.leftWrap article>*:not(h1)\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"alternate\":{\"name\":\"content\",\"selector\":\".myEvaluation>*:not(.user_list)\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{\"REMOVE\":[\"a\"]},\"tagsAtrrName\":{\"ADD\":{\"img\":[\"src\",\"class\"],\"iframe\":[\"class\"]}},\"imgClass\":\"face\",\"replaceImgClass\":\"smzdmEmoji\",\"imgAttrReplace\":\"data-original\"},\"stridePageMerge\":false,\"tagsName\":{\"REMOVE\":[\"a\"]},\"tagsAtrrName\":{\"ADD\":{\"img\":[\"src\",\"class\"],\"iframe\":[\"class\"]}},\"imgClass\":\"face\",\"replaceImgClass\":\"smzdmEmoji\",\"imgAttrReplace\":\"data-original\"},{\"name\":\"brand\",\"selector\":\"article h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"script\":\"load('nashorn:mozilla_compat.js');importPackage(com.mifan.batch.spider.dictionary);function method(o,str1,str2){if(Dictionary.result(o)!=null){return Dictionary.result(o)}else if(Dictionary.result(str1)!=null){return Dictionary.result(str1)}else{return Dictionary.result(str2)}}\",\"params\":[{\"name\":\"str1\",\"selector\":\"article h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"str2\",\"selector\":\".leftWrap .poster-wraper,.leftWrap article\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"3\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"images\",\"selector\":\".leftWrap .poster-wraper img,.leftWrap article img:not(.face),.myEvaluation>*:not(.user_list) img:not(.face)\",\"index\":-1,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"attrWhenEmpty\":\"data-original\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"oneToManyLink\":false}]}\n");
//        seeds.setName("News");
//        seeds.setAgencyIp("127.0.0.1");
//        seeds.setAgencyIpPort(9999);
//        spider.fetchUrl(page(seeds));

        //fetchSeedId(32l);  //甜水新闻
        //单一的新闻
//        Seeds seeds = new Seeds();
//        seeds.setId(32l);  //甜水新闻
//        seeds.setUrl("https://www.sweetwater.com/insync/mic-stands-and-accessories-buying-guide/");
//        seeds.setSource("sweetwaterNews");
//        seeds.setConf("{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\".content_wrap\",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"fields\":[{\"name\":\"title\",\"selector\":\".post_intro h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"author\",\"selector\":\".post_intro .author\",\"index\":0,\"type\":\"TEXT\",\"regex\":\"By \",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"postDate\",\"selector\":\".date\",\"index\":0,\"type\":\"DATE\",\"dateFormat\":\"MMM dd, yyyy\",\"locale\":\"en\",\"regex\":\"on |, (\\\\d)+:[\\\\W\\\\w]+\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"content\",\"selector\":\".post_content\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"brand\",\"selector\":\".post_intro h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"script\":\"load('nashorn:mozilla_compat.js');importPackage(com.mifan.batch.spider.dictionary);function method(o,str1,str2){if(Dictionary.result(o)!=null){return Dictionary.result(o)}else if(Dictionary.result(str1)!=null){return Dictionary.result(str1)}else{return Dictionary.result(str2)}}\",\"params\":[{\"name\":\"str1\",\"selector\":\".post_intro h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"str2\",\"selector\":\".post_content\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"3\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"images\",\"selector\":\".post_content img\",\"index\":-1,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}}],\"oneToManyLink\":false}]}\n");
//        seeds.setName("News");
//        seeds.setAgencyIp("127.0.0.1");
//        seeds.setAgencyIpPort(9999);
//        spider.fetchUrl(page(seeds));


//        Seeds seeds = new Seeds();
//        seeds.setId(22l);  //prosoundweb新闻
//        seeds.setUrl("https://www.prosoundweb.com/channels/church/bang_on_the_drum_all_day");
//        seeds.setSource("ProsoundwebNews");
//        seeds.setConf("{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"div .row>section\",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"remove\":{\"clone\":false,\"selectors\":[\"#endbar\"]},\"fields\":[{\"name\":\"title\",\"selector\":\".col-md-12 h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"description\",\"selector\":\".col-md-12 h2\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"author\",\"selector\":\".blog-author a\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"alternate\":{\"name\":\"author\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"ProSoundWeb官网\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"postDate\",\"selector\":\".blog-author\",\"index\":0,\"type\":\"DATE\",\"dateFormat\":\"MMM d, yyyy\",\"locale\":\"en\",\"regex\":\"[\\\\W\\\\w]+(?=•)• \",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"content\",\"selector\":\".storybody article>*:not(.social-links):not(#endbar):not(.vertical-spacer):not(.tags):not([class^=comment]):not(.paginationWrapper):not(#highlight-color):not(.dotted-line):not(#author):not(.blog-author)\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"tag\",\"selector\":\".tags ul>li:nth-last-child(n+2):not(*:nth-last-child(1))\",\"index\":-1,\"type\":\"TEXT\",\"regex\":\"·\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"brand\",\"selector\":\".col-md-12 h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"script\":\"load('nashorn:mozilla_compat.js');importPackage(com.mifan.batch.spider.dictionary);function method(o,str1,str2){if(Dictionary.result(o)!=null){return Dictionary.result(o)}else if(Dictionary.result(str1)!=null){return Dictionary.result(str1)}else{return Dictionary.result(str2)}}\",\"params\":[{\"name\":\"str1\",\"selector\":\".col-md-12 h2\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"str2\",\"selector\":\".blog-author a\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"3\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"images\",\"selector\":\".storybody article>*:not(.social-links):not(#endbar):not(.vertical-spacer):not(.tags):not([class^=comment]):not(.paginationWrapper):not(#highlight-color):not(.dotted-line):not(#author):not(.blog-author) img\",\"index\":-1,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"_link\",\"selector\":\".paginationWrapper>.paginationItem+a\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"href\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"next\":[{\"primary\":false,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\".row \",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"fields\":[{\"name\":\"content\",\"selector\":\".post-type-post>*:not(.social-links):not(.blog-author):not(h2#highlight-color):not(.dotted-line):not(.tags):not(.vertical-spacer):not(.paginationWrapper):not(#endbar):not(.comments):not(#author):not([clear=both]):not([clear=left]):not(.image_wrapper)\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":true,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"_link\",\"selector\":\".paginationWrapper>.paginationItem+a\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"href\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"images\",\"selector\":\".post-type-post>*:not(.social-links):not(.blog-author):not(h2#highlight-color):not(.dotted-line):not(.tags):not(.vertical-spacer):not(.paginationWrapper):not(#endbar):not(.comments):not(#author):not([clear=both]):not([clear=left]):not(.image_wrapper) img\",\"index\":-1,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"oneToManyLink\":false}],\"oneToManyLink\":false}]}\n");
//        seeds.setName("documents");
//        seeds.setAgencyIp("127.0.0.1");
//        seeds.setAgencyIpPort(9999);
//        spider.fetchUrl(page(seeds));


//        Seeds seeds = new Seeds();
//        seeds.setId(124l);
//        seeds.setUrl("http://www.jiguo.com/article/article/40447.html");
//        seeds.setSource("极果酷玩");
//        seeds.setConf("{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"html\",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"fields\":[{\"name\":\"title\",\"selector\":\".article-author-inner .d-title\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"postDate\",\"selector\":\".article-author-inner .d-name .ft-12.fr\",\"index\":0,\"type\":\"DATE\",\"dateFormat\":\"yyyy-MM-dd\",\"locale\":\"zh\",\"regex\":\"\\\\d{4}-\\\\d{1,2}-\\\\d{1,2}\",\"regexBoolean\":false,\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"tag\",\"selector\":\"[name=keywords]\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"content\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"author\",\"selector\":\".article-author-inner .d-name .ft-14.line\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"alternate\":{\"name\":\"author\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"极果酷玩\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"content\",\"selector\":\"#article-content-show\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{\"ADD\":{\"p\":[\"style\"]}}},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"4\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"images\",\"selector\":\"#article-content-show img\",\"index\":-1,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{\"REMOVE\":{\"img\":[\"data-original\"]}}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"oneToManyLink\":false}]}\n");
//        seeds.setName("Reviews");
//        seeds.setAgencyIp("127.0.0.1");
//        seeds.setAgencyIpPort(9999);
//        spider.fetchUrl(page(seeds));

//        Seeds seeds = new Seeds();
//        seeds.setId(24l);
//        seeds.setUrl("https://www.soundonsound.com/music-business/record-album-credits");
//        seeds.setSource("SoundonsoundNews");
//        seeds.setConf("{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\".l-content\",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"fields\":[{\"name\":\"title\",\"selector\":\"h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"description\",\"selector\":\".content .field--name-field-subtitle .field__item\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"postDate\",\"selector\":\".date\",\"index\":0,\"type\":\"DATE\",\"attr\":\"content\",\"dateFormat\":\"yyyy-MM-dd\",\"locale\":\"zh\",\"regex\":\"\\\\d{4}-\\\\d{1,2}-\\\\d{1,2}\",\"regexBoolean\":false,\"clone\":true,\"alternate\":{\"name\":\"postDate\",\"selector\":\"#node-4917997 > div > div > div.field.field--name-field-issue-date.field--type-date.field--label-hidden > div > div > a\",\"index\":0,\"type\":\"DATE\",\"dateFormat\":\"MMM-yyyy\",\"locale\":\"zh\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"author\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"SOS官网\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"content\",\"selector\":\".field__items>div[property^=content]\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"brand\",\"selector\":\"h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"script\":\"load('nashorn:mozilla_compat.js');importPackage(com.mifan.batch.spider.dictionary);function method(o,str1,str2){if(Dictionary.result(o)!=null){return Dictionary.result(o)}else if(Dictionary.result(str1)!=null){return Dictionary.result(str1)}else{return Dictionary.result(str2)}}\",\"params\":[{\"name\":\"str1\",\"selector\":\".content .field--name-field-subtitle .field__item\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"str2\",\"selector\":\".field__items>div[property^=content]\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"3\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}},{\"name\":\"images\",\"selector\":\".field__items>div[property^=content] img\",\"index\":0,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{}}],\"oneToManyLink\":false}]}\n");
//        seeds.setName("documents");
//        seeds.setAgencyIp("127.0.0.1");
//        seeds.setAgencyIpPort(9999);
//        spider.fetchUrl(page(seeds));

//        Seeds seeds = new Seeds();
//        seeds.setId(115l);
//        seeds.setUrl("http://www.yy.com/yue/85979");
//        seeds.setSource("YY");
//        seeds.setConf("{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"html\",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"fields\":[{\"name\":\"title\",\"selector\":\".detail-head h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"postDate\",\"selector\":\".left-info-data\",\"index\":0,\"type\":\"DATE\",\"dateFormat\":\"yyyy-MM-dd\",\"locale\":\"zh\",\"regex\":\"\\\\d{4}-\\\\d{0,2}-\\\\d{2}\",\"regexBoolean\":false,\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"reviews\",\"selector\":\"div.detail-head > div > div.right-icon > span:nth-child(2)\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"description\",\"selector\":\"[name='description']\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"content\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"author\",\"selector\":\"body > div.page-main.js-page-main.new--wenzhang > div.left-block > div.detail-main > div.detail-content > div.detail-head > div > div.left-info > span:nth-child(3) > a\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"alternate\":{\"name\":\"author\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"YY\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"content\",\"selector\":\"body > div.page-main.js-page-main.new--wenzhang > div.left-block > div.detail-main > div.detail-content > div.master > div.articleArea > div\",\"index\":-1,\"type\":\"HTML\",\"clone\":true,\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"6\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}},{\"name\":\"images\",\"selector\":\"body > div.page-main.js-page-main.new--wenzhang > div.left-block > div.detail-main > div.detail-content > div.master > div.articleArea > div img\",\"index\":0,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\",\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}}],\"stridePageMerge\":false,\"tagsName\":{},\"tagsAtrrName\":{},\"tagsAttrNameReplace\":{},\"addTagsAttr\":{}}],\"oneToManyLink\":false}]}\n");
//        seeds.setName("News");
//        spider.fetchUrl(page(seeds));

    }

    @Override
    public void fetch(Data data) {
        Map<String, Object> result = data.getAttributes();
        SeedMap(result);
    }

    private void fetchSeedId(Long seedId) {
        Map<String, String> meta = new HashMap<>(2);
        meta.put("id", seedId.toString());
        meta.put("className", "com.mifan.article.domain.Seeds");
        Map<String, Object> result = message.sendAndReceive(meta, null, null);
        SeedMap(result);
    }

    private void SeedMap(Map<String, Object> seedMap) {
        //false 为全量
        if (seedMap.get("incremental") != null && !(Boolean) (seedMap.get("incremental"))) {
            StringBuilder sb = new StringBuilder();
            try {
                sb.append(new URL(seedMap.get("url").toString()).getHost());
                sb.append(":");
                sb.append(seedMap.get("name").toString());
            } catch (MalformedURLException e) {
                logger.error("error", e);
            }
            if (template.hasKey(sb.toString())) {
                template.delete(sb.toString());
            }
            String msg = "Redis key : " + sb + "  has been deleted";
            if (logger.isInfoEnabled()) {
                logger.info(msg);
            }
        }

        Seeds seeds = new Seeds();
        seeds.setId(Long.parseLong(seedMap.get("id").toString()));
        seeds.setUrl(seedMap.get("url").toString());
        seeds.setSource(seedMap.get("source").toString());
        seeds.setConf(seedMap.get("conf").toString());
        seeds.setName(seedMap.get("name").toString());

        if (seedMap.get("agencyIp") != null && seedMap.get("agencyIpPort") != null) {
            seeds.setAgencyIp(seedMap.get("agencyIp").toString());
            seeds.setAgencyIpPort(Integer.parseInt(seedMap.get("agencyIpPort").toString()));
        }
        if (seedMap.get("charset") != null) {
            seeds.setCharset(seedMap.get("charset").toString());
        }
        spider.fetchUrl(page(seeds));
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
            Map<String, Object> data = new HashMap<>(16);
            data.put("seedId", seeds.getId());
            webPage.setData(data);
            return webPage;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

}
