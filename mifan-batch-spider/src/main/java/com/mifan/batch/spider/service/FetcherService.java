package com.mifan.batch.spider.service;

import org.moonframework.amqp.Data;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/16
 */
public interface FetcherService {

    void fetch();
    void fetch(Data data);

}
