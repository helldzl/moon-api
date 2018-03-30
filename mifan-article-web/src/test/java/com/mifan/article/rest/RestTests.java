package com.mifan.article.rest;

import com.mifan.article.AbstractTests;

//import com.mifan.sku.domain.support.Content;
//import com.mifan.sku.domain.support.DataType;
//import com.mifan.sku.service.RestService;
//import com.mifan.sku.service.impl.ImagesServiceImpl;


/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/26
 */
public class RestTests extends AbstractTests {

//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    private RestService restService;
//
//    @Autowired
//    private ImagesServiceImpl imagesService;
//
//    @Test
//    public void testImages2() throws Exception {
//        Data<Map<String, Object>> watermark = imagesService.watermark(1L);
//        System.out.println(watermark);
//    }
//
//    @Test
//    public void testImages() throws Exception {
//        Data<DataType> watermark = productsService.watermark(1L, 2L, 3L);
//        System.out.println(watermark);
//    }
//
//    @Test
//    public void testProduct() throws Exception {
//        Data<DataType> translate = productsService.translate(12L, 13L);
//        System.out.println(translate);
//    }
//
//    /**
//     * <p>翻译内容API</p>
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testSupport() throws Exception {
//        String body = "{\"data\": {\"content\":\"I am the test for google translate\"}}";
//        Data<Content> data = restService.postForEntity(Content.class, "/api/translate", body, null);
//        System.out.println(data);
//    }
//
//    /**
//     * 翻译产品:根据ID
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testSupport2() throws Exception {
//        String body = "{\"data\":{\"ids\":[\"1\",\"12\",\"34\"]}}";
//        Data<DataType> data = restService.postForEntity(DataType.class, "/api/product/translate", body, null);
//        System.out.println(data);
//    }
//
//    /**
//     * <p>翻译内容API</p>
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testRest() throws Exception {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
//
//        String body = "{\"data\": {\"content\":\"I am the test for google translate\"}}";
//        HttpEntity<String> formEntity = new HttpEntity<>(body, headers);
//        String result = restTemplate.postForObject("http://192.168.1.248/api/translate", formEntity, String.class);
//
//        System.out.println(result);
//    }
//
//    /**
//     * <p>翻译产品API</p>
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testRest2() throws Exception {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
//
//        String body = "{\"data\":{\"ids\":[\"1\",\"12\",\"34\"]}}";
//        HttpEntity<String> formEntity = new HttpEntity<>(body, headers);
//        String result = restTemplate.postForObject("http://192.168.1.248/api/product/translate", formEntity, String.class);
//
//        System.out.println(result);
//    }
//
//    public static void main(String[] args) throws IOException {
//        JsonFactory jsonFactory = new JsonFactory();
//        StringWriter writer = new StringWriter();
//        JsonGenerator generator = jsonFactory.createGenerator(writer);
//
//        // start
//        generator.writeStartObject();
//
//        // data
//        generator.writeFieldName("data");
//        generator.writeStartObject();
//
//        // ids
//        generator.writeFieldName("ids");
//        generator.writeStartArray();
//        generator.writeNumber(1L);
//        generator.writeNumber(2L);
//        generator.writeNumber(3L);
//        generator.writeEndArray();
//
//        generator.writeEndObject();
//
//        // end
//        generator.writeEndObject();
//        // close
//        generator.close();
//
//        System.out.println(writer.toString());
//    }

}
