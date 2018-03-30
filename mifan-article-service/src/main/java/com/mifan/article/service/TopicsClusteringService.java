package com.mifan.article.service;

import com.mifan.article.domain.TopicsClustering;

import org.moonframework.model.mybatis.service.BaseService;
import org.springframework.data.domain.Page;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public interface TopicsClusteringService extends BaseService<TopicsClustering> {
    /**
     * 人工增加去重，目前没有审核，只支持管理员操作，返回值为0时说明已经存在，为1时说明操作成功
     * 会同时操作topicId和targetId对换的数据，即一次操作可能会增加两条数据
     * 添加的时候会同时复制topic的所有相关数据（机器学习类型），并将复制的数据修改为人工干预类型进行保存
     * @param topicsClustering
     * @param currentUserId
     * @return
     */
    int repeat(TopicsClustering topicsClustering,Long currentUserId);
    /**
     * 人工删除去重，没有审核，只支持管理员操作，返回值为0删除失败，为1操作成功
     * 会同时操作topicId和targetId对换的数据，即一次删除可能会删除两行数据
     * 这里的删除分两种可能，1，删除机器学习的原始数据，此时其实是增加一条数据覆盖原数据，2，删除人工添加的数据，此时是直接设置enable为0；总结：修改不会对机器学习的原始数据做任何修改，只会覆盖
     * @param id
     * @param currentUserId
     * @return
     */
    int delete(Long id,Long currentUserId);
    
    Page<TopicsClustering> findPage(Long topicId, Long targetId, int page, int size);
    
    
}
