package com.mifan.batch.spider.attachment;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/7/28
 */
@FunctionalInterface
public interface ColourDistance {

    double distance(int r1, int g1, int b1, int r2, int g2, int b2);

}
