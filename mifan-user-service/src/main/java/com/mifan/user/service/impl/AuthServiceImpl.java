/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.service.impl.AuthServiceImpl
 * @description:登录业务
 * @version:v0.0.1
 * @author:ZYW Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月17日 ZYW v0.0.1 create
 */
package com.mifan.user.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.fragment.security.JwtTokenUtil;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Pair;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.security.authentication.PermissionControl;
import org.moonframework.security.authentication.UserPermissions;
import org.moonframework.security.core.AuthorizationService;
import org.moonframework.web.utils.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mifan.user.domain.UserAccounts;
import com.mifan.user.domain.UserProfiles;
import com.mifan.user.domain.Users;
import com.mifan.user.domain.WxRedPacket;
import com.mifan.user.domain.support.WeChatUser;
import com.mifan.user.service.AuthService;
import com.mifan.user.service.UsersService;
import com.mifan.user.service.WeChatService;
import com.mifan.user.type.AccountType;
import com.mifan.user.type.PwdType;
import com.mifan.user.type.RegisterErr;
import com.mifan.user.type.WeChatType;
import com.mifan.user.utils.CaptchaUtil;
import com.mifan.user.utils.HttpClientUtil;
import com.mifan.user.utils.TenpayUtil;
import com.mifan.user.utils.wxpay.HttpClientCertUtil;
import com.mifan.user.utils.wxpay.WXPayConfig;
import com.mifan.user.utils.wxpay.WXPayUtil;

import net.sf.json.JSONObject;

/**
 * @author ZYW
 *
 */
@Service
@EnableConfigurationProperties(WXPayConfig.class)
public class AuthServiceImpl implements AuthService {

    protected final Log logger = LogFactory.getLog(this.getClass());

    private final static String WX_USERNAME_SUFFIX = "@budeeweixin.com";//根据之前的设计，username必须是手机或者邮箱，现在用openid当作username，需要加上这个后缀

    private static final String OPEN_ID = "openid";

    private static final String EXPIRES_IN = "expires_in";

    private static final String HEAD_TYPE = "head_type";

    private static final String ERR = "err";
    
    private static final String ISREGISTER = "isRegister";
    
    private static final String WX_USER_NAME = "wxusername";
    
    private static final String BEFORE_SEND_EXCEPTION = "before_send_exception";
    
    private static final String AFTER_SEND_EXCEPTION = "after_send_exception";
    
    private static final String RADOM_RED_PACKET_KEY = "random_red_packet_list";
    
    @Value("${moon.security.cas.filter.appKey}")
    private String appKey;

    @Value("${moon.data.token.expiration}")
    private long expiration;

    @Value("${moon.data.token.remember-me-expiration}")
    private long rememberMeExpiration;
    
    @Value("${mifan.proxy.server}")
    private String proxyServer;
    
    @Value("${mifan.proxy.port}")
    private int proxyPort;
    
    @Value("${mifan.proxy.isUse}")
    private boolean isUse;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CaptchaUtil captchaUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private WXPayConfig wXPayConfig;
    
    @Autowired
    private WeChatService weChatService;

