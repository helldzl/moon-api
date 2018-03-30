package com.mifan.article.web;


import com.mifan.article.domain.support.UserKarmaLogs;
import com.mifan.article.domain.support.UserProfiles;
import com.mifan.article.domain.support.Users;
import com.mifan.article.feign.user.UserAccountsClient;
import com.mifan.article.feign.user.UserKarmaLogsClient;
import com.mifan.article.feign.user.UserProfilesClient;
import com.mifan.article.feign.user.UsersClient;
import org.moonframework.web.feign.Feigns;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/10
 */
@RestController
public class UserController {

    @Autowired
    private UserAccountsClient userAccountsClient;

    @Autowired
    private UsersClient usersClient;

    @Autowired
    private UserProfilesClient userProfilesClient;

    @Autowired
    private UserKarmaLogsClient userKarmaLogsClient;

    @GetMapping("/user/{id}")
    public ResponseEntity<? extends Response> doGet(
            @PathVariable(value = "id") Long id) {

        // 临时提权执行远程API
        Feigns.asRemote("ROLE_REMOTE", "ROLE_REMOTE_1");
        ResponseEntity<Data<Users>> usersData = usersClient.doGet(id, Feigns.params().fields("users", f -> f.add("id").add("username")).build());

//        ResponseEntity<Data<List<Users>>> data = usersClient.doGetPage(Feigns.params()
//                .fields("users", f -> f.add("id").add("username").add("reg_from"))
//                .build());
//        System.out.println();
        Data<Users> body = usersData.getBody();
        if (body == null) {
            return ResponseEntity.notFound().build();
        }
        return usersData;

//        ResponseEntity<? extends Response> result = userFeignClient.doGetProfiles(id, null);
//        ChannelsService channelsService = ApplicationContextHolder.getBean("channelsServiceImpl", ChannelsService.class);
//        Long channel = channelsService.channel(id);
//        System.out.println("channel ID:"+id);
//        return result;
    }

    @GetMapping("/user")
    public ResponseEntity<? extends Response> doGetPage(
            @RequestParam(required = false, value = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, value = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false, value = "sort") String[] sort,
            @RequestParam(required = false, value = "include") String[] include) {

        Feigns.ParamsBuilder builder = Feigns.params().page(p -> p.page(page).size(size));
        if (sort != null) {

        }
        builder.include(include);

        ResponseEntity<? extends Response> result = userProfilesClient.doGetPage(builder.build());
        return result;
    }

    @RequestMapping(value = "/user/{id}",
            method = PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<UserProfiles> data) {

        ResponseEntity<Data<Void>> responseEntity = userProfilesClient.doPatch(id, data);
        return responseEntity;
    }

    @GetMapping("/user/karma")
    public ResponseEntity<? extends Response> karma() {
        UserKarmaLogs userKarmaLogs = new UserKarmaLogs();
        userKarmaLogs.setUserId(12L);
        userKarmaLogs.setAction(UserKarmaLogs.Event.TRANSLATE);
        userKarmaLogs.setScore(100);

        ResponseEntity<Data<Map<String, Object>>> responseEntity = userKarmaLogsClient.doPost(Data.of(userKarmaLogs));
        Map<String, Object> data = responseEntity.getBody().getData();
        if (data == null) {
            throw new IllegalStateException("奖励米粒失败");
        }

        return ResponseEntity.ok(Responses.builder().data("ok"));
    }

}
