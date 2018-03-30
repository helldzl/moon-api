package com.mifan.article.domain.support;

import java.io.Serializable;

/**
 * <p>翻译相关</p>
 * @author quzile
 * @version 1.0
 * @since 2017/5/4
 */
public class Content implements Serializable {

    private static final long serialVersionUID = -5223931745145360653L;
    private String content;
    private String translate;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }
}
