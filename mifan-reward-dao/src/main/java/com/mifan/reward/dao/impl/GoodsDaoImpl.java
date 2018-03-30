package com.mifan.reward.dao.impl;

import com.mifan.reward.dao.GoodsDao;
import com.mifan.reward.domain.Goods;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author DXZ
 * @version 1.0
 * @since 2017-08-31
 */
@Repository
public class GoodsDaoImpl extends AbstractBaseDao<Goods> implements GoodsDao {
}
