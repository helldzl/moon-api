/**
 *
 */
package com.mifan.support.service.impl;

import com.mifan.support.dao.EventLogDao;
import com.mifan.support.domain.EventLog;
import com.mifan.support.domain.support.Rank;
import com.mifan.support.domain.support.Rank.RankType;
import com.mifan.support.domain.support.Topics;
import com.mifan.support.service.RankService;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ZYW
 *
 */
@Service
public class RankServiceImpl extends AbstractBaseService<EventLog,EventLogDao> implements RankService{

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    @Override
    public int remove(String key,Long topicId) {
        ZSetOperations<String, Long> ops = redisTemplate.opsForZSet();
        if(StringUtils.isEmpty(key)) {
            ops.remove(RankType.DAY.getKey(), topicId);
            ops.remove(RankType.WEEK.getKey(), topicId);
            ops.remove(RankType.MONTH.getKey(), topicId);
            ops.remove(RankType.ALL.getKey(), topicId);
        }else {
            if(RankType.getRankType(key) == null) {
                throw new IllegalStateException("参数错误");
            }
            ops.remove(key, topicId);
        }
        return 1;
    }
    @Override
    public Page<Rank> findPageWithScore(String key,int page,int size){
        if(page <= 0) {
            page = 1;
        }
        if(size <= 0) {
            size = 10;
        }
        PageRequest pageRequest = Pages.builder().page(page).size(size).build();
        ZSetOperations<String, Long> ops = redisTemplate.opsForZSet();
        long count = ops.size(key);
        List<Rank> ranks = Collections.emptyList();
        if(count > 0) {
            int start = (page-1)*size;
            int end = start + size-1;
            Set<ZSetOperations.TypedTuple<Long>> tuples = ops.reverseRangeWithScores(key, start, end);
            ranks = new ArrayList<Rank>();
            Iterator<ZSetOperations.TypedTuple<Long>> iterator = tuples.iterator();
              while (iterator.hasNext())
              {
                  ZSetOperations.TypedTuple<Long> tuple = iterator.next();
                  Rank rank = new Rank(tuple.getValue(),tuple.getScore());
                  ranks.add(rank);
              }
              /*List<Long> topicIds = ranks.stream().map(Rank::getTopicId).collect(Collectors.toList());
              Map<Long,Topics> map = Services.findAll(Topics.class, topicIds,Fields.builder().add(Topics.ID).add(Topics.TITLE).build()).stream().collect(Collectors.toMap(BaseEntity::getId, topic -> topic));
              for(Rank rank : ranks) {
                  if(map.containsKey(rank.getTopicId())) {
                      rank.setTitle(map.get(rank.getTopicId()).getTitle());
                  }else
                      rank.setTitle("");
              }*/
        }
        return new PageImpl<Rank>(ranks, pageRequest, count);
    }
    @Override
    public Page<Topics> findPage(String key, int page, int size) {
        if(page <= 0) {
            page = 1;
        }
        if(size <= 0) {
            size = 10;
        }
        Page<Long> topicIds = this.getRank(key, page, size);
//        List<Topics> topics = Services.findAll(Topics.class, topicIds.getContent(),Fields.builder().add(Topics.ID).add(Topics.TITLE).build());
        List<Topics> topics = topicIds.getContent().stream().map(tId -> new Topics(tId)).collect(Collectors.toList());
        PageRequest pageRequest = Pages.builder().page(page).size(size).build();
        return new PageImpl<Topics>(topics, pageRequest, topicIds.getTotalElements());
    }
    @Override
    public Page<Long> getRank(String key, int page, int size) {
        if(page <= 0) {
            page = 1;
        }
        if(size <= 0) {
            size = 10;
        }
        PageRequest pageRequest = Pages.builder().page(page).size(size).build();
        ZSetOperations<String, Long> ops = redisTemplate.opsForZSet();
        long count = ops.size(key);
        List<Long> topicIds = Collections.emptyList();
        if(count > 0) {
            int start = (page-1)*size;
            int end = start + size-1;
            Set<Long> set = ops.reverseRange(key, start, end);
            topicIds = new ArrayList<Long>(set);
        }
        return new PageImpl<Long>(topicIds, pageRequest, count);
    }
    @Override
    public void updateRanking(String key,Integer type) {
        RankType rankType = RankType.getRankType(key);
        if(rankType == null) {
            throw new IllegalStateException("参数错误");
        }
        //TODO 想办法解决
        /*if(type == 1) {//此时为人工更新，加锁防并发；定时更新时已在上一层添加了锁，所以此时不应加锁
            ScheduledJob job = new ScheduledJob(5l);
            job.setLastModifiedTime(new Date());
            int n = Services.update(ScheduledJob.class, job);
            if(n == 0)
                throw new IllegalArgumentException("系统繁忙，请稍后再试");
        }*/

        LocalDate today = LocalDate.now();
        String startTime;//pv统计开始时间
        int size;//pv个数
        switch(rankType) {
            case DAY:
                startTime = today.minusDays(1).toString();
                size = 100;
                break;
            case WEEK:
                startTime = today.minusWeeks(1).toString();
                size = 500;
                break;
            case MONTH:
                startTime = today.minusMonths(1).toString();
                size = 1000;
                break;
            default :
                startTime = "2016-01-01";
                size = 2000;
                break;
        }
        List<EventLog> ranking = baseDao.findPageViews(startTime, today.toString(), size);
        Set<ZSetOperations.TypedTuple<Long>> tuples = new HashSet<ZSetOperations.TypedTuple<Long>>();
        for(EventLog pv : ranking) {
            ZSetOperations.TypedTuple<Long> tuple = new DefaultTypedTuple<Long>(pv.getTopicId(),(double)pv.getPageViews());
            tuples.add(tuple);
        }
        ZSetOperations<String, Long> ops = redisTemplate.opsForZSet();
        if(tuples.size() > 0) {
            if(redisTemplate.hasKey(key)) {//由于时间无法控制，所以需要全部替换，先清空，再塞入
                ops.removeRange(key, 0, size + 50);
            }
            ops.add(key, tuples);
        }
    }
}
