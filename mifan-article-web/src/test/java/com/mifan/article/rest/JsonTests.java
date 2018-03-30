package com.mifan.article.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifan.article.domain.support.UserProfiles;
import org.moonframework.web.jsonapi.Data;

import java.io.IOException;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/28
 */
public class JsonTests {

    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        String jsonString = "{\"data\":{\"id\":11,\"gender\":2,\"birthday\":1474560000000,\"nickname\":\"e\",\"fullname\":\"请求222\",\"email\":\"aaaa\",\"userKarma\":33,\"userAvatar\":\"http://static.budee.com/iyyren/image/201610/21/1452/132943086757232640.jpg\",\"cityId\":445100,\"company\":\"请求ee\",\"profession\":\"录音师\",\"signCount\":3,\"signCountContinuos\":1,\"signTime\":\"2017-04-26 15:12:01\"}}";
        JavaType javaType = getCollectionType();
        Data<UserProfiles> result = mapper.readValue(jsonString, javaType);
        System.out.println(result);
    }

    public static JavaType getCollectionType() {
        return mapper.getTypeFactory().constructParametrizedType(Data.class, Data.class, UserProfiles.class);
    }

}
