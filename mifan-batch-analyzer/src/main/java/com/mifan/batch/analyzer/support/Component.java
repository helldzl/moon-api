package com.mifan.batch.analyzer.support;

import org.moonframework.amqp.Resource;

import java.util.function.Consumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/16
 */
public interface Component extends Consumer<Resource> {

    Component next(Component component);

}
