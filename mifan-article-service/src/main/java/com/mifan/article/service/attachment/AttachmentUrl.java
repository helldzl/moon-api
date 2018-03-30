package com.mifan.article.service.attachment;

import java.util.function.BiFunction;

/**
 * Created by quzile on 2016/8/10.
 */
public interface AttachmentUrl extends BiFunction<String, String, String> {

    /**
     * <p>根据basePath和source返回一个新的URL字符串</p>
     *
     * @param baseUrl base url
     * @param source  source
     * @return new url
     */
    @Override
    String apply(String baseUrl, String source);

}
