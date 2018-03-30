package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsFetchDao;
import com.mifan.article.domain.Channels;
import com.mifan.article.domain.Seeds;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.TopicsFetch;
import com.mifan.article.service.SearchService;
import com.mifan.article.service.TopicsFetchService;
import org.apache.commons.lang.StringUtils;
import org.moonframework.elasticsearch.SearchResult;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class TopicsFetchServiceImpl extends AbstractBaseService<TopicsFetch, TopicsFetchDao> implements TopicsFetchService {

    @Autowired
    private SearchService searchService;

    @Override
    @Deprecated
    public SearchResult<Map<String, Object>> mpHotNews(String seedIds, int size) {
    	String[] seedIdArr = seedIds.split(",");
        Long[] seedsArr = new Long[seedIdArr.length];
        for(int i = 0;i < seedIdArr.length;i++) {
            seedsArr[i] = Long.parseLong(seedIdArr[i]);
        }
        /*PageRequest pageRequest =  Pages.builder().page(1).size(size).sort(Pages.sortBuilder().add(TopicsFetch.REVIEWS, false).build()).build();
        Page<TopicsFetch> page = super.findAll(
                Restrictions.in(TopicsFetch.SEED_ID, seedsArr),
                pageRequest,
                Fields.builder().add(TopicsFetch.TOPIC_ID).build());*/
        LocalDate today = LocalDate.now();
        String mydate = today.minusWeeks(1).toString();
        List<TopicsFetch> list = baseDao.mpHotNews(seedsArr, size, mydate);
        
        List<String> ids = list.stream().map(tf -> String.valueOf(tf.getTopicId())).collect(Collectors.toList());
        SearchResult<Map<String, Object>> result = searchService.search(Topics.class,ids,true);
        return result;
    }

    @Override
    public SearchResult<Map<String, Object>> mpRecommendNews(String seedIds, int size) {
        String[] seedIdArr = seedIds.split(",");
        if (seedIdArr.length > size) {
            throw new IllegalStateException("来源个数不能大于所取新闻个数！");
        }
        int average = size / seedIdArr.length;//平均每个来源要取的文章个数（平均数）

        Map<Integer, Map<Long, List<Long>>> reviewsMap = new HashMap<Integer, Map<Long, List<Long>>>(16);
        List<Long> remainderTopidIds = new ArrayList<Long>();//不做交叉排序的topics放在这里面，最后添加进总集合里
        for (int i = 0; i < seedIdArr.length; i++) {
            PageRequest pageRequest = Pages.builder().page(1).size(average).sort(Pages.sortBuilder().add(TopicsFetch.REVIEWS, false).build()).build();
            Page<TopicsFetch> page = super.findAll(
                    Restrictions.eq(TopicsFetch.SEED_ID, Long.parseLong(seedIdArr[i])),
                    pageRequest,
                    Fields.builder().add(TopicsFetch.TOPIC_ID).add(TopicsFetch.REVIEWS).build());
            if (page.hasContent()) {
                List<Long> topics = page.getContent().stream().map(TopicsFetch::getTopicId).collect(Collectors.toList());
                if (page.getContent().size() == average) {//如果某个来源中获取的topics数量少于平均数时，则不做交叉排序
                    int reviews = page.getContent().get(0).getReviews();
                    Map<Long, List<Long>> seedMap = new HashMap<Long, List<Long>>(16);
                    seedMap.put(Long.parseLong(seedIdArr[i]), topics);
                    while (reviewsMap.containsKey(reviews)) {
                        reviews--;
                    }
                    reviewsMap.put(reviews, seedMap);
                } else {
                    remainderTopidIds.addAll(topics);
                }
            }
        }
        List<Integer> reviews = new ArrayList<Integer>();
        for (Map.Entry<Integer, Map<Long, List<Long>>> entry : reviewsMap.entrySet()) {
            reviews.add(entry.getKey());
        }
        Collections.sort(reviews);//排序，按点击量升序

        LinkedList<Long> averageTopicIds = new LinkedList<Long>();
        for (int i = 0; i < reviews.size(); i++) {//交叉排序，先插进去点击量低的，再把点击量高的插到前边
            Integer key = reviews.get(i);
            Map<Long, List<Long>> seedMap = reviewsMap.get(key);
            List<Long> tIds = new ArrayList<List<Long>>(seedMap.values()).get(0);

            for (int j = 0; j < average; j++) {//当某个来源获取的topics数量大于平均数时，多出来的topics不做交叉排序
                int index = (i + 1) * j;//计算当前数据应该存放的位置
                averageTopicIds.add(index, tIds.get(j));
            }
        }

        averageTopicIds.addAll(remainderTopidIds);
        int num = averageTopicIds.size() < size ? size - averageTopicIds.size() : 0;
        if (num > 0) {//当获取的新闻个数少于想要获取的新闻个数时，则从资源多余的来源中补充
            Map<Long, List<Long>> hasTopicsSeed = reviewsMap.get(reviews.get(reviews.size() - 1));
            Long seedId = null;
            int thisSize = 0;
            for (Map.Entry<Long, List<Long>> entry : hasTopicsSeed.entrySet()) {
                seedId = entry.getKey();
                thisSize = entry.getValue().size();
                break;
            }
            if (thisSize >= average) {
                //TODO 这里可能会取到重复的topics，因为前边是从page=1 pagesize=average，这里的分页取其之后的数据，会不准确，待优化
                PageRequest pageRequest = Pages.builder().page(2).size(num).sort(Pages.sortBuilder().add(TopicsFetch.REVIEWS, false).build()).build();
                Page<TopicsFetch> page = super.findAll(
                        Restrictions.eq(TopicsFetch.SEED_ID, seedId),
                        pageRequest,
                        Fields.builder().add(TopicsFetch.TOPIC_ID).add(TopicsFetch.REVIEWS).build());
                if (page.hasContent()) {
                    List<Long> topics = page.getContent().stream().map(TopicsFetch::getTopicId).collect(Collectors.toList());
                    averageTopicIds.addAll(topics);
                }
            }

        }
        List<String> ids = averageTopicIds.stream().map(l -> String.valueOf(l)).collect(Collectors.toList());
        SearchResult<Map<String, Object>> result = searchService.search(Topics.class, ids, true);
        return result;
    }

    public static void main(String[] args) {
        List<Integer> one = new ArrayList<Integer>();
        one.add(5);
        one.add(4);
        one.add(3);
        one.add(8);
        LinkedList<Integer> two = new LinkedList<Integer>();
        two.add(9);
        two.add(10);
        System.out.println(StringUtils.join(two, ","));
        two.addAll(one);
        List<String> ids = two.stream().map(l -> String.valueOf(l)).collect(Collectors.toList());
        System.out.println(StringUtils.join(ids, ","));
    }

    @Override
    public TopicsFetch findOne(TopicsFetch entity) {
        return findOne(entity, null);
    }

    @Override
    public TopicsFetch findOne(TopicsFetch entity, Iterable<? extends Field> fields) {
        TopicsFetch fetch = super.findOne(entity, fields == null ? TopicsFetch.DEFAULT_FIELDS : fields);
        // TopicsFetch fetch = super.findOne(entity, TopicsFetch.DEFAULT_FIELDS);
        if (fetch == null || fetch.getSeedId() == null) {
            return fetch;
        }

        // read from ehcache first, otherwise from db. See SeedsServiceImpl
        Seeds seed = Services.findOne(Seeds.class, fetch.getSeedId());
        if (seed == null) {
            return fetch;
        }

        fetch.setHost(seed.getUrl());
        Channels channel = seed.getChannel();
        if (channel == null) {
            return fetch;
        }

        fetch.setSource(channel.getChannelName());
        fetch.setImage(channel.getChannelImage());
        fetch.setChannelId(channel.getId());
        return fetch;
    }
}
