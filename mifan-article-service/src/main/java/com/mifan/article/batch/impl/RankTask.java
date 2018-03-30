/**
 * 排行榜定时任务
 */
package com.mifan.article.batch.impl;

import com.mifan.article.batch.ScheduledTask;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author ZYW
 */
@Component
public class RankTask extends ScheduledTask {

//    @Autowired
//    private RankService rankService;

    //    @Scheduled(initialDelay = 10 * 1000, fixedRate = 60 * 5 * 1000)
    @Override
    public void task() {
        super.task();
    }

    public RankTask() {
        super(5L);
    }

    @Override
    protected void doTask(Date from, Date to) {
//        rankService.updateRanking(Rank.RankType.DAY.getKey(),0);
//        rankService.updateRanking(Rank.RankType.WEEK.getKey(),0);
//        rankService.updateRanking(Rank.RankType.MONTH.getKey(),0);
//        rankService.updateRanking(Rank.RankType.ALL.getKey(),0);
    }

}
