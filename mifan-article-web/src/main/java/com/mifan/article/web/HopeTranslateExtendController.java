/**
 * 
 */
package com.mifan.article.web;

import com.mifan.article.domain.HopeTranslateExtend;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author ZYW
 *
 */
@RestController
@RequestMapping("/hopeTranslateExtends")
public class HopeTranslateExtendController extends RestfulController<HopeTranslateExtend> {

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
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
     public ResponseEntity<Response> doGet(
                @PathVariable Long id,
                @RequestParam(required = false) String[] include) {
        return super.doGet(id, include);
    }

    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_AUDITOR)
    @RequestMapping(value = "/{id}",method = RequestMethod.PATCH,
              consumes = APPLICATION_JSON_VALUE,
              produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<HopeTranslateExtend> data) {

        //场景：驳回希望精翻
        data.getData().setState(2);
        return super.doPatch(id, data);
    }
}
