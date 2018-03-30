/**
 * 
 */
package com.mifan.article.web;

import com.mifan.article.domain.TranslateTask;
import com.mifan.article.domain.support.TranslateTaskAudit;
import com.mifan.article.domain.support.ValidationGroups.AdminPost;
import com.mifan.article.domain.support.ValidationGroups.AuditorPatch;
import com.mifan.article.domain.support.ValidationGroups.TranslateTaskPatch;
import com.mifan.article.service.TranslateTaskService;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author ZYW
 *
 */
@RestController
@RequestMapping("/translateTasks")
public class TranslateTaskController  extends RestfulController<TranslateTask>{

    @Autowired
    private TranslateTaskService translateTaskService;

    @RequiresAuthentication
    @RequiresRoles(ROLE_AUDITOR)
    @RequestMapping(value = "/auditors/{id}",method = RequestMethod.GET)
     public ResponseEntity<Response> doGetOfAuditor(
                @PathVariable Long id) {

        TranslateTask task = translateTaskService.findOne(id);
        Long currentUserId = getCurrentUserId();
        if(task.getState() != 3 && !task.getAuditor().equals(currentUserId)) {
            throw new UnauthorizedException();
        }
        return super.doGet(id, new String[]{TranslateTask.AUDITOR});
    }

    @RequiresAuthentication
    @RequiresRoles(ROLE_AUDITOR)
    @RequestMapping(value="/auditors",method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPageOfAuditor(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = true, name = "filter[state]") Integer state ) {
        //写多个查询列表的接口是因为，如果有个人有多个角色，他在查看其中一个角色的列表时无法区分权限
        HttpServletRequest request = getHttpServletRequest();
        Long currentUserId = getCurrentUserId();
        TranslateTask task = new TranslateTask();
        task.setState(state);
        task.setAuditor(currentUserId);
        Page<TranslateTask> result = translateTaskService.doGetPageOfAuditor(task, page, size);
        return ResponseEntity.ok(Responses.builder().page(result, "/article/translateTasks/auditors", request.getParameterMap()).data(result.getContent()));
    }

    @RequiresAuthentication
    @RequiresRoles(ROLE_AUDITOR)
    @RequestMapping(value = "/auditors/{id}",method = RequestMethod.PATCH,
              consumes = APPLICATION_JSON_VALUE,
              produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> auditorPatch(
            @PathVariable Long id,
            @RequestBody Data<TranslateTaskAudit> data) {
        //1开始审核，2暂存、提交审核结果
        hasError(validate(data.getData(), AuditorPatch.class));
        Long currentUserId = getCurrentUserId();
        data.getData().setAuditor(currentUserId);
        data.getData().setId(id);
        translateTaskService.updateOfAuditor(data.getData());
        return ResponseEntity.ok(null);
    }

    @RequiresAuthentication
    @RequiresRoles(ROLE_AD_USER)
    @RequestMapping(value = "/translators/{id}",method = RequestMethod.GET)
     public ResponseEntity<Response> doGetOfTranslator(
                @PathVariable Long id) {
        TranslateTask task = translateTaskService.findOne(id);
        Long currentUserId = getCurrentUserId();
        if(task.getState() == 4 || task.getState() != 1 && !task.getTranslator().equals(currentUserId)) {
            throw new UnauthorizedException();
        }
        return super.doGet(id, new String[]{TranslateTask.ROLE_TRANSLATOR});
    }

    @RequiresAuthentication
    @RequiresRoles(ROLE_AD_USER)
    @RequestMapping(value="/translators",method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPageOfTranslator(//写多个查询列表的接口是因为，如果有个人有多个角色，他在查看其中一个角色的列表时无法区分权限
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = true, name = "filter[state]") Integer state  ) {

        HttpServletRequest request = getHttpServletRequest();
        Long currentUserId = getCurrentUserId();
        TranslateTask task = new TranslateTask();
        task.setState(state);
        task.setTranslator(currentUserId);
        Page<TranslateTask> result = translateTaskService.doGetPageOfTranslator(task, page, size);
        return ResponseEntity.ok(Responses.builder().page(result, "/article/translateTasks/translators", request.getParameterMap()).data(result.getContent()));
    }

    @RequiresAuthentication
    @RequiresRoles(ROLE_AD_USER)
    @RequestMapping(value = "/translators/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> translatorDelete(
            @PathVariable Long id){
        //删除任务（退回任务）
        Long currentUserId = getCurrentUserId();
        TranslateTask one = Services.findOne(TranslateTask.class, id);
        if(one.getState() != 2 || !one.getTranslator().equals(currentUserId)) {
            throw new UnauthorizedException();
        }

        TranslateTask task = new TranslateTask();
        task.setId(id);
        task.setPostId(0L);
        task.setTranslator(0L);
        task.setState(1);
        task.setModifier(currentUserId);

        Services.update(TranslateTask.class, task);
        return ResponseEntity.ok(null);
    }

    @RequiresAuthentication
    @RequiresRoles(ROLE_AD_USER)
    @RequestMapping(value = "/translators/{id}",method = RequestMethod.PATCH,
              consumes = APPLICATION_JSON_VALUE,
              produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> translatorPatch(
            @PathVariable Long id,
            @RequestBody Data<TranslateTask> data) {
        //领取任务，添加、修改翻译，审核失败后继续翻译
        hasError(validate(data.getData(), TranslateTaskPatch.class));
        data.getData().setId(id);
        Long currentUserId = getCurrentUserId();
        data.getData().setModifier(currentUserId);
        translateTaskService.updateOfTranslator(data.getData());
        return ResponseEntity.ok(null);
    }

    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
     public ResponseEntity<Response> doGet(
                @PathVariable Long id,
                @RequestParam(required = false) String[] include) {

        return super.doGet(id, include);
    }

    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
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
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> doDelete(
            @PathVariable Long id){

        return super.doDelete(id);
    }

    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}",method = RequestMethod.PATCH,
              consumes = APPLICATION_JSON_VALUE,
              produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<TranslateTask> data) {
        //修改状态，字数，金额
        return super.doPatch(id, data);
    }

    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.POST,
              consumes = APPLICATION_JSON_VALUE,
              produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPost(
            @RequestBody Data<TranslateTask> data){
        TranslateTask entity = data.getData();
        Long userId = getCurrentUserId();
        if (userId != null) {
            entity.setCreator(userId);
            entity.setModifier(userId);
        }
        hasError(validate(entity, AdminPost.class));
        afterPostValidate(entity);
        switch (Services.save(entityClass, entity)) {
            case 0:
                throw new IllegalStateException();
            case ACCEPTED:
                return ResponseEntity.accepted().build();
            case NO_CONTENT:
                return ResponseEntity.noContent().build();
            default:
                Map<String, Object> result = new HashMap<>(16);
                result.put("type", "translateTasks");
                result.put("id", entity.getId());
                return ResponseEntity.created(URI.create("translateTasks" + "/" + entity.getId())).body(Responses.builder().data(result));
        }
//        return super.doPost(data);
    }

    @Override
    protected Criterion doDeleteCriterion() {
        return Restrictions.eq(TranslateTask.STATE,1);
    }

}
