package com.mifan.article.api;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/11
 */
public class UploadTests {

    @Test
    public void testUpload() {

//        {
//            "data": {
//            "code": "20160725001",
//                    "isPublic": 1,
//                    "time": 1491887707669,
//                    "userId": 12,
//                    "key": "7f18e16daa5af80a1dba381ce671464e"
//        }
//        }

        long time = 1491888192911L;
        long userId = 12L;
        int isPublic = 1;
        String code = "20160725001";
        String key1 = "o2o&: JDm@Q中国（*@DYdm!~@#_+?><!@+~_DASM@_aq*SBG";
        String key2 = "SA)FJAqwe892-^%@(QAWE*_@!H@E_!@)~(≧▽≦)/~啦啦啦";
        String key = DigestUtils.md5Hex(code + time + key1 + isPublic + userId + code + time + key2 + isPublic + userId);
        System.out.println(key);
    }
}
