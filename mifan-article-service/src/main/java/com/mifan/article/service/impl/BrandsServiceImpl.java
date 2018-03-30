package com.mifan.article.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mifan.article.domain.Attachments;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.TopicsBrandMapping;
import com.mifan.article.domain.TopicsFetch;
import com.mifan.article.domain.support.Brands;
import com.mifan.article.service.BrandsService;
import com.mifan.article.service.TopicsService;
import com.mifan.article.service.util.EntityUtils;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/2
 */
@Service
public class BrandsServiceImpl implements BrandsService {

    protected final Log logger = LogFactory.getLog(BrandsServiceImpl.class);

    private volatile Map<String, Brands> cache = null;

    private static final String[] NAMES = {"AKAI","AKG","ALESIS","API Audio","Allen & Heath","Apogee","Avid","B&O Play","BLUE","Bettermaker","Burl Audio","Chandler Limited","Crown","DPA","Digigrid","EAW","EV","Empirical Labs","Fender","Genelec","Gibson","Heritage Audio","Icon","JBL","K&M","Lexicon","MAGIC-V","MOTU","Manley","Marshall","Martin Guitars","Pearl","Pioneer","PreSonus","QSC","Quested","Roland","RØDE Microphones","Sennheiser","Shadow Hills Industries","Steinberg","T-Rex","TC Electronic","Telefunken","Trinnov","Violet","Waves","YAMAHA"};
    
    @Autowired
    private TopicsService topicsService;
    
    @Override
    public Brands findBrand(String name, boolean cached) {
        if (cached) {
            cache();
            return cache.computeIfAbsent(name.toLowerCase(), key -> {
                Brands brand = new Brands();
                brand.setName(key);
                // TODO add new brand to DB here
                if (logger.isWarnEnabled()) {
                    logger.warn("Unknown brand name : " + key);
                }

                return brand;
            });
        } else {
            return findOne(name);
        }
    }

    @Override
    public Brands findBrand(String name) {
        return findBrand(name, true);
    }

    @Override
    public boolean reset(String name) {
        cache();
        Brands brand = findOne(name);
        boolean exists = brand != null;
        if (exists) {
            cache.put(name.toLowerCase(), brand);
        }
        return exists;
    }

    public static void main(String[] args) {
        List<Topics> list = new ArrayList<Topics>();
        Topics t1 = new Topics();
        t1.setTitle("张永伟");
        t1.setId(1L);
        Topics t2 = new Topics();
        t2.setTitle("赵洪阳");
        t2.setId(2L);
        Topics t3 = new Topics();
        t3.setTitle("刘凯");
        t3.setId(3L);
        Topics t4 = new Topics();
        t4.setTitle("张永伟");
        t4.setId(4L);
        list.add(t1);
        list.add(t2);
        list.add(t3);
        list.add(t4);
        Map<String,Topics> map = new HashMap<String,Topics>(list.size());
        for(Topics topic : list) {
            map.put(topic.getTitle(), topic);
        }
        Collection<Topics> topicsCollection = map.values();
        List<Topics> ll = new ArrayList<Topics>(topicsCollection);
        for(Topics topic : ll) {
            System.out.println(topic.getId());
        }
    }
    
    /**
     * 产品中心48个热门品牌
     */
    @Override
    public List<Brands> hotBrands(){
    	Page<Topics> page = Services.findAll(
                Topics.class,
                Restrictions.and(
                        Restrictions.eq(Topics.FORUM_ID, Topics.ForumType.BRAND.getIndex()),
                        Restrictions.in(Topics.TITLE, NAMES),
                        Restrictions.eq(Topics.ENABLED, 1)),
                Pages.builder().page(1).size(100).sort(Pages.sortBuilder().add(Topics.TITLE, true).build()).build(),
                null,
                false);
    	Map<String,Topics> map = new LinkedHashMap<String,Topics>();
    	for(Topics topic : page.getContent()) {//通过map去重，防止一个品牌多个topic
    		if(!map.containsKey(topic.getTitle())){
    			map.put(topic.getTitle(), topic);
    		}
    	}
    	Collection<Topics> topicsCollection = map.values();//通过map去重，防止一个品牌多个topic
    	List<Topics> topics = new ArrayList<Topics>(topicsCollection);
    	
    	List<String> names = topics.stream().map(t -> t.getTitle().toLowerCase()).collect(Collectors.toList());
    	
    	Set<Long> topicIds = page.getContent().stream().map(t -> t.getId()).collect(Collectors.toSet());
    	Map<Long, Map<Attachments.ContentType, List<Attachments>>> attMaps=  topicsService.findAttachment(topicIds);
    	List<Brands> brands = new ArrayList<Brands>();
    	for(Topics topic : topics) {
    		Brands brand = new Brands();
	        brand.setId(topic.getId());
	        brand.setName(topic.getTitle());
	        brand.setTop(true);
	        brands.add(brand);
    		if(attMaps.containsKey(topic.getId())) {
    			Map<Attachments.ContentType, List<Attachments>> attachments = attMaps.get(topic.getId());
    			List<Attachments> images = attachments.get(Attachments.ContentType.IMAGES);
    			if(!CollectionUtils.isEmpty(images)) {
    				brand.logo(images.get(0));
    			}
    		}
    	}
    	for(int i = 0;i < NAMES.length;i ++) {
    		if(!names.contains(NAMES[i].toLowerCase())) {
    			Brands brand = new Brands();
                brand.setName(NAMES[i]);
                brands.add(brand);
    		}
    	}
		return brands;
    }
    
