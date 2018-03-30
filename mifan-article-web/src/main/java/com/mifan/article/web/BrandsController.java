package com.mifan.article.web;

import com.mifan.article.domain.Topics;
import com.mifan.article.domain.support.Brands;
import com.mifan.article.service.BrandsService;
import com.mifan.article.service.util.EntityUtils;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/12
 */
@RestController
@RequestMapping("/brands")
public class BrandsController extends RestfulController<Topics> {
	
	@Autowired
	private BrandsService brandsService;

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable String name,
            @RequestParam(required = false) String[] include) {

        List<Topics> list = Services.findAll(
                Topics.class,
                Restrictions.and(
                        Restrictions.eq(Topics.FORUM_ID, Topics.ForumType.BRAND.getIndex()),
                        Restrictions.eq(Topics.TITLE_HASH, EntityUtils.asLong(name)),
                        Restrictions.eq(Topics.TITLE, name),
                        Restrictions.eq(Topics.ENABLED, 1)),
                Fields.builder().add(BaseEntity.ID).build());

        if (list.isEmpty()) {
            return ResponseEntity.ok(Responses.builder().data(null));
        }

        return super.doGet(list.get(0).getId(), include);
    }
    @RequestMapping(value = "/hot", method = RequestMethod.GET)
    public ResponseEntity<Response> hotBrands(){
    	List<Brands> brands = brandsService.hotBrands();
    	return ResponseEntity.ok(Responses.builder().data(brands));
    }
}
