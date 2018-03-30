package com.mifan.article.service.impl;

import com.mifan.article.dao.MpDownloadsDao;
import com.mifan.article.domain.MpDownloads;
import com.mifan.article.service.MpDownloadsService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.MatchMode;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
@Service
public class MpDownloadsServiceImpl extends AbstractBaseService<MpDownloads, MpDownloadsDao> implements MpDownloadsService {
    
    @Override
    public List<MpDownloads> relatedTitle(String topicTitle){
        String[] words = topicTitle.trim().split("\\s+");
        Criterion criterion = Restrictions.eq(MpDownloads.ENABLED, 1);
        List<Criterion> list = new ArrayList<>();
        for(String w : words) {
            list.add(Restrictions.like(MpDownloads.TITLE, w, MatchMode.ANYWHERE));
        }
        criterion = Restrictions.and(criterion,Restrictions.or(list));
        List<MpDownloads> result = this.findAll(criterion);
        return result;
    }
    
    @Override
    public Map<String,List<MpDownloads>> fastDownload(int assistantsSize,int driversSize,int softwareSize){
        MpDownloads find = new MpDownloads();
        find.setType(0);
        find.setEnabled(1);
        Page<MpDownloads> assistants = this.findAll(find,
                Pages.builder().page(1).size(assistantsSize).sort(Pages.sortBuilder().add(MpDownloads.DISPLAY_ORDER, true).build()).build());
        find.setType(1);
        Page<MpDownloads> drivers = this.findAll(find,
                Pages.builder().page(1).size(driversSize).sort(Pages.sortBuilder().add(MpDownloads.DISPLAY_ORDER, true).build()).build());
        find.setType(2);
        Page<MpDownloads> software = this.findAll(find,
                Pages.builder().page(1).size(softwareSize).sort(Pages.sortBuilder().add(MpDownloads.DISPLAY_ORDER, true).build()).build());
        Map<String,List<MpDownloads>> map = new LinkedHashMap<>();
        
        map.put("assistants", assistants.getContent());
        map.put("drivers", drivers.getContent());
        map.put("software", software.getContent());
        
        return map;
    }
}
