package com.mifan.article.service.impl;

import com.mifan.article.dao.UserSearchHistoryDao;
import com.mifan.article.domain.SearchKeyword;
import com.mifan.article.domain.UserSearchHistory;
import com.mifan.article.service.UserSearchHistoryService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-01-18
 */
@Service
public class UserSearchHistoryServiceImpl extends AbstractBaseService<UserSearchHistory, UserSearchHistoryDao> implements UserSearchHistoryService {
    
    @Override
    public List<String> findHistoryBySsid(String ssid,long categoryId,long forumId){
        if(ssid == null) {
            throw new IllegalStateException("参数错误，ssid不能为空！");
        }
        UserSearchHistory find = new UserSearchHistory();
        find.setSsid(ssid);
        find.setForumId(forumId);
        find.setCategoryId(categoryId);
        find.setEnabled(1);
        UserSearchHistory one = super.findOne(find,Fields.builder().add(UserSearchHistory.ID).build());
        SearchKeyword skw = new SearchKeyword();
        skw.setSearchId(one.getId());
        skw.setEnabled(1);
        Page<SearchKeyword> pages = Services.findAll(SearchKeyword.class, 
                skw,
                Pages.builder().page(1).size(100).sort(Pages.sortBuilder().add(BaseEntity.MODIFIED, false).build()).build(),
                Fields.builder().add(SearchKeyword.KEYWORD).build());
        List<String> result;
        if(pages.hasContent()) {
            result = new ArrayList<String>();
            for(SearchKeyword word : pages.getContent()) {
                String str = word.getKeyword();
                result.add(str);
            }
        }else {
            result = Collections.emptyList();
        }
        return result;
    }
    @Override
    public int save (UserSearchHistory entity) {
        super.saveOrUpdate(entity);
        SearchKeyword skw = new SearchKeyword();
        skw.setSearchId(entity.getId());
        skw.setKeyword(entity.getKeyword().trim());
        SearchKeyword one = Services.findOne(SearchKeyword.class, skw);
        if(one != null){
            skw.setId(one.getId());
            skw.setNum(one.getNum() + 1);
            skw.setEnabled(1);//删除后再次保存时，需要设置enabled
            Services.update(SearchKeyword.class, skw);
        }else {
            Services.save(SearchKeyword.class, skw);
        }
        return 1;
    }
    @Override
    public int deleteKeyword(String ssid,String keyword) {
        if(ssid == null) {
            throw new IllegalStateException("参数错误，ssid不能为空！");
        }
        UserSearchHistory one = new UserSearchHistory();
        one.setSsid(ssid);
        one.setEnabled(1);
        List<UserSearchHistory> list = this.findAll(one);
        if(CollectionUtils.isEmpty(list)) {
            return 0;
        }
        List<SearchKeyword> delete = new ArrayList<SearchKeyword>();
        SearchKeyword find = new SearchKeyword();
        find.setKeyword(keyword);
        find.setEnabled(1);
        for(UserSearchHistory ush : list) {
            find.setSearchId(ush.getId());
            List<SearchKeyword> keywords = Services.findAll(SearchKeyword.class, find,Fields.builder().add(BaseEntity.ID).build());
            if(CollectionUtils.isNotEmpty(keywords)) {
                delete.addAll(keywords);
            }
        }
        int n = 0;
        for(SearchKeyword skw : delete) {
            skw.setEnabled(0);
            n += Services.update(SearchKeyword.class, skw);
        }
        return n;
    }
    
}