    @Override
    public Map<String, String> accessToken(String username, Long userId, int rememberme) {
        PermissionControl control = authorizationService.doGetAuthorizationInfo(appKey, userId);//查询权限

        // 更新数据, 登陆次数, 最后登陆时间等等
        usersService.updateOnLogin(userId);

        long expires = 7200;
        if (rememberme == 1) {
            expires = rememberMeExpiration;
        } else {
            expires = expiration;
        }
        String token = jwtTokenUtil.generateToken(username, String.valueOf(userId), rememberme,control);//生成token，并把用户信息及权限信息放入token
        Map<String, String> map = new HashMap<String, String>(3);
        map.put(ACCESS_TOKEN, token);
        map.put(EXPIRES_IN, expires + "");
        map.put(HEAD_TYPE, "bearer");
        captchaUtil.clearErrPasswordCount(username);//清除缓存中的登录错误次数

        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        UserPermissions userPermissions;
        String jsonOld = ops.get(getUserKey(username));
        List<String> tokens;
        if (jsonOld != null) {
            userPermissions = BeanUtils.readValue(jsonOld, UserPermissions.class);
            tokens = userPermissions.getTokens();
            long objectExpires = redisTemplate.getExpire(getUserKey(username), TimeUnit.SECONDS);
            if (objectExpires > expires) {//判断redis中tokens的剩余过期时间，如果剩余过期时间大于刚生成的token的过期时间，说明tokens中有token的过期时间大于刚生成的token的过期时间，
                expires = objectExpires;//因此此时tokens在redis中的过期时间不能被覆盖，应该取相对大的过期时间设置为redis中tokens的过期时间
            }
        } else {
            userPermissions = new UserPermissions();
            tokens = new ArrayList<String>();
        }
        tokens.add(token);
        userPermissions.setTokens(tokens);
        userPermissions.setUserId(userId);
        userPermissions.setUsername(username);
        if(control != null) {
            userPermissions.setRoles(control.getRoles());
            userPermissions.setPermissions(control.getAuthorities());
        }
        String json = BeanUtils.writeValueAsString(userPermissions);
        ops.set(getUserKey(username), json, expires, TimeUnit.SECONDS);//将权限信息和用户信息存入redis

        return map;
    }

    @Override
    public Map<String, String> refreshToken(String oldToken) {
        Map<String, String> map = new HashMap<String, String>(3);
        String username = jwtTokenUtil.getUsernameFromToken(oldToken);

        String json = redisTemplate.opsForValue().get(getUserKey(username));
        if (json != null) {
            UserPermissions userPermissions = BeanUtils.readValue(json, UserPermissions.class);
//                String ip = NetWorkUtils.getIpAddress(request);   || !ip.equals(userPermissions.getIp())  此处取消ip校验，为了支持多点登录
            if (userPermissions.getTokens().contains(oldToken)) {
                String refreshToken = jwtTokenUtil.refreshToken(oldToken);
                map.put(ACCESS_TOKEN, refreshToken);
                map.put(EXPIRES_IN, expiration + "");
                map.put(HEAD_TYPE, "bearer");
                List<String> tokens = userPermissions.getTokens();
                tokens.add(refreshToken);
                tokens.remove(oldToken);
                userPermissions.setTokens(tokens);
                userPermissions.setOldToken(oldToken);
                json = BeanUtils.writeValueAsString(userPermissions);

                long objectExpires = redisTemplate.getExpire(getUserKey(username), TimeUnit.SECONDS);
                if (objectExpires > expiration) {//判断redis中tokens的剩余过期时间，如果剩余过期时间大于刚生成的token的过期时间，说明tokens中有token的过期时间大于刚生成的token的过期时间，
                    expiration = objectExpires;//因此此时tokens在redis中的过期时间不能被覆盖，应该取相对大的过期时间设置为redis中tokens的过期时间
                }

                redisTemplate.opsForValue().set(getUserKey(username), json, expiration, TimeUnit.SECONDS);//将权限信息和用户信息存入redis
                return map;
            } else {
                throw new IllegalStateException(RegisterErr.token_notfount.getIndex());
            }
        } else {
            throw new IllegalStateException(RegisterErr.token_notfount.getIndex());
        }
    }

    private String getUserKey(String username) {
        return UserPermissions.OBJECT_KEY + username;
    }

