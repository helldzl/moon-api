package com.mifan.article.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

/**
 * <p>支持性服务</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/4/27
 */
@Component
public final class RestService {

    @Value("${application.rest.support.host}")
    private String host;

    @Value("${application.rest.support.scheme:http}")
    private String scheme;

    @Autowired
    private RestTemplate template;

    public <T> Data<T> postForEntity(Class<T> clazz, String path, String body) {
        return postForEntity(clazz, path, body, null);
    }

    public <T> Data<T> postForEntity(Class<T> clazz, String path, String body, Consumer<HttpHeaders> consumer) {
        return exchange(clazz, path, body, HttpMethod.POST, consumer);
    }

    public Data<Map<String, Object>> postForEntity(String path, String body) {
        return postForEntity(path, body, null);
    }

    public Data<Map<String, Object>> postForEntity(String path, String body, Consumer<HttpHeaders> consumer) {
        return exchange(path, body, HttpMethod.POST, consumer);
    }

    /**
     * @param clazz    clazz
     * @param path     path
     * @param body     body
     * @param consumer consumer
     * @param method   method
     * @param <T>      T
     * @return Data
     */
    public <T> Data<T> exchange(Class<T> clazz, String path, String body, HttpMethod method, Consumer<HttpHeaders> consumer) {
        return exchange(path, body, method, BeanUtils.constructParametrizedType(Data.class, Data.class, clazz), consumer);
    }

    public Data<Map<String, Object>> exchange(String path, String body, HttpMethod method, Consumer<HttpHeaders> consumer) {
        return exchange(path, body, method, BeanUtils.constructParametrizedType(Data.class, Data.class, Map.class), consumer);
    }

    public <T> T exchange(String path, String body, HttpMethod method, JavaType javaType, Consumer<HttpHeaders> consumer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON_UTF8);
        headers.setAccept(Collections.singletonList(APPLICATION_JSON_UTF8));
        if (consumer != null) {
            consumer.accept(headers);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = template.exchange(scheme + "://" + host + path, method, entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException(String.valueOf(response.getStatusCodeValue()));
        }
        return BeanUtils.readValue(response.getBody(), javaType);
    }

    /**
     * <p>Build request string</p>
     *
     * @param ids resource ids
     * @return request string
     */
    public static String requestAsArray(Long... ids) {
        return request(generator -> {
            try {
                generator.writeFieldName("ids");
                generator.writeStartArray();
                for (Long id : ids) {
                    generator.writeNumber(id);
                }
                generator.writeEndArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * @param map map
     * @return request string
     */
    public static String requestAsMap(Map<String, String> map) {
        return request(generator ->
                map.forEach((s, o) -> {
                    try {
                        generator.writeStringField(s, o);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    /**
     * @param consumer consumer
     * @return request json string
     */
    private static String request(Consumer<JsonGenerator> consumer) {
        try {
            JsonFactory jsonFactory = new JsonFactory();
            StringWriter writer = new StringWriter();
            JsonGenerator generator = jsonFactory.createGenerator(writer);

            generator.writeStartObject();
            generator.writeFieldName("data");
            generator.writeStartObject();

            // request body
            consumer.accept(generator);

            generator.writeEndObject();
            generator.writeEndObject();
            generator.close();
            return writer.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
