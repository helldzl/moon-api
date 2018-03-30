package com.mifan.article.service.impl;

import com.mifan.article.dao.ExchangeRatesDao;
import com.mifan.article.domain.ExchangeRates;
import com.mifan.article.service.ExchangeRatesService;
import org.apache.commons.codec.digest.DigestUtils;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class ExchangeRatesServiceImpl extends AbstractBaseService<ExchangeRates, ExchangeRatesDao> implements ExchangeRatesService {

    @Cacheable(cacheManager = "ehCacheCacheManager", cacheNames = "article:exchange:rate", key = "T(org.apache.commons.codec.digest.DigestUtils).md5Hex(#criterion.toString())")
    @Override
    public List<ExchangeRates> findAll(Criterion criterion) {
        if (logger.isInfoEnabled()) {
            logger.info(DigestUtils.md5Hex(criterion.toString()));
        }
        return super.findAll(criterion);
    }

}
