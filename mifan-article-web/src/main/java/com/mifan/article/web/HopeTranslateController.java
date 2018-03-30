/**
 * 2017年9月5日 16:05:38
 */
package com.mifan.article.web;

import com.mifan.article.domain.HopeTranslate;
import com.mifan.article.service.HopeTranslateService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author ZYW
 *
 */
@RestController
@RequestMapping("/hopeTranslates")
public class HopeTranslateController extends RestfulController<HopeTranslate> {

    @Autowired
    private HopeTranslateService hopeTranslateService;

    @Override
    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.POST,
              consumes = APPLICATION_JSON_VALUE,
              produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPost(
            @RequestBody Data<HopeTranslate> data){

        return super.doPost(data);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {

        return super.doGetPage(page, size, sort, include);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> doDelete(
            @PathVariable Long id) {

        HopeTranslate hope = hopeTranslateService.findOne(id);
        Long userId = hope == null ? null : hope.getUserId();
        hasPermissions(userId);
        
        return super.doDelete(id);
    }

    @Override
    protected Criterion criterion(Criterion criterion) {
        if (isRoleADUser() || isRoleAdmin()) {
            return criterion;
        }

        return Restrictions.and(
                Restrictions.eq(HopeTranslate.USER_ID, getCurrentUserId()),
                Restrictions.eq(BaseEntity.ENABLED, 1),
                criterion);
    }
}
