package com.mifan.user.web;

import com.mifan.user.domain.Users;
import com.mifan.user.domain.support.LoginUser;
import com.mifan.user.domain.support.PhoneLogin;
import com.mifan.user.service.AuthService;
import com.mifan.user.service.SmsService;
import com.mifan.user.service.UsersService;
import com.mifan.user.type.PwdType;
import com.mifan.user.type.RegisterErr;
import com.mifan.user.type.SpecType;
import com.mifan.user.utils.CaptchaUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.validation.ValidationGroups.Post;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Error;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.moonframework.web.utils.ValidatorCodeUtils;
import org.moonframework.web.utils.ValidatorCodeUtils.ValidatorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录相关
 *
 * @author ZYW
 */
@RestController
@RequestMapping("/oauth")
public class AuthController extends RestfulController<Users> {

    private static final Log logger = LogFactory.getLog(AuthController.class);

    private static final String REFRESH_LOCK = "refresh_lock";

    private static final String ERR_COUNT = "err_count";

//    private static final String ERR = "err";

    @Value("${moon.data.token.token-header}")
    private String tokenHeader;

    @Value("${moon.data.token.token-head}")
    private String tokenHead;

    @Value("${moon.security.cas.filter.appKey}")
    private String appKey;

    @Value("${moon.data.token.expiration}")
    private long expiration;

    @Value("${moon.data.token.remember-me-expiration}")
    private long rememberMeExpiration;

    @Autowired
    private UsersService usersService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CaptchaUtil captchaUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequiresAuthentication
    @RequestMapping(value = "/phone/bind", method = RequestMethod.GET)
    public ResponseEntity<Response> weChatBindPhone(HttpServletRequest request, String phone, String smsCode) {//微信绑定手机：当前登录帐号是微信;权限必须是登录
        if (!smsService.verifySMSCode(phone, smsCode, SpecType.WEB_LOGIN)) {
            Error error = Error.builder()
                    .code(RegisterErr.sms_code_err.getIndex())
                    .detail(RegisterErr.sms_code_err.getName()).build();
            return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.BAD_REQUEST);
        }
        String authHeader = request.getHeader(tokenHeader);
        String token = authHeader.substring(tokenHead.length());
        authService.weChatBindPhone(token, phone);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/phone", method = RequestMethod.POST)
    public ResponseEntity<Response> phoneLogin(/*@RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "smsCode", required = true) String smsCode, boolean rememberme*/@RequestBody Data<PhoneLogin> data) {
        hasError(validate(data.getData(), Post.class));//校验必填参数

        String username = data.getData().getUsername();
        String smsCode = data.getData().getSmsCode();
        boolean isRememberMe = data.getData().getRememberme();
        if (!smsService.verifySMSCode(username, smsCode, SpecType.WEB_LOGIN)) {
            Error error = Error.builder()
                    .code(RegisterErr.sms_code_err.getIndex())
                    .detail(RegisterErr.sms_code_err.getName()).build();
            return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.BAD_REQUEST);
        }
        Users user;
        if (!usersService.exists(username)) {//如果用户不存在，则后台自动注册
            user = new Users();
            user.setUsername(username);
            user.setPassword("budee123");//设置默认密码
            user.setPwdType(PwdType.no_pwd.getIndex());//密码类型，这里应该设置为1：无密码类型
            usersService.save(user);
        }
        user = usersService.findUserByUsername(username);
        Map<String, String> map = authService.accessToken(username, user.getId(), isRememberMe ? 1 : 0);
        map.put("username", username);

