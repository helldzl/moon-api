package com.mifan.article.service.impl;

import com.mifan.article.dao.FoldersDao;
import com.mifan.article.domain.Folders;
import com.mifan.article.service.FoldersService;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pair;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-12-12
 */
@Service
public class FoldersServiceImpl extends AbstractBaseService<Folders, FoldersDao> implements FoldersService {

    /**
     * <p>increment原子改变文件夹的统计数</p>
     *
     * @param entity entity
     * @param <S>    S
     * @return int
     */
    @Override
    public <S extends Folders> int update(S entity) {
        if (entity.getIncrement() != null) {
            int update = super.update(entity.getId(), Pair.builder().add(Folders.AMOUNT, entity.getIncrement()).build(), null);
            if (update == 0) {
                throw new IllegalArgumentException(String.format("没有找到文件夹 ID:%s", entity.getId()));
            }

            Folders one = super.findOne(entity.getId(), Fields.builder().add(Folders.AMOUNT).add(Folders.CAPACITY).build());
            if ((one.getAmount() > one.getCapacity()) || one.getAmount() < 0) {
                throw new IllegalArgumentException("超出文件夹容量限制");
            }
            return update;
        } else {
            return super.update(entity);
        }
    }

    @Override
    public int remove(Long id) {
        return super.remove(id, Restrictions.eq(Folders.CANCELLABLE, 1));
    }

}