    private WxRedPacket newWxRedPacket(Long userId) {
        String nonceStr = TenpayUtil.getNonceStr();
        String mchBillno = wXPayConfig.getMchID() + TenpayUtil.getMchBillnoEnding();//商户订单号
        double totalAmount = 1d;
        try {
            ListOperations<String, String> ops = redisTemplate.opsForList();
            String value = ops.leftPop(RADOM_RED_PACKET_KEY);
            List<String> randoms;
            if(StringUtils.isEmpty(value)) {
                randoms = TenpayUtil.randomPackage().stream().map(String::valueOf).collect(Collectors.toList());
                value = randoms.remove(0);
                ops.leftPushAll(RADOM_RED_PACKET_KEY, randoms);
            }
            totalAmount = Double.valueOf(value);
        }catch(Exception e) {
            totalAmount = 1d;
            logger.error("获取金额错误" + e.getMessage());
            e.printStackTrace();
        }
        
        WxRedPacket packet = new WxRedPacket(totalAmount,userId,mchBillno,nonceStr);
        return packet;
    }
    
    @Override
    public Map<String,String> redPacket(String code, String phone){
        Map<String, String> map = new HashMap<String,String>(1);
        if(!Pattern.matches(AccountType.REGEXP_MOBILE, phone)) {
            map.put(ERR, RegisterErr.wx_redpacket_conformity.getIndex());
            return map;
        }
        Users phoneUserInfo = usersService.findUserByUsername(phone);
        if(phoneUserInfo == null) {
            map.put(ERR, RegisterErr.user_not_exists.getIndex());
            return map;
        }

        Services.update(Users.class, phoneUserInfo.getId(), Pair.builder().add(Users.LOGIN_TIMES, 1).build(), null);//全局锁

        WxRedPacket packet = new WxRedPacket();
        packet.setUserId(phoneUserInfo.getId());
        packet = Services.findOne(WxRedPacket.class, packet);
        if(packet != null) {
            if(packet.getIsSuccess() != 0 && packet.getEnabled() == 0) {
                map.put(ERR, RegisterErr.wx_redpacket_conformity.getIndex());
                return map;
            }
        }else {
            Date created = new Date(phoneUserInfo.getCreated().getTime() + 24 * 3600 * 1000);
            if(created.before(new Date())) {
                map.put(ERR, RegisterErr.wx_redpacket_conformity.getIndex());
                return map;
            }else {
                packet = newWxRedPacket(phoneUserInfo.getId());
                try {
                    Services.save(WxRedPacket.class, packet);
                }catch(Exception e){
                    map.put(ERR, RegisterErr.wx_redpacket_conformity.getIndex());
                    logger.error("领红包：初始化订单失败");
                    return map;
                }
            }
        }

        map = getWeChatUserInfo(code,WeChatType.wechat_public.getState());
        if(map.containsKey(ERR)) {
            logger.error("领红包：" + map.get(ERR));
            return map;
        }
        String username = map.get(WX_USER_NAME);
        String openId = map.get(UserAccounts.OPENID);//此处应该获取公众号的openid,当微信不是刚注册时，从数据库查出来的 有可能是网页app的openid
        Users wxUserInfo = usersService.findUserInfo(username);
        if(wxUserInfo == null) {
            map.put(ERR, RegisterErr.user_not_exists.getIndex());
            logger.error("领红包：微信用户不存在");
            return map;
        }
        packet.setReOpenid(openId);
        packet.setWxUserId(wxUserInfo.getUserAccount().getUserId());//同一个微信领取多次红包之后，这里的userId应该还是该微信的userId，而不是其绑定帐号的userId
        if(map.containsKey(ISREGISTER)) {//说明微信是刚注册的，可以绑定
            try {
                usersService.userBind(phone, username);
            }catch(Exception e) {
                logger.error("领红包：帐号绑定失败");
                e.printStackTrace();
            }
        }
        map = this.red(packet.getNonceStr(), packet.getMchBillno(), openId, new Double(packet.getTotalAmount()*100).intValue());
        
        if(!map.containsKey(ERR)) {
            packet.setIsSuccess(1);
            packet.setSendListid(map.get(WxRedPacket.SEND_LISTID));
        }else {
            map.put(ERR, RegisterErr.wx_redpacket_err.getIndex());//替换错误信息
        }
        if(map.containsKey(WxRedPacket.ERR_CODE)) {
            packet.setErrCode(map.get(WxRedPacket.ERR_CODE));
        }
        if(map.containsKey(WxRedPacket.ERR_CODE_DES)) {
            packet.setErrCodeDes(map.get(WxRedPacket.ERR_CODE_DES));
        }
        
        Services.update(WxRedPacket.class, packet);
        map.put(OPEN_ID, openId);//传给
        return map;
    }
    
