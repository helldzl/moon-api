package com.mifan.award.web;

import com.mifan.award.domain.*;
import com.mifan.award.domain.support.UserRecord;
import com.mifan.award.service.AwardService;
import com.mifan.award.service.enums.PrizeState;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.model.mongodb.enums.DeleteState;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.web.core.BaseController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.moonframework.web.utils.NetWorkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AwardController extends BaseController {

    @Autowired
    private AwardService awardService;

    @RequestMapping(value = "/categorys", method = RequestMethod.GET)
    public Object queryForCategoryList(
            @RequestParam(required = false, defaultValue = "1") Integer state,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort
    ) {
        HttpServletRequest request = getHttpServletRequest();
        if (size > 50) {
            size = 50;
        }
        if (size < 0) {
            size = 0;
        }
        Category findEntity = new Category();
        findEntity.setIsDel(DeleteState.CREATE.getCode());
        findEntity.setState(state);

        Page<Category> result = awardService.queryForCategoryList(Example.of(findEntity), pageRequest(page, size, sort));
        return ResponseEntity.ok(Responses.builder()
                .page(result, "/award/categorys", request.getParameterMap())
                .data(result.getContent()));
    }

    @RequestMapping(value = "/notices", method = RequestMethod.GET)
    public Object queryForNoticeList(
            @RequestParam(required = false) Integer state,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort
    ) {
        HttpServletRequest request = getHttpServletRequest();
        if (size > 50) {
            size = 50;
        }
        if (size < 0) {
            size = 0;
        }
        Notice findEntity = new Notice();
        findEntity.setIsDel(DeleteState.CREATE.getCode());
        findEntity.setState(state);

        Page<Notice> result = awardService.queryForNoticeList(Example.of(findEntity), pageRequest(page, size, sort));
        return ResponseEntity.ok(Responses.builder()
                .page(result, "/award/notices", request.getParameterMap())
                .data(result.getContent()));
    }

    @RequestMapping(value = "/notices/{id}", method = RequestMethod.GET)
    public Object queryForNoticeObject(@PathVariable String id) {
        return ResponseEntity.ok(Responses.builder().data(awardService.queryForNoticeObject(id)));
    }

    @RequestMapping(value = "/prizes", method = RequestMethod.GET)
    public ResponseEntity<Response> queryForPrizeList(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false, defaultValue = "1") Integer state,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort
    ) {
        HttpServletRequest request = getHttpServletRequest();
        if (size > 50) {
            size = 50;
        }
        if (size < 0) {
            size = 0;
        }

        Prize findEntity = new Prize();
        findEntity.setIsDel(DeleteState.CREATE.getCode());
        findEntity.setState(state);
        findEntity.setUserId(userId);
        findEntity.setProductId(productId);
        findEntity.setCategoryId(categoryId);

        Page<Prize> result = awardService.queryForPrizeList(Example.of(findEntity), pageRequest(page, size, sort));
        return ResponseEntity.ok(Responses.builder()
                .page(result, "/award/prizes", request.getParameterMap())
                .data(result.getContent()));
    }

    @RequestMapping(value = "/prizes/{id}", method = RequestMethod.GET)
    public Object queryForPrizeObject(@PathVariable String id) {
        return ResponseEntity.ok(Responses.builder().data(awardService.queryForPrizeObject(id)));
    }

    @RequestMapping(value = "/records", method = RequestMethod.GET)
    public Object queryForRecordList(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String prizeId,
            @RequestParam(required = false, defaultValue = "1") Integer state,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort
    ) {
        HttpServletRequest request = getHttpServletRequest();
        if (size > 50) {
            size = 50;
        }
        if (size < 0) {
            size = 0;
        }
        Record findEntity = new Record();
        findEntity.setIsDel(DeleteState.CREATE.getCode());
        findEntity.setState(state);
        findEntity.setUserId(userId);
        findEntity.setPrizeId(prizeId);

        Page<Record> result = awardService.queryForRecordList(Example.of(findEntity), pageRequest(page, size, sort));
        return ResponseEntity.ok(Responses.builder()
                .page(result, "/award/records", request.getParameterMap())
                .data(result.getContent()));
    }

    @RequestMapping(value = "/records/{id}", method = RequestMethod.GET)
    public Object queryForRecordObject(@PathVariable String id) {
        return ResponseEntity.ok(Responses.builder().data(awardService.queryForRecordObject(id)));
    }


    @RequestMapping(value = "/shares", method = RequestMethod.GET)
    public Object queryForShareList(
            @RequestParam(required = false) String prizeId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false, defaultValue = "2") Integer state,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort
    ) {
        HttpServletRequest request = getHttpServletRequest();
        if (size > 50) {
            size = 50;
        }
        if (size < 0) {
            size = 0;
        }
        Share findEntity = new Share();
        findEntity.setIsDel(DeleteState.CREATE.getCode());
        findEntity.setState(state);
        findEntity.setPrizeId(prizeId);
        findEntity.setUserId(userId);
        findEntity.setProductId(productId);

        Page<Share> result = awardService.queryForShareList(Example.of(findEntity), pageRequest(page, size, sort));
        return ResponseEntity.ok(Responses.builder()
                .page(result, "/award/shares", request.getParameterMap())
                .data(result.getContent()));
    }

    @RequestMapping(value = "/shares/{id}", method = RequestMethod.GET)
    public Object queryForShareObject(@PathVariable String id) {
        return ResponseEntity.ok(Responses.builder().data(awardService.queryForShareObject(id)));
    }

    @RequiresAuthentication
    @RequestMapping(value = "/records", method = RequestMethod.POST)
    public Object createRecord(@RequestBody Data<Record> data) {

        Long userId = getCurrentUserId();

        if (StringUtils.isEmpty(userId)) {
            throw new IllegalStateException();
        }
        HttpServletRequest request = getHttpServletRequest();
        Record saveRecord = data.getData();
        saveRecord.setIp(NetWorkUtils.getIpAddress(request));
        saveRecord.setUserId(String.valueOf(userId));
        switch (awardService.saveRecord(saveRecord)) {
            case -4060:
                return ResponseEntity.badRequest().body(Responses.builder().error(Responses.error(
                        String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
                        HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                        "下单次数为负")));
            case -4061:
                return ResponseEntity.badRequest().body(Responses.builder().error(Responses.error(
                                                        String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
                                                        HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                                                        "用户米粒余额不足")));
            case -4062:
                return ResponseEntity.badRequest().body(Responses.builder().error(Responses.error(
                                                        String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
                                                        HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                                                        "奖品剩余夺宝次数不足")));
            default:
                Map<String, Object> result = new HashMap<>(16);
                result.put("type", "record");
                result.put("id", saveRecord.getId());
                return ResponseEntity.created(URI.create("/award/records" + saveRecord.getId())).body(Responses.builder().data(result));
        }
    }

    @RequiresAuthentication
    @RequestMapping(value = "/shares", method = RequestMethod.POST)
    public Object createShare(@RequestBody Data<Share> data) {

        Long userId = getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new IllegalStateException();
        }
        Share saveShare = data.getData();
        saveShare.setUserId(String.valueOf(userId));
        switch (awardService.saveShare(saveShare)) {
            case -202:
                return ResponseEntity.accepted().build();
            case -404:
                return ResponseEntity.notFound().build();
            default:
                Map<String, Object> result = new HashMap<>(16);
                result.put("type", "share");
                result.put("id", saveShare.getId());
                return ResponseEntity.created(URI.create("/award/shares/" + saveShare.getId())).body(Responses.builder().data(result));
        }
    }

    //@RequiresAuthentication
    @RequestMapping(value = "/user/prizes", method = RequestMethod.GET)
    public Object getUserPrizeRecords(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false, name = "prizeState", defaultValue = "0") int prizeState,
            @RequestParam(required = false) String[] sort)
    {
        HttpServletRequest request = getHttpServletRequest();
        if (size > 50) {
            size = 50;
        }
        if (size < 0) {
            size = 0;
        }
        if(StringUtils.isEmpty(userId)){
            if(getCurrentUserId()!=null) {
                userId = String.valueOf(getCurrentUserId());
            } else {
                throw new IllegalStateException();
            }
        }
        Record findEntity = new Record();
        findEntity.setIsDel(DeleteState.CREATE.getCode());
        findEntity.setUserId(userId);

        Page<UserRecord> result = awardService.queryForUserPrizeRecordList(Example.of(findEntity), prizeState, pageRequest(page, size, sort));
        return ResponseEntity.ok(Responses.builder()
                .page(result, "/award/user/prizes", request.getParameterMap())
                .data(result.getContent()));
    }

    @RequestMapping(value = "/user/lucks", method = RequestMethod.GET)
    public Object getUserLuckRecords(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort)
    {
        HttpServletRequest request = getHttpServletRequest();
        if (size > 50) {
            size = 50;
        }
        if (size < 0) {
            size = 0;
        }
        if(StringUtils.isEmpty(userId)){
            if(getCurrentUserId() != null) {
                userId = String.valueOf(getCurrentUserId());
            } else {
                throw new IllegalStateException();
            }
        }
        Prize findEntity = new Prize();
        findEntity.setIsDel(DeleteState.CREATE.getCode());
        findEntity.setState(PrizeState.COMPLETE.getState());
        findEntity.setUserId(userId);

        Page<UserRecord> result = awardService.queryForUserLuckRecordList(Example.of(findEntity), pageRequest(page, size, sort));
        return ResponseEntity.ok(Responses.builder()
                .page(result, "/award/user/lucks", request.getParameterMap())
                .data(result.getContent()));
    }

    @RequestMapping(value = "/user/shares", method = RequestMethod.GET)
    public Object getUserShares(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort)
    {
        HttpServletRequest request = getHttpServletRequest();
        if (size > 50) {
            size = 50;
        }
        if (size < 0) {
            size = 0;
        }
        if(StringUtils.isEmpty(userId)){
            if(getCurrentUserId()!=null) {
                userId = String.valueOf(getCurrentUserId());
            } else {
                throw new IllegalStateException();
            }
        }
        Share findEntity = new Share();
        findEntity.setIsDel(DeleteState.CREATE.getCode());
        findEntity.setUserId(userId);

        Page<UserRecord> result = awardService.queryForUserShareList(Example.of(findEntity), pageRequest(page, size, sort));
        return ResponseEntity.ok(Responses.builder()
                .page(result, "/award/user/shares", request.getParameterMap())
                .data(result.getContent()));
    }


    @Override
    protected HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    protected static PageRequest pageRequest(int page, int size, String[] sorts) {
        Sort sort = null;
        if (sorts != null) {
            Pages.SortBuilder builder = Pages.sortBuilder();
            for (String value : sorts) {
                if (value.startsWith("-")) {
                    builder.add(value.substring(1), false);
                } else {
                    builder.add(value, true);
                }
            }
            sort = builder.build();
        }
        return Pages.builder().page(page).size(size).sort(sort).build();
    }
}
