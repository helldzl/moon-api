/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.service.impl.SmsServiceImpl
 * @description:TODO
 * @version:v0.0.1
 * @author:ZYW Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月13日 ZYW v0.0.1 create
 */
package com.mifan.user.service.impl;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.web.utils.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.mifan.user.service.SmsService;
import com.mifan.user.type.AccountType;
import com.mifan.user.type.AliTemplae;
import com.mifan.user.type.RegisterErr;
import com.mifan.user.type.SmsServerType;
import com.mifan.user.type.SpecType;
import com.mifan.user.utils.HttpClientUtil;
import com.mifan.user.utils.MyTobaoClient;
import com.taobao.api.ApiException;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

/**
 * @author ZYW
 *
 */
@Service
public class SmsServiceImpl implements SmsService {

    protected final Log logger = LogFactory.getLog(this.getClass());

    private static final String ERR = "err";

//    private static final String ERR_MSG = "err_message";

    private static final String KEY = "SMS:";

    private static final String COUNT = "SMSCOUNT";

    // 阿里短信参数，短信固定参数
    private static final String SMS_TYPE = "normal";

    // 阿里短信签名，可以在阿里后台配置
    private static final String SMS_SIGN_NAME = "米饭";
    
    @Value("${mifan.proxy.server}")
    private String proxyServer;
    
    @Value("${mifan.proxy.port}")
    private int proxyPort;
    
    @Value("${mifan.proxy.isUse}")
    private boolean isUse;

    private JsonMapper mapper = new JsonMapper();

    private final Header[] headers = {
            new BasicHeader("X-AVOSCloud-Application-Id", SmsServerType.lean_server.getAppSecret()),
            new BasicHeader("X-AVOSCloud-Application-Key", SmsServerType.lean_server.getAppKey()),
            new BasicHeader("Content-Type", "application/json")
    };

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Map<String, String> sendVerifyCode(String phoneNum, Integer serverType, SpecType sp) {
        Map<String, String> result = new HashMap<String, String>(1);
        if (serverType == null) {
            serverType = SmsServerType.ali_server.getIndex();
        }
        if (sp == null) {
            result.put(ERR, RegisterErr.sms_description_err.getIndex());
            return result;
        }
        phoneNum = phoneNum.trim();
        if (!matches(AccountType.REGEXP_MOBILE, phoneNum)) {
            logger.error("参数错误：phone:" + phoneNum + ",serverType:" + serverType + ",SpecType:" + sp.getDescription());
            result.put(ERR, RegisterErr.phoneNum_err.getIndex());
            return result;
        }
        String count = this.redisTemplate.opsForValue().get(getCountKey(phoneNum));
        int smsCount = 0;
        if (count != null) {
            smsCount = Integer.parseInt(count);
        }
        if (smsCount >= 1) {
            result.put(ERR, RegisterErr.sms_count_excess.getIndex());
            logger.error("短信验证码获取次数过多！");
            return result;
        }

        logger.info("phone:" + phoneNum + ",serverType:" + serverType);
        String smsCode = genVerifyCode();
        if (SmsServerType.ali_server.getIndex() == serverType) {
            if (!sendForMobileAli(phoneNum, smsCode, sp)) {
                if (!sendForMobile(phoneNum, smsCode, sp)) {
                    result.put(ERR, RegisterErr.sms_serv_fail.getIndex());
                    return result;
                }
            }
        } else {
            if (!sendForMobile(phoneNum, smsCode, sp)) {
                if (!sendForMobileAli(phoneNum, smsCode, sp)) {
                    result.put(ERR, RegisterErr.sms_serv_fail.getIndex());
                    return result;
                }
            }
        }
        this.redisTemplate.opsForValue().set(getKey(phoneNum, sp), smsCode, 600, TimeUnit.SECONDS);
        smsCount++;
        this.redisTemplate.opsForValue().set(getCountKey(phoneNum), smsCount + "", 59, TimeUnit.SECONDS);//将同一个号码获取验证码的次数保存到redis，保存时长一分钟；即一分钟内每个号码只能获取1次 验证码
        return result;
    }

    @Override
    public boolean verifySMSCode(String phoneNum, String smsCode, SpecType sp) {
        if (StringUtils.isEmpty(smsCode)) {
            return false;
        }
        ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
        String value = ops.get(getKey(phoneNum, sp));
        this.redisTemplate.delete(getKey(phoneNum, sp));//应及时删除redis中保存的短信验证码

        return smsCode.equals(value);
    }

