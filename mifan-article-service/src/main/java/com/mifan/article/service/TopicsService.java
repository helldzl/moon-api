package com.mifan.article.service;

import com.mifan.article.domain.Attachments;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.SpiderStatistics;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.support.TopicsReq;
import org.elasticsearch.index.query.QueryBuilder;
import org.moonframework.elasticsearch.SearchResult;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.service.BaseService;
import org.moonframework.web.jsonapi.Response;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public interface TopicsService extends BaseService<Topics> {

    Topics queryForObject(Long id, Iterable<? extends Field> fields, boolean clustering, boolean authentication);

    Response convert(int page, int size, SearchResult<Map<String, Object>> result, Map<String, String[]> params);

    String image(QueryBuilder queryBuilder);

    /**
     * 
     * @param page
     * @param size
     * @param fromDays
     * @param sort
     * @param request
     * @return
     * ZYW 2018年3月23日 17:47:01
     */
    List<Map<String, Object>> hotSearch(int page,int size,Integer fromDays,Set<String> excludes,String[] sort,HttpServletRequest request);
    
    /**
     * 
     * @param imageSize
     * @param forumIds
     * @param size
     * @param brands
     * @param from
     * @param sort
     * @return
     * ZYW 2018年3月8日 15:09:19
     */
    List<Map<String, Object>> hotSearch(Integer imageSize,long[] forumIds,int size,String[] brands,String from,String[] sort);
    
    /**
     * 根据id获取详情，后台专用
     *
     * @param id
     * @return
     */
    Topics findTopics(Long id);

    /**
     * <p>根据主题ID查找POST, 并根据映射更新categoryId</p>
     *
     * @param topic topic
     * @return posts
     */
    Posts findPost(Topics topic);

    /**
     * topic ID -> Posts
     *
     * @param topicIds topicIds
     * @return map
     */
    Map<Long, Posts> findPost(Set<Long> topicIds);

    /**
     * <p>根据主题ID, 查找附件信息, 并按附件类型进行分组</p>
     *
     * @param topicId topicId
     * @return result map
     */
    Map<Attachments.ContentType, List<Attachments>> findAttachment(Long topicId);

    /**
     * topic ID -> MAP
     *
     * @param topicIds topicIds
     * @return map
     */
    Map<Long, Map<Attachments.ContentType, List<Attachments>>> findAttachment(Set<Long> topicIds);

    /**
     * <p>产品比较</p>
     *
     * @param ids doc id
     * @return result
     */
    Response compare(Set<String> ids);

    /**
     * 根据条件查询列表
     *
     * @param req
     * @return
     */
    Page<Topics> findAll(TopicsReq req);

    /**
     * 搜索热词
     * @param product 是否是产品中心1：是，0：否
     * @return
     */
    List<Topics> findHotKeywords(int product);

    Page<SpiderStatistics> findSpiderStatisticsByTime(String priortime, String latertime, int page, int size);
}
