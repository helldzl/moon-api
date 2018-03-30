package com.mifan.user.web;

import com.mifan.user.domain.UserInvitations;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/31
 */
@RestController
@RequestMapping("/invitations")
public class UserInvitationsControllers extends RestfulController<UserInvitations> {

    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {
        return super.doGetPage(page, size, sort, include);
    }

    @Override
    protected Criterion criterion(Criterion criterion) {
        if (isAdmin()) {
            return criterion;
        }
        return Restrictions.and(
                Restrictions.eq(UserInvitations.USER_ID, getCurrentUserId()),
                criterion);
    }

}
