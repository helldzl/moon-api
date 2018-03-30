package com.mifan.article.service;

import com.mifan.article.domain.support.Comment;
import org.moonframework.web.jsonapi.Data;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * @author ZYW
 * 2018-02-01
 */
public interface CommentService {
    /**
     * 列表查询，并附带点赞统计
     *
     * @param themeId
     * @param topId
     * @param page
     * @param size
     * @return
     */
    ResponseEntity<Data<List<Comment>>> doGetPage(Long themeId, Long topId, int page, int size);

    /**
     * 保存评论
     *
     * @param data
     * @return
     */
    ResponseEntity<Data<Map<String, Object>>> save(Data<Comment> data);

    /**
     * 删除评论
     *
     * @param id
     * @return
     */
    ResponseEntity<Data<Void>> remove(Long id);

    /**
     * 获取热门评论
     *
     * @param themeId
     * @return
     */
    ResponseEntity<Data<List<Comment>>> findHotComments(Long themeId);
}