    private Map<String,String> red(String nonceStr,String mchBillno,String reOpenid,int totalAmount) {
        Map<String, String> errMap = new HashMap<String,String>(2);
        Map<String,String> params = wXPayConfig.getParams();
        params.put(WxRedPacket.MCH_BILLNO, mchBillno);
        params.put(WxRedPacket.NONCE_STR,nonceStr);
        params.put(WxRedPacket.RE_OPENID, reOpenid);
        params.put(WxRedPacket.TOTAL_AMOUNT, String.valueOf(totalAmount));
        params.put("wxappid", WeChatType.wechat_public.getAppid());
        String data;
        try {
            data = WXPayUtil.generateSignedXml(params, wXPayConfig.getKey());
        } catch (Exception e) {
            errMap.put(ERR, BEFORE_SEND_EXCEPTION);
            logger.error("领红包：mapToXml_exception");
            e.printStackTrace();
            return errMap;
        }
        try {
            String result = this.executePost(wXPayConfig.getNotifyUrl(),data);
            if(result != null) {
                Map<String,String> map = WXPayUtil.xmlToMap(result);
                if(map.containsKey(WxRedPacket.SEND_LISTID)) {
                    errMap.put(WxRedPacket.SEND_LISTID, map.get(WxRedPacket.SEND_LISTID));
                }else {
                    errMap.put(ERR, AFTER_SEND_EXCEPTION);
                    if(map.containsKey("return_msg")) {
                        errMap.put(WxRedPacket.ERR_CODE_DES, map.get("return_msg"));
                    }
                }
                if(map.containsKey(WxRedPacket.ERR_CODE)) {
                    errMap.put(WxRedPacket.ERR_CODE, map.get(WxRedPacket.ERR_CODE));
                }
                if(map.containsKey(WxRedPacket.ERR_CODE_DES)) {
                    errMap.put(WxRedPacket.ERR_CODE_DES, map.get(WxRedPacket.ERR_CODE_DES));
                }
            }else {
                errMap.put(ERR, BEFORE_SEND_EXCEPTION);
            }

        }catch (Exception e) {
            errMap.put(ERR, AFTER_SEND_EXCEPTION);
            logger.error("领红包：xmlToMap_exception");
            e.printStackTrace();
        }
        return errMap;
    }
    /*@Scheduled(cron = "0 0 23 * * ?")*/
    public void timingRedPacket() {
        Services.update(Users.class, 12L, Pair.builder().add(Users.LOGIN_TIMES, 1).build(), null);//全局锁,防止五个任务同时开启

        Criterion criterion = Restrictions.and(Restrictions.eq(WxRedPacket.IS_SUCCESS, 0),
                                    Restrictions.ne(WxRedPacket.WX_USER_ID, 0),
                                    Restrictions.eq(WxRedPacket.ENABLED, 1));
        List<WxRedPacket> packets = Services.findAll(WxRedPacket.class, criterion);
        packets = packets.stream().filter(p -> p.getReOpenid() != null).collect(Collectors.toList());
        for(WxRedPacket packet : packets) {
            Map<String,String> map = this.red(packet.getNonceStr(), packet.getMchBillno(), packet.getReOpenid(),new Double(packet.getTotalAmount()*100).intValue());
            if(!map.containsKey(ERR)) {
                packet.setIsSuccess(1);
                packet.setSendListid(map.get(WxRedPacket.SEND_LISTID));
            }
            if(map.containsKey(WxRedPacket.ERR_CODE)) {
                packet.setErrCode(map.get(WxRedPacket.ERR_CODE));
            }
            if(map.containsKey(WxRedPacket.ERR_CODE_DES)) {
                packet.setErrCodeDes(map.get(WxRedPacket.ERR_CODE_DES));
            }
            
            Services.update(WxRedPacket.class, packet);
        }

    }
    
