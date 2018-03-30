package com.mifan.article.web.mp;

import java.util.List;
import java.util.Map;

import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mifan.article.domain.MpDownloads;
import com.mifan.article.service.MpDownloadsService;

/**
 * @author ZYW
 *  2018年3月27日 18:07:18
 */
@RestController
@RequestMapping("/mpDownloads")
public class MpDownloadsController extends RestfulController<MpDownloads> {
    
    @Autowired
    private MpDownloadsService mpDownloadsService;
    
    /*@RequestMapping(value = "/relatedTitle",method = RequestMethod.GET)
    public ResponseEntity<Response> related(@RequestParam(required = true) String topicTitle){
        List<MpDownloads> result = mpDownloadsService.relatedTitle(topicTitle);
        return ResponseEntity.ok(Responses.builder().data(result));
    }*/
    
    @RequestMapping(value = "/fast",method = RequestMethod.GET)
    public ResponseEntity<Response> fast(@RequestParam(required = false, name = "assistantsSize", defaultValue = "10") int assistantsSize,
            @RequestParam(required = false, name = "driveSize", defaultValue = "10") int driversSize,
            @RequestParam(required = false, name = "softwareSize", defaultValue = "10") int softwareSize){
        
        Map<String,List<MpDownloads>> map = mpDownloadsService.fastDownload(assistantsSize, driversSize, softwareSize);
        return ResponseEntity.ok(Responses.builder().data(map));
    }
    
    @Override
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {

        return super.doGetPage(page, size, sort, include);
    }
    
    @Override
    protected Criterion criterion(Criterion criterion) {
        return Restrictions.eq(BaseEntity.ENABLED, 1);
    }
}
