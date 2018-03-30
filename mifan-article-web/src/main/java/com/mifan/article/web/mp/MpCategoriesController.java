package com.mifan.article.web.mp;

import java.util.List;

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

import com.mifan.article.domain.MpCategories;
import com.mifan.article.service.MpCategoriesService;

/**
 * @author ZYW
 * 2018年3月27日 11:46:22
 */
@RestController
@RequestMapping("/mpCategories")
public class MpCategoriesController extends RestfulController<MpCategories>{

    @Autowired
    private MpCategoriesService mpCategoriesService;
    
    @RequestMapping(value = "/cascade",method = RequestMethod.GET)
    public ResponseEntity<Response> getListByBrand(@RequestParam(required = true, name = "filter[brandId]") Long brandId){
        List<MpCategories> categories = mpCategoriesService.findByBrandId(brandId);
        return ResponseEntity.ok(Responses.builder().data(categories));
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
