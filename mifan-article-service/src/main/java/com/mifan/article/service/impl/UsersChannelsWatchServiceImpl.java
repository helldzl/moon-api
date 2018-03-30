package com.mifan.article.service.impl;

import com.mifan.article.dao.UsersChannelsWatchDao;
import com.mifan.article.domain.Channels;
import com.mifan.article.domain.UsersChannelsWatch;
import com.mifan.article.service.UsersChannelsWatchService;
import org.moonframework.model.mybatis.domain.Pair;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class UsersChannelsWatchServiceImpl extends AbstractBaseService<UsersChannelsWatch, UsersChannelsWatchDao> implements UsersChannelsWatchService {

    /**
     * <p>确保线程安全的更新统计数</p>
     * <p>saveOrUpdate语义:[MUST]enabled=1</p>
     * <ol>
     * <li>根据 unique key 查找ID, 为了安全, 目前框架不支持非主键条件update</li>
     * <li>save : 通过 unique key 控制</li>
     * <li>update : 通过行级锁控制</li>
     * <li></li>
     * </ol>
     *
     * @param insert insert
     * @param update update
     * @param <S>    S
     * @return int
     */
    @Override
    public <S extends UsersChannelsWatch> int saveOrUpdate(S insert, S update) {
        // 只能关注非系统频道
        Channels one = new Channels(insert.getChannelId());
        one.setCancellable(1);
        one.setEnabled(1);
        if (!Services.exists(Channels.class, one)) {
            return 0;
        }

        UsersChannelsWatch unique = new UsersChannelsWatch();
        unique.setUserId(insert.getUserId());
        unique.setChannelId(insert.getChannelId());
        return Services.saveOrUpdate(UsersChannelsWatch.class, unique, insert, update, action -> {
            if (Services.Action.INSERT == action) {
                Services.update(Channels.class, insert.getChannelId(), Pair.builder().add(Channels.WATCHED, 1).build(), null);
            }
        });
    }

    @Override
    public int remove(Iterable<Long> ids) {
        return Services.remove(UsersChannelsWatch.class, ids, UsersChannelsWatch.CHANNEL_ID, watch ->
                Services.update(Channels.class, watch.getChannelId(), Pair.builder().add(Channels.WATCHED, -1).build(), null));
    }
}
