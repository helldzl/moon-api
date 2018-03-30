package com.mifan.article.service.impl;

import com.mifan.article.dao.VotesDao;
import com.mifan.article.domain.Votes;
import com.mifan.article.domain.VotesOption;
import com.mifan.article.service.VotesService;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-05
 */
@Service
public class VotesServiceImpl extends AbstractBaseService<Votes, VotesDao> implements VotesService {

    @Override
    public Votes queryForObject(Long id, Iterable<? extends Field> fields) {
        Votes vote = super.queryForObject(id, Votes.DEFAULT_FIELDS);
        if (vote == null) {
            return null;
        }

        Page<VotesOption> page = Services.findAll(VotesOption.class, Restrictions.and(
                Restrictions.eq(VotesOption.VOTE_ID, vote.getId()),
                Restrictions.eq(VotesOption.ENABLED, 1)),
                Pages.builder().page(1).size(20).sort(Pages.sortBuilder().add(VotesOption.DISPLAY_ORDER, true).build()).build(),
                Fields.builder()
                        .add(VotesOption.ID)
                        .add(VotesOption.VOTE_OPTION_TEXT)
                        .add(VotesOption.VOTE_OPTION_COUNT)
                        .build(),
                false);

        vote.setOptions(page.getContent());
        return vote;
    }
}