    @Override
    public Map<String, String> weChat(String code, String state) {
        Map<String, String> map = getWeChatUserInfo(code,state);
        if(map.containsKey(ERR)) {
            return map;
        }
        String username = map.get(WX_USER_NAME);
        Users userInfo = usersService.findUserByUsername(username);
        if (userInfo == null) {
            map.put(ERR, RegisterErr.wechat_login_err.getIndex());
            return map;
        }
        map = this.accessToken(username, userInfo.getId(), 0);
        return map;
    }
    @Override
    public Map<String, String> isFollow(String openid) {
        Map<String, String> map = new HashMap<String,String>(1);
        String accessToken = weChatService.getBusinessAccessToken();

        String infoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+accessToken+"&openid="+openid+"&lang=zh_CN";
        String userJson = execute(infoUrl);
        if (userJson == null) {
            map.put(ERR, RegisterErr.wechat_login_err.getIndex());
            return map;
        }
        JSONObject infoJo = JSONObject.fromObject(userJson);
        if (infoJo.has("errcode")) {
            logger.error(infoJo.getString("errcode") + "," + infoJo.getString("errmsg"));
            map.put(ERR, RegisterErr.wechat_login_err.getIndex());
            return map;
        }
        int subscribe = infoJo.getInt("subscribe");
        if(subscribe == 0) {
            map.put(ERR, RegisterErr.wx_not_follow.getIndex());
        }
        return map;
    }
    @Override
    public Map<String, String> isFollow(String code, String state) {
        Map<String, String> map = this.getOpenid(code, WeChatType.wechat_public.getState());
        if(map.containsKey(ERR)) {
            return map;
        }
        String openid = map.get(OPEN_ID);
        map = isFollow(openid);
        return map;
    }
    private Map<String,String> getOpenid(String code, String state){
        WeChatType wechat = WeChatType.getTypeByState(state);
        Map<String, String> map = new HashMap<String, String>(1);
        if (wechat == null) {
            logger.error("异常请求，state不符合。");
            map.put(ERR, RegisterErr.wechat_state_err.getIndex());
            return map;
        }
        if(code == null) {
            logger.error("用户未授权，code不符合。");
            map.put(ERR, RegisterErr.wechat_code_err.getIndex());
            return map;
        }
        String appid = wechat.getAppid();
        String secret = wechat.getSecret();
        String tokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";

        String json = execute(tokenUrl);
        if (json == null) {
            map.put(ERR, RegisterErr.wechat_login_err.getIndex());
            return map;
        }
        JSONObject jo = JSONObject.fromObject(json);
        if (jo.has("errcode")) {
            logger.error("获取openid错误：" + jo.getString("errcode") + "," + jo.getString("errmsg"));
            map.put(ERR, RegisterErr.wechat_login_err.getIndex());
            return map;
        }
        String openid = jo.getString(OPEN_ID);
        String accessToken = jo.getString(ACCESS_TOKEN);
        map.put(OPEN_ID, openid);
        map.put(ACCESS_TOKEN, accessToken);
        return map;
    }
    