    private boolean sendForMobileAli(String phone, String smsCode, SpecType sp) {
        //此处根据sp值控制template值
        String template;
        if (SpecType.WEB_REGISTER.getDescription().equals(sp.getDescription())) {
            template = AliTemplae.sms_register.getDescription();
        } else if (SpecType.WEB_FIND_PASSWORD.getDescription().equals(sp.getDescription())) {
            template = AliTemplae.sms_back_password.getDescription();
        } else {
            template = AliTemplae.sms_login.getDescription();
        }

        logger.info("短信接收号码" + phone + ",验证码：" + smsCode + ",SpecType:" + sp.getDescription());
        MyTobaoClient client = new MyTobaoClient(SmsServerType.ali_server.getServer(), SmsServerType.ali_server.getAppKey(), SmsServerType.ali_server.getAppSecret());
        if(isUse){
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyServer,proxyPort));
            client.setProxy(proxy);
        }
        
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        Map<String, String> map = ImmutableMap.of(
                "mycode", smsCode,
                "ttl", "10"
        );
        String body = mapper.toJson(map);
//        req.setExtend("");自定义信息，可以返回
        req.setSmsType(SMS_TYPE);
        req.setSmsFreeSignName(SMS_SIGN_NAME);
        req.setSmsParamString(body);
        req.setRecNum(phone);
        req.setSmsTemplateCode(template);
        AlibabaAliqinFcSmsNumSendResponse rsp;
        try {
            rsp = client.execute(req);
            if (rsp.getErrorCode() != null) {
                logger.error("阿里服务发送验证码返回信息：" + rsp.getBody() + ":" + rsp.getMsg());
                return false;
            }
            //TODO 处理没有错误码时body里的success信息
        } catch (ApiException e) {
            logger.error("阿里服务发送验证码短信错误：" + e.getErrCode() + ":" + e.getErrMsg());
            return false;
        }
        return true;
    }

    private boolean sendForMobile(String phone, String smsCode, SpecType sp) {
        logger.info("短信接收号码" + phone + ",验证码：" + smsCode + ",SpecType:" + sp.getDescription());
        Map<String, Object> map = ImmutableMap.of(
                "mobilePhoneNumber", phone,
                "template", sp.getDescription(),
                "myco", smsCode,
                "ttl", 10/*,
                "myname", "米饭网"*/
        );
        HttpPost post = new HttpPost(SmsServerType.lean_server.getServer());
        if(isUse){
            HttpHost proxy = new HttpHost(proxyServer, proxyPort);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(5000)//连接超时时间
                    .setConnectionRequestTimeout(5000)//从池中获取连接超时时间
                    .setSocketTimeout(10000)
                    .setProxy(proxy).build();
            post.setConfig(config);
        }
        
     // 创建参数队列
        String body = mapper.toJson(map);

        StringEntity entity = new StringEntity(body, "UTF-8");
        post.setEntity(entity);
        post.setHeaders(headers);
        String json = HttpClientUtil.execute(post);
        logger.info("response:" + json);
        return json != null;
        
        
        /*CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            Map<String, Object> map = ImmutableMap.of(
                    "mobilePhoneNumber", phone,
                    "template", sp.getDescription(),
                    "myco", smsCode,
                    "ttl", 10,
                    "myname", "米饭网"
            );
            
            RequestConfig config = null;
            if(isUse){
                HttpHost proxy = new HttpHost(proxyServer, proxyPort); 
                config = RequestConfig.custom().setProxy(proxy).build();
            }else{
                config = RequestConfig.custom().build();
            }
            
            HttpPost post = new HttpPost(SmsServerType.lean_server.getServer());
            post.setConfig(config);
            // 创建参数队列
            String body = mapper.toJson(map);

            StringEntity entity = new StringEntity(body, "UTF-8");
            post.setEntity(entity);
            post.setHeaders(headers);

            httpClient = HttpClientUtil.getHttpClient();
            // post请求
            httpResponse = httpClient.execute(post);//this.httpClient  HttpClientUtil.getHttpClient()

            StatusLine status = httpResponse.getStatusLine();
            if (!(status.getStatusCode() >= 200 && status.getStatusCode() < 300)) {
                logger.error("httpclient.statuscode错误：" + status.getStatusCode() + status.getReasonPhrase());
                logger.error("" + map.get("mobilePhoneNumber"));
                return false;
            }
            logger.info(httpResponse.getStatusLine().toString());
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                // 打印响应内容
                logger.info("response:" + EntityUtils.toString(httpEntity, "UTF-8"));
            }
            return true;
        } catch (Exception e) {
            logger.error("发送验证码短信错误：" + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                logger.error("httpclient连接池释放连接错误：" + e.getMessage());
                e.printStackTrace();
            }
        }*/
    }

    private static String genVerifyCode() {
        return "" + ThreadLocalRandom.current().nextInt(10_0000, 100_0000);
    }

    private String getKey(String phoneNum, SpecType sp) {
        return KEY + phoneNum + ":" + sp.getDescription();
    }

    private String getCountKey(String phoneNum) {
        return COUNT + phoneNum;
    }

    protected boolean matches(String regex, String input) {
        return Pattern.matches(regex, input);
    }
}
