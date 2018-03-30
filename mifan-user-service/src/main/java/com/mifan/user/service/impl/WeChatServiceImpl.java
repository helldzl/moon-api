/**
 * 微信相关
 */
package com.mifan.user.service.impl;

import com.mifan.user.domain.support.Signature;
import com.mifan.user.service.AuthService;
import com.mifan.user.service.WeChatService;
import com.mifan.user.type.WeChatType;
import com.mifan.user.utils.HttpClientUtil;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author ZYW
 * 2018年1月11日 15:55:46
 */
@Service
public class WeChatServiceImpl implements WeChatService {

    protected final Log logger = LogFactory.getLog(this.getClass());

    private static final String JSAPI_TICKET_KEY = "jsapi_ticket_key";

    private static final String ALL_CHAR_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";

    @Value("${mifan.proxy.server}")
    private String proxyServer;

    @Value("${mifan.proxy.port}")
    private int proxyPort;

    @Value("${mifan.proxy.isUse}")
    private boolean isUse;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Signature signature(String url) {
        String jsapiTicket = getJsapiTicket(getBusinessAccessToken());
        String noncestr = getRandomString(10);
        long timestamp = System.currentTimeMillis() / 1000;
        String all = "jsapi_ticket="+jsapiTicket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url;
        String sign = DigestUtils.sha1Hex(all);
        return new Signature(sign,WeChatType.wechat_public.getAppid(),noncestr,timestamp);

    }
    private String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        int len = ALL_CHAR_STRING.length();
        for (int i = 0; i < length; i++) {
            sb.append(ALL_CHAR_STRING.charAt(getRandom(len-1)));
        }
        return sb.toString();
    }
    private int getRandom(int count) {
        return (int) Math.round(Math.random() * (count));
    }
    private String getJsapiTicket(String accessToken) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String jsapiTicket = ops.get(JSAPI_TICKET_KEY);
        if(StringUtils.isEmpty(jsapiTicket)) {
          //TODO 高并发时，可能会造成多个线程都会重新获取token
            String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+accessToken+"&type=jsapi";
            HttpGet get = new HttpGet(url);
            if(isUse){
                HttpHost proxy = new HttpHost(proxyServer, proxyPort);
                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(5000)//连接超时时间
                        .setConnectionRequestTimeout(5000)//从池中获取连接超时时间
                        .setSocketTimeout(10000)
                        .setProxy(proxy).build();
                get.setConfig(config);
            }
            String json = HttpClientUtil.execute(get);
            if (json == null) {
                return null;
            }
            JSONObject jo = JSONObject.fromObject(json);
            if (jo.has("errcode") && !"0".equals(jo.getString("errcode"))) {
                logger.error(jo.getString("errcode") + "," + jo.getString("errmsg"));
                return null;
            }
            jsapiTicket = jo.getString("ticket");
            int expiresIn = jo.getInt("expires_in");
            ops.set(JSAPI_TICKET_KEY, jsapiTicket, expiresIn - 1200, TimeUnit.SECONDS);
        }
        return jsapiTicket;
    }

    @Override
    public String getBusinessAccessToken() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String accessToken = ops.get(AuthService.BUSINESS_ACCESS_TOKEN_KEY);
        if(StringUtils.isEmpty(accessToken)) {
            //TODO 高并发时，可能会造成多个线程都会重新获取token
            String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+WeChatType.wechat_public.getAppid()+"&secret="+WeChatType.wechat_public.getSecret();
            HttpGet get = new HttpGet(tokenUrl);
            if(isUse){
                HttpHost proxy = new HttpHost(proxyServer, proxyPort);
                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(5000)//连接超时时间
                        .setConnectionRequestTimeout(5000)//从池中获取连接超时时间
                        .setSocketTimeout(10000)
                        .setProxy(proxy).build();
                get.setConfig(config);
            }
            String json = HttpClientUtil.execute(get);
            if (json == null) {
                return null;
            }
            JSONObject jo = JSONObject.fromObject(json);
            if (jo.has("errcode")) {
                logger.error(jo.getString("errcode") + "," + jo.getString("errmsg"));
                return null;
            }
            accessToken = jo.getString(AuthService.ACCESS_TOKEN);
            int expiresIn = jo.getInt("expires_in");
            ops.set(AuthService.BUSINESS_ACCESS_TOKEN_KEY, accessToken, expiresIn - 1200, TimeUnit.SECONDS);
        }
        return accessToken;
    }


}
