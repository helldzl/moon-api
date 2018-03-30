package com.mifan.batch.spider;

import com.mifan.batch.AbstractTests;
import org.junit.Test;
import org.moonframework.amqp.Data;
import org.moonframework.amqp.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/24
 */
public class ResourceTests extends AbstractTests {

    @Autowired
    private RabbitTemplate template;
    @Value("${moon.amqp.rabbit.vpn-seeds-data-queue}")
    private  String server_data_queue;
    @Value("${moon.amqp.rabbit.vpn-seeds-exchange}")
    private  String server_data_exchange;

    @Test
    public void testResource() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 49);
        attributes.put("url", "https://www.premierguitar.com/");
        attributes.put("source", "premierguitar评测");
        attributes.put("conf","{\"nodes\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"#primaryNav .level1 .level1-li:first-child .level2 li\",\"fields\":[{\"name\":\"category\",\"selector\":\"a:not([href*='/gear-review-inquiries'])\",\"index\":0,\"type\":\"TEXT\",\"clone\":true},{\"name\":\"_link\",\"selector\":\" a:not([href*='/gear-review-inquiries'])\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"href\",\"clone\":true}],\"next\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"html\",\"fields\":[{\"name\":\"_link\",\"selector\":\".next_page\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"href\",\"clone\":true}]},{\"primary\":true,\"transfer\":true,\"filter\":true,\"ignoreOnEmpty\":true,\"selector\":\".article-summary__headline\",\"fields\":[{\"name\":\"_link\",\"selector\":\"a\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"href\",\"clone\":true}],\"next\":[{\"primary\":true,\"transfer\":true,\"filter\":false,\"ignoreOnEmpty\":true,\"selector\":\"html\",\"name\":\"com.mifan.article.domain.TopicsDocument\",\"remove\":{\"clone\":true,\"selectors\":[\".additional\"]},\"fields\":[{\"name\":\"title\",\"selector\":\".box1 .record h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true},{\"name\":\"author\",\"selector\":\".authors a\",\"index\":0,\"type\":\"TEXT\",\"clone\":true},{\"name\":\"postDate\",\"selector\":\".date\",\"index\":0,\"type\":\"DATE\",\"dateFormat\":\"MMM dd, yyyy\",\"locale\":\"en\",\"clone\":true},{\"name\":\"description\",\"selector\":\"[name=description]\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"content\",\"clone\":true},{\"name\":\"tag\",\"selector\":\"[name='sailthru.tags']\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"content\",\"clone\":true},{\"name\":\"content\",\"selector\":\".body:not(.editorial-content__body)>*:not([style='width: 200px; float: right; background-color: rgb(234, 234, 234); padding: 5px; margin-left: 5px; border: 1px solid black;'])\",\"index\":-1,\"type\":\"HTML\",\"clone\":true},{\"name\":\"brand\",\"selector\":\".box1 .record h1\",\"index\":0,\"type\":\"TEXT\",\"clone\":true,\"script\":\"load('nashorn:mozilla_compat.js');importPackage(com.mifan.batch.spider.dictionary);function method(o,str1,str2){if(Dictionary.result(o)!=null){return Dictionary.result(o)}else if(Dictionary.result(str1)!=null){return Dictionary.result(str1)}else{return Dictionary.result(str2)}}\",\"params\":[{\"name\":\"str1\",\"selector\":\"[name=description]\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"content\",\"clone\":true},{\"name\":\"str2\",\"selector\":\".body:not(.editorial-content__body)>*:not([style='width: 200px; float: right; background-color: rgb(234, 234, 234); padding: 5px; margin-left: 5px; border: 1px solid black;'])\",\"index\":-1,\"type\":\"HTML\",\"clone\":true}]},{\"name\":\"forumId\",\"index\":0,\"type\":\"FIXED_FIELD\",\"clone\":true,\"fixedField\":\"4\"},{\"name\":\"images\",\"selector\":\".body:not(.editorial-content__body)>*:not([style='width: 200px; float: right; background-color: rgb(234, 234, 234); padding: 5px; margin-left: 5px; border: 1px solid black;']) img\",\"index\":0,\"type\":\"ELEMENT\",\"clone\":true,\"fields\":[{\"name\":\"XL\",\"selector\":\"img\",\"index\":0,\"type\":\"ATTRIBUTE\",\"attr\":\"src\",\"clone\":true,\"tag\":\"img\",\"tagAttr\":\"src\",\"model\":\"EXTRA_LARGE\"}]}]}]}]}]}");
        attributes.put("name", "reviews");
        attributes.put("agencyIp","127.0.0.1");
        attributes.put("agencyIpPort","9999");


        Map<String, Object> meta = new HashMap<>();
        meta.put("action", "update");

        Data data = new Data("222", "com.mifan.api.Products");
        data.setAttributes(attributes);
        Resource resource = new Resource(meta, data);

        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(UUID.randomUUID().toString());

        template.convertAndSend(server_data_exchange, server_data_queue, resource, correlationData);
    }
}
