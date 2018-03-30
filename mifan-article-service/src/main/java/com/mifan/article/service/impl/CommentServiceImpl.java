package com.mifan.article.service.impl;

import com.mifan.article.domain.Topics;
import com.mifan.article.domain.TopicsClustering;
import com.mifan.article.domain.support.Comment;
import com.mifan.article.feign.support.CommentClient;
import com.mifan.article.service.CommentService;
import com.mifan.article.service.TopicsClusteringService;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.feign.Feigns;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * @author ZYW
 * 2018-02-01
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private TopicsClusteringService topicsClusteringService;

    @Autowired
    private CommentClient commentClient;

    @Override
    public ResponseEntity<Data<Void>> remove(Long id) {
        Feigns.asRemote();
        ResponseEntity<Data<Void>> response = commentClient.doDelete(id);
        if (response.getStatusCode().is2xxSuccessful()) {
            Comment comment = this.findOne(id);
            if (comment != null) {
                Map<String, Object> map = this.topicInfo(comment.getThemeId());
                if (map != null && map.containsKey("theme_comment_count")) {
                    Integer replayCount = (Integer) map.get("theme_comment_count");
                    if(replayCount >= 0) {
                        Topics topic = new Topics(comment.getThemeId());
                        topic.setReplies(replayCount);
                        Services.update(Topics.class, topic);
                    }
                }
            }
        }
        return response;
    }

    @Override
    public ResponseEntity<Data<Map<String, Object>>> save(Data<Comment> data) {
        data.getData().setConfId(1L);
        Feigns.asRemote();
        ResponseEntity<Data<Map<String, Object>>> response = commentClient.doPost(data);
        if (response.getStatusCode().is2xxSuccessful()) {
            Comment comment = data.getData();
            Data<Map<String, Object>> body = response.getBody();
            if (body != null && body.getData() != null && body.getData().containsKey("replayCount")) {
                Topics topic = new Topics(comment.getThemeId());
                topic.setReplies((Integer) body.getData().get("replayCount"));
                Services.update(Topics.class, topic);
            }
        }
        return response;
    }

    @Override
    public ResponseEntity<Data<List<Comment>>> doGetPage(Long themeId, Long topId, int page, int size) {
        Long[] themeIds = getThemeIds(themeId);
        ResponseEntity<Data<List<Comment>>> response = commentClient.doGetPage(Feigns.params()
                .page(p -> p.page(page).size(size))
                .filter("confId", 1)
                .filter("themeIds", Arrays.asList(themeIds))
                .filter("topId", topId)
                .build());
        return response;
    }

    @Override
    public ResponseEntity<Data<List<Comment>>> findHotComments(Long themeId) {
        Long[] themeIds = getThemeIds(themeId);
        ResponseEntity<Data<List<Comment>>> response = commentClient.hotComments(Feigns.params()
                .custom("confId", 1)
                .custom("themeIds", Arrays.asList(themeIds))
                .build());
        return response;
    }

    private Comment findOne(Long id) {
        ResponseEntity<Data<Comment>> response = commentClient.doGet(id);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getData();
        } else {
            return null;
        }
    }

    private Map<String, Object> topicInfo(Long topicId) {
        ResponseEntity<Data<Map<String, Object>>> response = commentClient.topicInfo(Feigns.params()
                .custom("themeId", topicId)
                .custom("confId", 1)
                .build());
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getData();
        } else {
            return null;
        }
    }

    private Long[] getThemeIds(Long themeId) {
        Long[] themeIds = null;//topic有可能重复合并，所以在这里合并的topic的评论也要合并的查出来
        //targetId=0是否存在，是区分该主题是否被人工干预过的标志
        TopicsClustering clus = new TopicsClustering();
        clus.setTopicId(themeId);
        clus.setType(TopicsClustering.ClusteringType.PEOPLE.getIndex());
        clus.setTargetId(0L);
        Criterion criterion = Restrictions.eq(TopicsClustering.TOPIC_ID, themeId);
        if (topicsClusteringService.exists(clus)) {//如果被干预过，则机器的不再算数
            criterion = Restrictions.and(Restrictions.eq(TopicsClustering.TYPE, TopicsClustering.ClusteringType.PEOPLE.getIndex()),
                    Restrictions.eq(TopicsClustering.ENABLED, 1),
                    Restrictions.ne(TopicsClustering.TARGET_ID, 0),
                    criterion);
        }
        List<TopicsClustering> topicsClusterings = topicsClusteringService.findAll(criterion);

        themeIds = new Long[topicsClusterings.size() + 1];
        for (int i = 0; i < topicsClusterings.size(); i++) {
            themeIds[i] = topicsClusterings.get(i).getTargetId();
        }
        int last = topicsClusterings.size();
        themeIds[last] = themeId;
        return themeIds;
    }
}
