/**
 * 满足琐碎杂乱的需求
 * 
 */
package com.mifan.article.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mifan.article.domain.Topics;
import com.mifan.article.service.TopicsService;

/**
 * @author ZYW
 * 2018年3月9日 14:37:15
 */
@RestController
@RequestMapping("/fewTopics")
public class FewTopicsController extends RestfulController<Topics> {
	@Autowired
    private TopicsService topicsService;
	
	
	@RequestMapping(value = "/hotByBrandsForReviews", method = RequestMethod.GET)//可用于2.3.0需求：品牌经典评测
    public HttpEntity<Response> hotByBrandsForReviews(@RequestParam(required = false, name = "forum") long[] forumIds,
    		@RequestParam(required = false, name = "size", defaultValue = "10") int size,
    		@RequestParam(required = true, name = "brands") String[] brands,
    		HttpServletRequest request ){
        LocalDate today = LocalDate.now();
        String from = today.minusMonths(3).toString();
        List<Map<String, Object>> list = topicsService.hotSearch(4,forumIds, size, brands, from, new String[] {"-items.reviews"});
        
        size = size - list.size();
    	if(size > 0) {
    		List<Map<String, Object>> more = topicsService.hotSearch(4,forumIds,size,brands,null,new String[]{"-created"});
    		list.addAll(more);
    	}
        
    	return ResponseEntity.ok(Responses.builder().data(list));
    }
	@RequestMapping(value = "/newsForBrands", method = RequestMethod.GET)//用于满足2.3.0需求：品牌详情页的四个相关咨询
    public HttpEntity<Response> newsForBrands(@RequestParam(required = true, name = "brands") String[] brands,
    		HttpServletRequest request ){
		List<Map<String, Object>> news = topicsService.hotSearch(4,new long[] {3},2,brands,null,new String[]{"-created"});
		
		List<Map<String, Object>> evaluations = topicsService.hotSearch(4,new long[] {4},2,brands,null,new String[]{"-created"});
		int newsSize = news.size();
		int evaluationsSize = evaluations.size();
		if(newsSize < 2 && evaluationsSize ==2) {
			evaluations = topicsService.hotSearch(4,new long[] {4},4-newsSize,brands,null,new String[]{"-created"});
		}else if(newsSize == 2 && evaluationsSize < 2) {
			news = topicsService.hotSearch(4,new long[] {3},4-evaluationsSize,brands,null,new String[]{"-created"});
		}
		news.addAll(evaluations);
    	return ResponseEntity.ok(Responses.builder().data(news));
    }
}
