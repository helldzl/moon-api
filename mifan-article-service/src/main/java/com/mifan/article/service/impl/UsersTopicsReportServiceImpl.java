package com.mifan.article.service.impl;

import com.mifan.article.dao.UsersTopicsReportDao;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.UsersTopicsReport;
import com.mifan.article.domain.Votes;
import com.mifan.article.domain.VotesOption;
import com.mifan.article.service.UsersTopicsReportService;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pair;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class UsersTopicsReportServiceImpl extends AbstractBaseService<UsersTopicsReport, UsersTopicsReportDao> implements UsersTopicsReportService {

    @Override
    public <S extends UsersTopicsReport> int saveOrUpdate(S insert, S update) {
        if (!Services.exists(Topics.class, insert.getTopicId())) {
            return 0;
        }

        String date = LocalDateTime.now().minusMinutes(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (super.count(Restrictions.and(
                Restrictions.eq(UsersTopicsReport.USER_ID, insert.getUserId()),
                Restrictions.eq(UsersTopicsReport.TOPIC_ID, insert.getTopicId()),
                Restrictions.gt(UsersTopicsReport.CREATED, date))) > 0) {
            throw new IllegalStateException("您的操作过于频繁, 请您稍后再执行该操作");
        }

        // TODO check votes [start date] and [length]

        // this is not a unique relationship, so do not check unique constraint
        Long[] options = insert.getOptions();
        if (options != null && options.length != 0) {
            options = Services.findAll(VotesOption.class, Restrictions.and(
                    Restrictions.eq(VotesOption.VOTE_ID, Votes.SYSTEM_VOTE_REPORT),
                    Restrictions.in(VotesOption.ID, options),
                    Restrictions.eq(VotesOption.ENABLED, 1)),
                    Fields.builder()
                            .add(BaseEntity.ID)
                            .build())
                    .stream()
                    .map(BaseEntity::getId)
                    .toArray(Long[]::new);
            for (Long id : options) {
                Services.update(VotesOption.class, id, Pair.builder().add(VotesOption.VOTE_OPTION_COUNT, 1).build(), null);
            }
            insert.setOptions(options);
        }
        return super.save(insert);
        // return super.saveOrUpdate(insert, update);
    }

    @Override
    public int remove(Iterable<Long> ids) {
        throw new UnsupportedOperationException();
    }

}
