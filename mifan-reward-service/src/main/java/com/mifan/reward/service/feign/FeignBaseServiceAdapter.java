package com.mifan.reward.service.feign;

import com.mifan.reward.domain.support.RewardUsers;
import com.mifan.reward.domain.support.UserProfiles;
import com.mifan.reward.service.feign.user.UserProfilesClient;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.repository.BaseDao;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.web.feign.Feigns;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeignBaseServiceAdapter<T extends BaseEntity, E extends BaseDao<T>> extends AbstractBaseService<T, E> {
    @Autowired
    private UserProfilesClient userProfilesClient;

    protected UserProfiles profiles(Long userId) {
        Feigns.asRemote();
        ResponseEntity<Data<UserProfiles>> response = userProfilesClient.doGet(userId);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getData();
        } else {
            return null;
        }
    }

    protected Map<Long, String> profiles(Collection<? extends Number> collection) {
        ResponseEntity<Data<List<UserProfiles>>> response = userProfilesClient.doGetPage(Feigns.params()
                .page(p -> p.page(1).size(100))
                .fields("user_profiles", f -> f.add(UserProfiles.ID).add(UserProfiles.NICKNAME))
                .filter(UserProfiles.ID, collection)
                .build());
        return response.getBody().getData().stream().collect(Collectors.toMap(UserProfiles::getId, UserProfiles::getNickname));
    }


    protected UserProfiles getProfiles(Long userId) {
        ResponseEntity<Data<List<UserProfiles>>> response = userProfilesClient.doGetPage(Feigns.params()
                .page(p -> p.page(1).size(1))
                .fields("user_profiles", f -> f.add(UserProfiles.ID).add(UserProfiles.NICKNAME).add(UserProfiles.USER_AVATAR))
                .filter(UserProfiles.ID, userId)
                .build());
        if (CollectionUtils.isEmpty(response.getBody().getData())) {
            return null;
        } else {
            return response.getBody().getData().get(0);
        }
    }

    // TODO 修改为不需要登录就可以获取用户昵称的方式
    protected RewardUsers getUserInfo(Long userId) {
        UserProfiles profile = getProfiles(userId);
        if (profile == null) {
            return null;
        }
        //TODO 马赛克名字
        RewardUsers userInfo = new RewardUsers();
        userInfo.setId(profile.getId());
        userInfo.setName(profile.getNickname());
        userInfo.setAvatar(profile.getUserAvatar());
        return userInfo;
    }

}