    private Map<String,String> getWeChatUserInfo(String code, String state) {//获取微信用户信息
        Map<String, String> map = this.getOpenid(code, state);
        if(map.containsKey(ERR)) {
            return map;
        }
        String accessToken = map.get(ACCESS_TOKEN);
        String openid = map.get(OPEN_ID);
        String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid;

        String userJson = execute(infoUrl);
        if (userJson == null) {
            map.put(ERR, RegisterErr.wechat_login_err.getIndex());
            return map;
        }
        JSONObject infoJo = JSONObject.fromObject(userJson);
        if (infoJo.has("errcode")) {
            logger.error("获取微信用户信息错误："+infoJo.getString("errcode") + "," + infoJo.getString("errmsg"));
            map.put(ERR, RegisterErr.wechat_login_err.getIndex());
            return map;
        }
        JsonMapper mapper = new JsonMapper();
        WeChatUser weUser = mapper.fromJson(userJson, WeChatUser.class);
        if (StringUtils.isEmpty(weUser.getUnionid()) || StringUtils.isEmpty(weUser.getNickname()) || StringUtils.isEmpty(weUser.getOpenid())) {
            logger.error("微信登录在获取用户信息时丢失了关键信息");
            map.put(ERR, RegisterErr.wechat_login_err.getIndex());
            return map;
        }
        String username = weUser.getUnionid() + WX_USERNAME_SUFFIX;
        if (!usersService.exists(username)) {
            Users user = new Users();
            user.setUsername(username);
            user.setPassword("budee123");
            user.setPwdType(PwdType.no_pwd.getIndex());
            UserProfiles userProfile = new UserProfiles();
            userProfile.setNickname(weUser.getNickname());
            userProfile.setGender(weUser.getSex());
            userProfile.setCity(weUser.getCity());
            userProfile.setProvince(weUser.getProvince());
            userProfile.setCountry(weUser.getCountry());
            userProfile.setUserAvatar(weUser.getHeadimgurl());
            UserAccounts account = new UserAccounts();
            account.setOpenid(weUser.getOpenid());

            user.setUserProfile(userProfile);
            user.setUserAccount(account);
            try {
                usersService.save(user);
            }catch(Exception e) {
                map.put(ERR, RegisterErr.wechat_login_err.getIndex());
                logger.error("保存微信用户错误");
                e.printStackTrace();
                return map;
            }
            map.put(ISREGISTER, "true");//基于此判断是否是刚注册
        }
        map.put(WX_USER_NAME, username);
        map.put(UserAccounts.OPENID, weUser.getOpenid());
        return map;
    }

