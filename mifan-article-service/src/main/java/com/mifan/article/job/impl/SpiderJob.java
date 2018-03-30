package com.mifan.article.job.impl;

import com.mifan.article.domain.Seeds;
import com.mifan.article.job.JobAdapter;
import org.moonframework.amqp.Data;
import org.moonframework.amqp.Resource;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.model.mybatis.service.Services;
import org.quartz.JobDataMap;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>A Spider Job</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/11/2
 */
public class SpiderJob extends JobAdapter {


    @SuppressWarnings("unchecked")
    @Override
    public void template(ApplicationContext applicationContext, JobDataMap jobDataMap) {
        String vpn_data_queue = applicationContext.getBean("environment", Environment.class).getProperty("moon.amqp.rabbit.vpn-seeds-data-queue");
        String vpn_data_exchange = applicationContext.getBean("environment", Environment.class).getProperty("moon.amqp.rabbit.vpn-seeds-exchange");
        String notvpn_data_queue = applicationContext.getBean("environment", Environment.class).getProperty("moon.amqp.rabbit.seeds-data-queue");
        String notvpn_data_exchange = applicationContext.getBean("environment", Environment.class).getProperty("moon.amqp.rabbit.seeds-exchange");
        RabbitTemplate template = applicationContext.getBean("rabbitTemplate", RabbitTemplate.class);

        // 种子数组
        Object seeds = jobDataMap.get("seeds");

        // true:VPN队列, false:普通队列
        boolean vpn = jobDataMap.getBooleanValue("vpn");

        // true:增量更新, false:全量更新
        boolean incremental = jobDataMap.getBooleanValue("incremental");
        if (seeds != null) {
            List<Integer> list = (List<Integer>) seeds;
            for (Integer id : list) {
                System.out.println(String.format("Seed ID:%s, VPN:%s", id, vpn));
                // TODO 根据种子ID查询数据, 并发送给消息队列
                Seeds seeds1 = Services.findOne(Seeds.class, id.longValue());
                Map<String, Object> meta = new HashMap<>(16);
                Data data = new Data("1", "com.mifan.api.seeds");
                Map<String, Object> attributes = new HashMap<>(16);
                attributes.put("id", seeds1.getId());
                attributes.put("url", seeds1.getUrl());
                attributes.put("source", seeds1.getSource());
                attributes.put("conf", seeds1.getConf());
                attributes.put("name", seeds1.getName());
                attributes.put("agencyIp", seeds1.getAgencyIp());
                attributes.put("agencyIpPort", seeds1.getAgencyIpPort());
                attributes.put("charset",seeds1.getCharset());
                //true:增量更新, false:全量更新
                attributes.put("incremental",incremental);
                Resource resource = new Resource(meta, data);
                data.setAttributes(attributes);
                CorrelationData correlationData = new CorrelationData();
                correlationData.setId(UUID.randomUUID().toString());
                // true:VPN队列, false:普通队列
                if (vpn) {
                    //vpn队列
                    template.convertAndSend(vpn_data_exchange, vpn_data_queue, resource, correlationData);
                } else {
                    //非vpn队列
                    template.convertAndSend(notvpn_data_exchange, notvpn_data_queue, resource, correlationData);
                }

            }
        }
    }

}
