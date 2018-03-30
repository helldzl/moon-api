package com.mifan.article.dao.impl;

import com.mifan.article.dao.TopicsDao;
import com.mifan.article.domain.SpiderStatistics;
import com.mifan.article.domain.Topics;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Repository
public class TopicsDaoImpl extends AbstractBaseDao<Topics> implements TopicsDao {
    
    private static final String[] HOT_BRANDS = {"'AKAI'","'Apogee'","'AKG'","'BLUE'","'Fender'","'DPA Microphones'","'Gibson'","'JBL'","'Martin Guitars'","'YAMAHA'","'ALESIS'","'Allen & Heath'","'API Audio'","'Crown'","'Digigrid'","'EAW'","'Genelec'",
            "'Heritage Audio'","'K&M'","'Lexicon'","'Marshall'","'MOTU'","'Pearl'","'Pioneer DJ'","'Roland'","'Steinberg'","'TC Electronic'","'Waves'","'Bettermaker'","'Avid'","'B&O Play'","'QSC'","'RØDE Microphones'",
            "'Sennheiser'","'EV'","'Chandler Limited'","'Burl Audio'","'Empirical Labs'","'Manley'","'PreSonus'","'Quested'","'Shadow Hills Industries'","'Telefunken'","'T-Rex'","'Trinnov'","'Icon'","'Violet'","'MAGIC-V'"};
    
    @Override
    public List<Topics> findHotKeywordForMicrophonesByReviews(int product,int minReviews,int size,String mydate){
        String findHotKeywordForMicrophonesByReviews = entityClass.getName() + "." + "findHotKeywordForMicrophonesByReviews";
        Map<String, Object> map = new HashMap<>(16);
        map.put("product", product);
        map.put("size", size);
        map.put("mydate", mydate);
        if(product == 1) {
            map.put("brands", HOT_BRANDS);
        }
        List<Topics> result = session.selectList(findHotKeywordForMicrophonesByReviews, map);
        return result;
    }
    
    @Override
    public List<Topics> findHotKeywordForMicrophonesByQuality(int product,int size){
        String findHotKeywordForMicrophonesByQuality = entityClass.getName() + "." + "findHotKeywordForMicrophonesByQuality";
        Map<String, Object> map = new HashMap<>(16);
        map.put("product", product);
        map.put("size", size);
        if(product == 1) {
            map.put("brands", HOT_BRANDS);
        }
        List<Topics> result = session.selectList(findHotKeywordForMicrophonesByQuality, map);
        return result;
    }
    @Override
    public List<Topics> findHotKeywordForOthersByReviews(int product,int minReviews,int size,String mydate){
        String findHotKeywordForOthersByReviews = entityClass.getName() + "." + "findHotKeywordForOthersByReviews";
        Map<String, Object> map = new HashMap<>(16);
        map.put("size", size);
        map.put("product", product);
        map.put("mydate", mydate);
        List<Topics> result = session.selectList(findHotKeywordForOthersByReviews, map);
        return result;
    }
    @Override
    public List<Topics> findHotKeywordForOthersByQuality(int product,int size){
        String findHotKeywordForOthersByQuality = entityClass.getName() + "." + "findHotKeywordForOthersByQuality";
        Map<String, Object> map = new HashMap<>(16);
        map.put("size", size);
        map.put("product", product);
        List<Topics> result = session.selectList(findHotKeywordForOthersByQuality, map);
        return result;
    }
    
    @Override
    public Page<SpiderStatistics> findSpiderStatisticsByTime(String priortime, String latertime, Pageable pageable, Iterable<? extends Field> fields) {
        String findSpiderStatisticsByTime = entityClass.getName() + "." + "findSpiderStatisticsByTime";
        Map<String,Object> map =pageMap(pageable);
        map.put("priortime", priortime);
        map.put("latertime", latertime);
        map.put("fields", fields);
        List<SpiderStatistics> spiderStatisticses = session.selectList(findSpiderStatisticsByTime, map);
        return  new PageImpl<>(spiderStatisticses,pageable,countSpiderStatisticsByTime(priortime,latertime));
    }

    private Long countSpiderStatisticsByTime(String priortime, String latertime){
        String findSpiderStatisticsByTimeCount = entityClass.getName() + "." + "findSpiderStatisticsByTimeCount";
        Map<String,Object> map =new HashMap<>(16);
        map.put("priortime", priortime);
        map.put("latertime", latertime);
        Long count=session.selectOne(findSpiderStatisticsByTimeCount,map);
        return count;
    }
}
