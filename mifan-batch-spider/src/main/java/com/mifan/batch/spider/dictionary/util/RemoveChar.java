package com.mifan.batch.spider.dictionary.util;

/**
 * Created by LiuKai on 2017/5/11.
 * 移除多余符号
 */
public class RemoveChar {
    public static String removeChar(String s,char token)
    {
        return s.replace(token, ' ');
    }
}