        return new ResponseEntity<>(Responses.builder().data(map), HttpStatus.OK);
    }
    
    /*@RequestMapping(value = "/phone/register", method = RequestMethod.POST)
    public ResponseEntity<Response> phoneRegister(@RequestBody Data<PhoneLogin> data){
        hasError(validate(data.getData(), Post.class));//校验必填参数
        
        String username = data.getData().getUsername();
        String smsCode = data.getData().getSmsCode();
        if(!smsService.verifySMSCode(username, smsCode,SpecType.WEB_LOGIN)){
            Error error = Error.builder()
                    .code(RegisterErr.sms_code_err.getIndex())
                    .detail(RegisterErr.sms_code_err.getName()).build();
            return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.BAD_REQUEST);
        }
        if(usersService.exists(username)) {
            Error error = Error.builder()
                    .code(RegisterErr.user_exists.getIndex())
                    .detail(RegisterErr.user_exists.getName()).build();
            return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.BAD_REQUEST);
        }
        Users user = new Users();
        user.setUsername(username);
        user.setPassword("budee123");//设置默认密码
        user.setPwdType(PwdType.no_pwd.getIndex());//密码类型，这里应该设置为1：无密码类型
        usersService.save(user);
        
        user = usersService.findUserByUsername(username);
        Map<String, String> map = authService.accessToken(username, user.getId(), 0);
        
        return new ResponseEntity<>(Responses.builder().data(map), HttpStatus.OK);
    }*/

    @RequestMapping(value = "/token", method = RequestMethod.POST)//登录验证
    public ResponseEntity<Response> login(HttpServletRequest request, @RequestBody Data<LoginUser> data) {
        hasError(validate(data.getData(), Post.class));//校验必填参数

        String username = data.getData().getUsername();
        String password = data.getData().getPassword();
        Long errCount = captchaUtil.getErrPasswordCount(username);//从缓存获取登录错误次数,如果这个次数大于等于3，则一定会校验验证码
        if (errCount >= 3) {
            String code = captchaUtil.getValidatorCode(username);
            String verifyCode = data.getData().getVerifyCode();
            if (code == null || !code.equals(verifyCode)) {
                errCount = captchaUtil.addErrPasswordCount(username);//增加错误次数
                Error error = Error.builder()
                        .code(RegisterErr.verifycode_err.getIndex())
                        .detail(RegisterErr.verifycode_err.getName()).build();
                return new ResponseEntity<>(Responses.builder().meta(ERR_COUNT, errCount).error(error), HttpStatus.BAD_REQUEST);
            }
        }

        Users user = usersService.findUserByUsername(username);
        if (user == null) {
            Error error = Error.builder()
                    .code(RegisterErr.user_not_exists.getIndex())
                    .detail(RegisterErr.user_not_exists.getName()).build();
            return new ResponseEntity<>(Responses.builder().meta(ERR_COUNT, captchaUtil.getErrPasswordCount(username)).error(error), HttpStatus.BAD_REQUEST);
        }

        if (user.getPwdType() == PwdType.no_pwd.getIndex() || !passwordEncoder.matches(password, user.getPassword())) {
            errCount = captchaUtil.addErrPasswordCount(username);//增加错误次数
            Error error = Error.builder()
                    .code(RegisterErr.password_err.getIndex())
                    .detail(RegisterErr.password_err.getName()).build();
            return new ResponseEntity<>(Responses.builder().meta(ERR_COUNT, errCount).error(error), HttpStatus.BAD_REQUEST);
        }

        Map<String, String> map = authService.accessToken(username, user.getId(), data.getData().isRememberme() ? 1 : 0);

        // The authorization server MUST include the HTTP "Cache-Control" response header field [RFC2616] with a value of "no-store" in any response
        // containing tokens, credentials, or other sensitive information, as well as the "Pragma" response header field [RFC2616] with a value of "no-cache".
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        return new ResponseEntity<>(Responses.builder().data(map), headers, HttpStatus.OK);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/token/refresh", method = RequestMethod.GET)//刷新token
    public ResponseEntity<Response> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authHeader = request.getHeader(tokenHeader);
        String oldToken = authHeader.substring(tokenHead.length());
        String lock = redisTemplate.opsForValue().get(REFRESH_LOCK + oldToken);
        if ("true".equals(lock)) {//5秒之内不允许刷新两次
            Error error = Error.builder()
                    .code(RegisterErr.refresh_token_many.getIndex())
                    .detail(RegisterErr.refresh_token_many.getName()).build();
            return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.FORBIDDEN);
        }
        Map<String, String> map = authService.refreshToken(oldToken);
        redisTemplate.opsForValue().set(REFRESH_LOCK + oldToken, "true", 5, TimeUnit.SECONDS);//将此次刷新时间保存在redis中5秒，在这5秒中不允许下次请求

        return new ResponseEntity<>(Responses.builder().data(map), HttpStatus.OK);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity<Response> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(tokenHeader);
        String oldToken = authHeader.substring(tokenHead.length());
        authService.logout(oldToken);
        return ResponseEntity.noContent().build();

    }

    @RequestMapping(value = "/check.jpg", method = RequestMethod.GET)
    public void createCode(@RequestParam(value = "username", required = true) String username, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 禁止图像缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ValidatorCode codeUtil = ValidatorCodeUtils.getCode();
        //存到缓存
        captchaUtil.addValidatorCode(username, codeUtil.getCode());
        // 输出打web页面  
        ImageIO.write(codeUtil.getImage(), "jpg", response.getOutputStream());
    }
}
