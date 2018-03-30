package com.mifan.user.web;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonView;
import com.mifan.user.domain.*;
import com.mifan.user.domain.support.BackPwdUser;
import com.mifan.user.service.SmsService;
import com.mifan.user.service.UsersService;
import com.mifan.user.type.RegisterErr;
import com.mifan.user.type.SpecType;
import com.mifan.user.utils.CaptchaUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.security.authentication.UserPermissions;
import org.moonframework.validation.ValidationGroups.Post;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Error;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.moonframework.web.utils.NetWorkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * <p>Controller:</p>
 * <p>RestController:就是Controller+ResponseBody</p>
 * <p>ResponseBody:MessageConverter, 消息转换器</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2015/11/27
 */
@RestController()
@RequestMapping("/users")
public class UsersController extends RestfulController<Users> {

    private static final String IP_KEY = "ip_key";

    @Autowired
    private UsersService usersService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CaptchaUtil captchaUtil;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPost(
            @RequestBody Data<Users> data) {

        String username = data.getData().getUsername();
        String smsCode = data.getData().getSmsCode();

        // 如果是管理员添加，则不需要验证码
        if (!isRoleAdmin()) {
            if (!smsService.verifySMSCode(username, smsCode, SpecType.WEB_REGISTER)) {
                Error error = Error.builder()
                        .code(RegisterErr.sms_code_err.getIndex())
                        .detail(RegisterErr.sms_code_err.getName()).build();
                return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.BAD_REQUEST);
            }
        }
        if (usersService.exists(username)) {
            Error error = Error.builder()
                    .code(RegisterErr.user_exists.getIndex())
                    .detail(RegisterErr.user_exists.getName()).build();
            return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<Response> response = super.doPost(data);
        Long userId = data.getData().getInviter();
        if (response.getStatusCode().is2xxSuccessful() && userId != null) {
            CompletableFuture.runAsync(() -> Services.transactional(UserKarmaLogs.class, () -> {
                UserKarmaLogs userKarmaLogs = new UserKarmaLogs();
                userKarmaLogs.setUserId(userId);
                userKarmaLogs.setAction(UserKarmaLogs.Event.INVITATION);
                if (Services.save(UserKarmaLogs.class, userKarmaLogs) > 0) {
                    UserInvitations invitation = new UserInvitations();
                    invitation.setUserId(userId);
                    invitation.setTargetId(data.getData().getId());
                    Services.save(UserInvitations.class, invitation);
                }
                return 1;
            }));
        }
        HttpServletRequest request = super.getHttpServletRequest();
        String ip = NetWorkUtils.getIpAddress(request);
        // 如果这个ip注册成功了一个号码，则清空redis中同一个ip获取短信验证码的次数
        if (!StringUtils.isEmpty(ip)) {
            this.redisTemplate.delete(getIpKey(ip));
        }
        return response;
    }

    @RequiresAuthentication
    @RequestMapping(value = "/{id}",
            method = RequestMethod.PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<Users> data) {
        hasPermissions(id);
        return super.doPatch(id, data);
    }

    @RequiresAuthentication
    @RequiresRoles(value = {ROLE_ADMIN, ROLE_REMOTE}, logical = Logical.OR)
    @JsonView(Users.WithoutPasswordView.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {
        return super.doGet(id, include);
    }

    @RequiresAuthentication
    @RequiresRoles(value = {ROLE_ADMIN, ROLE_REMOTE}, logical = Logical.OR)
    @JsonView(Users.WithoutPasswordView.class)
    @RequestMapping(method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {
        return super.doGetPage(page, size, sort, include);
    }

    @RequestMapping(value = "/exist", method = RequestMethod.GET)
    public ResponseEntity<Response> exist(@RequestParam(value = "username", required = true) String username) {
        boolean flag = usersService.exists(username);
        return new ResponseEntity<>(Responses.builder().data(flag), HttpStatus.OK);
    }

    //忘记密码 API
    @RequestMapping(value = "/backpwd", method = RequestMethod.POST)
    public ResponseEntity<Response> backPassword(@RequestBody Data<BackPwdUser> data) {
        hasError(validate(data.getData(), Post.class));//校验必填参数

        String username = data.getData().getUsername();
        String password = data.getData().getPassword();
        String confirmPassword = data.getData().getConfirmPassword();
        String smsCode = data.getData().getSmsCode();
        if (!usersService.exists(username)) {
            Error error = Error.builder().code(RegisterErr.user_not_exists.getIndex()).detail(RegisterErr.user_not_exists.getName()).build();
            return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.BAD_REQUEST);
        }
        if (!password.equals(confirmPassword)) {
            Error error = Error.builder().code(RegisterErr.password_atypism.getIndex()).detail(RegisterErr.password_atypism.getName()).build();
            return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.BAD_REQUEST);
        }
        if (!smsService.verifySMSCode(username, smsCode, SpecType.WEB_FIND_PASSWORD)) {
            Error error = Error.builder().code(RegisterErr.sms_code_err.getIndex()).detail(RegisterErr.sms_code_err.getName()).build();
            return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.BAD_REQUEST);
        }
        int n = usersService.updatePassword(username, password);
        if (n < 1) {
            Error error = Error.builder()
                    .code(RegisterErr.user_save_err.getIndex())
                    .detail(RegisterErr.user_save_err.getName()).build();
            return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // 修改密码成功后，要将redis中的token删除，让用户重新登录
        this.redisTemplate.delete(UserPermissions.OBJECT_KEY + username);
        // 修改密码成功后，将登录错误次数清零
        captchaUtil.clearErrPasswordCount(username);
        return ResponseEntity.noContent().build();
    }


    /**
     * <p>保存用户到多个群组</p>
     *
     * @param siteId 站点ID
     * @param userId 用户ID
     * @return Response
     */
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/sites/{siteId}/users/{userId}/groups", method = RequestMethod.POST)
    public ResponseEntity<Response> doSaveUsersGroups(@PathVariable Long siteId, @PathVariable Long userId, @RequestBody Data<UsersGroups> data) {
        // 校验必填参数
        hasError(validate(data.getData(), Post.class));
        if (0 == usersService.addGroups(siteId, userId, data.getData().getGroupIds())) {
            throw new RuntimeException();
        }
        return ResponseEntity.ok(null);
    }

    /**
     * <p>保存用户到多个角色</p>
     *
     * @param siteId 站点ID
     * @param userId 用户ID
     * @return Response
     */
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/sites/{siteId}/users/{userId}/roles", method = RequestMethod.POST)
    public ResponseEntity<Response> doSaveUsersRoles(@PathVariable Long siteId, @PathVariable Long userId, @RequestBody Data<UsersRoles> data) {
        // 校验必填参数
        hasError(validate(data.getData(), Post.class));
        if (0 == usersService.addRoles(siteId, userId, data.getData().getRoleIds())) {
            throw new RuntimeException();
        }
        return ResponseEntity.ok(null);
    }

    private String getIpKey(String ip) {
        return IP_KEY + ip;
    }
}
