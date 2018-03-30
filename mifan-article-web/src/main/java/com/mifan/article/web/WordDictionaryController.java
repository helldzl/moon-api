package com.mifan.article.web;

import com.mifan.article.domain.WordDictionary;
import com.mifan.article.service.util.EntityUtils;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Junction;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.criterion.SimpleExpression;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/1
 */
@RestController
@RequestMapping("/wordDictionary")
public class WordDictionaryController extends RestfulController<WordDictionary> {

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {

        return super.doGet(id, include);
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
        if (criterion instanceof Junction) {
            List<Criterion> list = new ArrayList<>();

            Junction junction = (Junction) criterion;
            for (Criterion c : junction.conditions()) {
                if (c instanceof SimpleExpression) {
                    SimpleExpression expression = (SimpleExpression) c;
                    String propertyName = expression.getPropertyName();
                    if (propertyName.equalsIgnoreCase(WordDictionary.WORD_EN)) {
                        list.add(Restrictions.eq(WordDictionary.WORD_EN_HASH, EntityUtils.asLong((String) expression.getValue())));
                    } else if (propertyName.equalsIgnoreCase(WordDictionary.WORD_CN)) {
                        list.add(Restrictions.eq(WordDictionary.WORD_CN_HASH, EntityUtils.asLong((String) expression.getValue())));
                    }
                }
            }

            list.forEach(junction::add);
        }
        return super.criterion(criterion);
    }
}
