/**
 * 审核相关
 */
package com.mifan.article.web;

import com.mifan.article.domain.Moderation;
import com.mifan.article.service.ModerationService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.validation.ValidationGroups.Patch;
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
@RequestMapping("/moderations")
public class ModerationsController extends RestfulController<Moderation> {

    @Autowired
    private ModerationService moderationService;

    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_AUDITOR)
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
    @RequiresRoles(ROLE_AUDITOR)
    @RequestMapping(value = "/audit/{id}",method = RequestMethod.PATCH,
              consumes = APPLICATION_JSON_VALUE,
              produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<Moderation> data) {

        hasError(validate(data.getData(), Patch.class));
        data.getData().setId(id);
        moderationService.examineHumanTranslate(data.getData());
        return ResponseEntity.ok(null);
    }
}