    /**
     * <p>查找单个品牌信息</p>
     * <ol>
     * <li>从topics查找</li>
     * <li>从mapping查找</li>
     * </ol>
     *
     * @param name 品牌名称
     * @return brand
     */
    private Brands findOne(String name) {
        if (logger.isInfoEnabled()) {
            logger.info("Read [Brands] from database, name : " + name);
        }


        // read from topics first
        Page<Topics> page = Services.findAll(
                Topics.class,
                Restrictions.and(
                        Restrictions.eq(Topics.FORUM_ID, Topics.ForumType.BRAND.getIndex()),
                        Restrictions.eq(Topics.TITLE_HASH, EntityUtils.asLong(name)),
                        Restrictions.eq(Topics.TITLE, name),
                        Restrictions.eq(Topics.ENABLED, 1)),
                Pages.builder().page(1).size(1).build(),
                null,
                false);

        // read from mapping second if content is empty
        if (!page.hasContent()) {
            TopicsBrandMapping one = new TopicsBrandMapping();
            one.setSourceName(name.toLowerCase());
            one.setEnabled(1);
            one = Services.findOne(TopicsBrandMapping.class, one, TopicsBrandMapping.DEFAULT_FIELDS);
            if (one == null) {
                return null;
            } else {
                Brands brand = new Brands();
                brand.setName(one.getTargetName());
                return brand;
            }
        }

        // [topics]
        Topics topic = page.getContent().get(0);
        Long id = topic.getId();

        // [attachments]
        Attachments attachment = null;
        Map<Attachments.ContentType, List<Attachments>> attachments = topicsService.findAttachment(id);
        if (!attachments.isEmpty()) {
            List<Attachments> images = attachments.get(Attachments.ContentType.IMAGES);
            if (!CollectionUtils.isEmpty(images)) {
                attachment = images.get(0);
            }
        }

        // [posts]
        Posts post = topicsService.findPost(topic);

        // [fetch]
        TopicsFetch topicsFetch = new TopicsFetch();
        topicsFetch.setTopicId(topic.getId());
        topicsFetch = Services.findOne(TopicsFetch.class, topicsFetch);

        // result
        Brands brand = new Brands();
        brand.setId(topic.getId());
        brand.setName(topic.getTitle());
        brand.fetch(topicsFetch).logo(attachment).top(post);
        return brand;
    }

    /**
     * <p>全量缓存品牌信息</p>
     */
    private void cache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    cache = new ConcurrentHashMap<>(16);
                    if (logger.isInfoEnabled()) {
                        logger.info("Start init [Brands] cache");
                    }


                    // from topics
                    Services.doList(
                            Topics.class,
                            Restrictions.and(
                                    Restrictions.eq(Topics.FORUM_ID, Topics.ForumType.BRAND.getIndex()),
                                    Restrictions.eq(Topics.ENABLED, 1)),
                            Pages.builder().page(1).size(500).build(),
                            list -> {
                                Map<Long, Topics> topics = list.stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
                                Set<Long> topicIds = topics.keySet();

                                // topic ID -> attachments
                                Map<Long, Map<Attachments.ContentType, List<Attachments>>> attachments = topicsService.findAttachment(topicIds);
                                // topic ID -> posts
                                Map<Long, Posts> posts = topicsService.findPost(topicIds);
                                // topic ID -> fetch
                                Map<Long, TopicsFetch> fetch = Services.findAll(TopicsFetch.class, Restrictions.in(TopicsFetch.TOPIC_ID, topicIds.toArray()))
                                        .stream()
                                        .collect(Collectors.toMap(TopicsFetch::getTopicId, Function.identity()));

                                for (Topics topic : list) {
                                    Long topicId = topic.getId();

                                    Brands brand = new Brands();
                                    brand.setId(topicId);
                                    brand.setName(topic.getTitle());
                                    brand
                                            .fetch(fetch.get(topicId))
                                            .logo(attachments.get(topicId))
                                            .top(posts.get(topicId));
                                    cache.put(brand.getName().toLowerCase(), brand);
                                }
                            });

                    // from mapping
                    Services.findAll(TopicsBrandMapping.class,
                            Restrictions.eq(BaseEntity.ENABLED, 1),
                            TopicsBrandMapping.DEFAULT_FIELDS)
                            .forEach(topicsBrandMapping -> cache.computeIfAbsent(topicsBrandMapping.getSourceName(), key -> {
                                Brands brand = new Brands();
                                brand.setName(topicsBrandMapping.getTargetName());
                                return brand;
                            }));

                    if (logger.isInfoEnabled()) {
                        logger.info("End init [Brands] cache");
                    }

                }
            }
        }
    }

}