    private String executePost(String url,String data) {
        HttpPost post = new HttpPost(url);
        if(isUse){
            HttpHost proxy = new HttpHost(proxyServer, proxyPort);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(5000)//连接超时时间
                    .setConnectionRequestTimeout(5000)//从池中获取连接超时时间
                    .setSocketTimeout(10000)
                    .setProxy(proxy).build();
            post.setConfig(config);
        }
        
        StringEntity postEntity = new StringEntity(data, "UTF-8");
        post.addHeader("Content-Type", "text/xml");
        post.addHeader("User-Agent", "wxpay sdk java v3.0 " + wXPayConfig.getMchID());//1488379182
        post.setEntity(postEntity);
        String json = HttpClientCertUtil.execute(post);
        return json;
        /*CloseableHttpClient httpClient = HttpClientCertUtil.getHttpClient();
        CloseableHttpResponse httpResponse = null;
        try {
            HttpPost post = new HttpPost(url);
            
            RequestConfig config = null;
            if(isUse){
                HttpHost proxy = new HttpHost(proxyServer, proxyPort); 
                config = RequestConfig.custom().setProxy(proxy).build();
            }else{
                config = RequestConfig.custom().build();
            }
            post.setConfig(config);
            
            StringEntity postEntity = new StringEntity(data, "UTF-8");
            post.addHeader("Content-Type", "text/xml");
            post.addHeader("User-Agent", "wxpay sdk java v3.0 " + wXPayConfig.getMchID());//1488379182
            post.setEntity(postEntity);
            
            httpResponse = httpClient.execute(post);
            HttpEntity entity = httpResponse.getEntity();
            if(entity != null) {
                StatusLine status = httpResponse.getStatusLine();
                if (!(status.getStatusCode() >= 200 && status.getStatusCode() < 300)) {
                    EntityUtils.consumeQuietly(httpResponse.getEntity());
                    logger.error("weiChat_httpclient.statuscode错误：" + status.getStatusCode() + status.getReasonPhrase());
                    return null;
                }
                String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");//这个方法会调用inputStream.close释放连接
                logger.info(result);
                return result;
            }else {
                return null;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();//当连接已经被释放，则什么也不做，如果没被释放（非20*），则关闭连接
                }
            } catch (IOException e) {
                logger.error("httpclient连接池释放连接错误：" + e.getMessage());
                e.printStackTrace();
            }
        }*/
    }
    private String execute(String url) {
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
        return json;
        /*CloseableHttpClient httpClient = HttpClientUtil.getHttpClient();
        CloseableHttpResponse httpResponse = null;
        try {
            HttpGet get = new HttpGet(url);
            
            RequestConfig config = null;
            if(isUse){
                HttpHost proxy = new HttpHost(proxyServer, proxyPort); 
                config = RequestConfig.custom().setProxy(proxy).build();
            }else{
                config = RequestConfig.custom().build();
            }
            get.setConfig(config);
            
            httpResponse = httpClient.execute(get);
            StatusLine status = httpResponse.getStatusLine();
            HttpEntity entity = httpResponse.getEntity();
            if(entity != null) {
                if (!(status.getStatusCode() >= 200 && status.getStatusCode() < 300)) {
                    EntityUtils.consumeQuietly(entity);
                    logger.error("weiChat_httpclient.statuscode错误：" + status.getStatusCode() + status.getReasonPhrase());
                    return null;
                }
                String json = EntityUtils.toString(entity, "UTF-8");
                logger.info(json);
                return json;
            }else {
                return null;
            }
            
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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

    @Override
    public void logout(String token) {
        String username = jwtTokenUtil.getUsernameFromToken(token);

        String json = redisTemplate.opsForValue().get(getUserKey(username));
        if (json != null) {
            UserPermissions userPermissions = BeanUtils.readValue(json, UserPermissions.class);
            List<String> tokens = userPermissions.getTokens();
            if (tokens.contains(token)) {//删除服务端该token
                tokens.remove(token);
            }
            if (tokens.size() < 1) {//如果tokens中没有值了，则应该把整个信息都删除掉
                redisTemplate.delete(getUserKey(username));
            } else {//否则更新tokens
                userPermissions.setTokens(tokens);
                if (token.equals(userPermissions.getOldToken())) {//删除服务端该token(可能存在于oldToken字段)
                    userPermissions.setOldToken(null);
                }
                json = BeanUtils.writeValueAsString(userPermissions);
                long expires = redisTemplate.getExpire(getUserKey(username), TimeUnit.SECONDS);
                redisTemplate.opsForValue().set(getUserKey(username), json, expires, TimeUnit.SECONDS);//将权限信息和用户信息重新存入redis
            }
        }

    }

    @Override
    public void weChatBindPhone(String token, String phone) {
        if (!usersService.exists(phone)) {
            Users user = new Users();
            user.setUsername(phone);
            user.setPassword("budee123");//设置默认密码
            user.setPwdType(PwdType.no_pwd.getIndex());//密码类型，这里应该设置为1：无密码类型
            usersService.save(user);
        }
        String wxusername = jwtTokenUtil.getUsernameFromToken(token);
        usersService.userBind(phone, wxusername);
    }

    @Override
    public Map<String, String> phoneBindWeChat(String phoneToken, String wxToken) {
        Map<String, String> map = new HashMap<String, String>(1);
        String phone = null;
        try {//判断当前登录token是否有效，wxToken是刚刚生成的，无需校验
            phone = jwtTokenUtil.getUsernameFromToken(phoneToken);
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            String phoneJson = ops.get(getUserKey(phone));
            if (phoneJson != null) {
                UserPermissions userPermissions = BeanUtils.readValue(phoneJson, UserPermissions.class);
                if (userPermissions.getTokens().contains(phoneToken) || phoneToken.equals(userPermissions.getOldToken())) {
                    this.weChatBindPhone(wxToken, phone);
                    return map;
                }
            }
            map.put(ERR, RegisterErr.token_notfount.getIndex());
        } catch (Exception e) {
            map.put(ERR, RegisterErr.token_err.getIndex());
        }
        return map;
    }
}
