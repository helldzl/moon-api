package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsClusteringDao;
import com.mifan.article.domain.TopicsClustering;
import com.mifan.article.service.TopicsClusteringService;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */

/**
 * 所有的操作都是以topicId为条件的
 * 机器学习的内容不能被操作，所以当人工干预的时候，我们会复制机器学习的内容保存为人工干预的内容，要操作的其实是复制的内容，当实际查询使用这些数据时，如果该topic存在人工干预数据，则优先使用
 * 人工添加的数据中必须有一条tagetId=0的切enabled=1的，因为这条数据用来判断是否被人工干预过
 *
 * @author ZYW
 */
@Service
public class TopicsClusteringServiceImpl extends AbstractBaseService<TopicsClustering, TopicsClusteringDao> implements TopicsClusteringService {

    @Override
    public <S extends TopicsClustering> int save(S entity) {
        return super.saveOrUpdate(entity);
    }

    @Override
    public Page<TopicsClustering> findPage(Long topicId, Long targetId, int page, int size) {
        TopicsClustering topicsClustering = new TopicsClustering();
        topicsClustering.setTopicId(topicId);
        topicsClustering.setType(TopicsClustering.ClusteringType.PEOPLE.getIndex());
        topicsClustering.setTargetId(0L);
        PageRequest pageRequest = Pages.builder().page(page).size(size).sort(Pages.sortBuilder().add(TopicsClustering.MODIFIED, false).build()).build();
        Criterion criterion = Restrictions.eq(TopicsClustering.TOPIC_ID, topicId);
        if (this.exists(topicsClustering)) {
            criterion = Restrictions.and(criterion, Restrictions.eq(TopicsClustering.TYPE, TopicsClustering.ClusteringType.PEOPLE.getIndex()),
                    Restrictions.eq(TopicsClustering.ENABLED, 1),
                    Restrictions.ne(TopicsClustering.TARGET_ID, 0));
        }
        if (targetId != null) {
            criterion = Restrictions.and(criterion, Restrictions.eq(TopicsClustering.TARGET_ID, targetId));
        }
        Page<TopicsClustering> pages = Services.findAll(entityClass, criterion, pageRequest);
        return pages;
    }

    @Override
    public int repeat(TopicsClustering topicsClustering, Long currentUserId) {
        Long topicId = topicsClustering.getTopicId();
        Long targetId = topicsClustering.getTargetId();
        int result = this.add(topicId, targetId, currentUserId);
        if (result == 0) {//如果添加失败，则直接返回，不继续操作对调情况
            return result;
        }
        this.add(targetId, topicId, currentUserId);

        return result;
    }

    private int add(Long topicId, Long targetId, Long currentUserId) {
        TopicsClustering topicsClustering = new TopicsClustering();
        topicsClustering.setTopicId(topicId);
        topicsClustering.setType(TopicsClustering.ClusteringType.PEOPLE.getIndex());
        topicsClustering.setTargetId(0L);
        Date now = new Date();
        if (!this.exists(topicsClustering)) {//当前topic未被人工干预过
            topicsClustering.setType(TopicsClustering.ClusteringType.ROBOT.getIndex());
            topicsClustering.setTargetId(targetId);
            if (this.exists(topicsClustering)) {//如果已经存在则不再继续
                return 0;
            }
            topicsClustering.setTargetId(null);
            List<TopicsClustering> list = this.findAll(topicsClustering);
            for (TopicsClustering clus : list) {//复制操作
                TopicsClustering clusAdd = this.setter(clus.getTopicId(), clus.getTargetId(), currentUserId);
                clusAdd.setType(TopicsClustering.ClusteringType.PEOPLE.getIndex());
                clusAdd.setCreator(currentUserId);
                clusAdd.setCreated(now);
                isTrue(baseDao.save(clusAdd) > 0, "Error.save.TopicsClustering");
            }
            //未被人工干预过的本次操作后需要添加人工干预标志，即targetId=0的数据
            TopicsClustering clusPeople = this.setter(topicId, 0L, currentUserId);
            clusPeople.setCreator(currentUserId);
            clusPeople.setCreated(now);
            isTrue(baseDao.save(clusPeople) > 0, "Error.save.TopicsClustering");
        }
        //不管是否被人工干预过，都需要保存本次用户添加的数据
        TopicsClustering update = this.setter(topicId, targetId, currentUserId);
        TopicsClustering insert = (TopicsClustering) update.clone();
        insert.setCreated(now);
        insert.setCreator(currentUserId);
        isTrue(baseDao.saveOrUpdate(insert, update) > 0, "Error.save.TopicsClustering");
        return 1;
    }

    @Override
    public int delete(Long id, Long currentUserId) {
        TopicsClustering topicsClusteringOne = this.findOne(id);
        if (topicsClusteringOne == null) {
            return 0;
        }
        Long topicId = topicsClusteringOne.getTopicId();
        Long targetId = topicsClusteringOne.getTargetId();
        int resultOne = this.deleteChild(topicsClusteringOne, currentUserId);//删除

        if (resultOne == 0) {//当操作失败时，直接返回结果，不继续操作对调的情况
            return resultOne;
        }

        //删除topic和target对调的数据
        TopicsClustering topicsClusteringTwo = new TopicsClustering();
        topicsClusteringTwo.setTopicId(targetId);
        topicsClusteringTwo.setTargetId(0L);
        topicsClusteringTwo.setType(TopicsClustering.ClusteringType.PEOPLE.getIndex());
        topicsClusteringTwo = this.findOne(topicsClusteringTwo);
        if (topicsClusteringTwo != null) {
            this.deleteChild(topicsClusteringTwo, currentUserId);
        } else {
            topicsClusteringTwo = new TopicsClustering();
            topicsClusteringTwo.setTopicId(targetId);
            topicsClusteringTwo.setTargetId(topicId);
            topicsClusteringTwo.setType(TopicsClustering.ClusteringType.ROBOT.getIndex());
            topicsClusteringTwo = this.findOne(topicsClusteringTwo);
            if (topicsClusteringTwo != null) {
                this.deleteChild(topicsClusteringTwo, currentUserId);
            }
        }

        return resultOne;
    }

    private int deleteChild(TopicsClustering topicsClustering, Long currentUserId) {
        Date now = new Date();
        if (topicsClustering.getType() == TopicsClustering.ClusteringType.ROBOT.getIndex()) {
            TopicsClustering topicsClusteringOne = new TopicsClustering();
            topicsClusteringOne.setTopicId(topicsClustering.getTopicId());
            topicsClusteringOne.setType(TopicsClustering.ClusteringType.PEOPLE.getIndex());
            topicsClusteringOne.setTargetId(0L);
            if (this.exists(topicsClusteringOne)) {//当这个topic拥有人工干预后的数据后，说明当前机器数据已经被覆盖（复制），被覆盖的数据不能再次被操纵
                return 0;
            }
            topicsClusteringOne.setType(TopicsClustering.ClusteringType.ROBOT.getIndex());
            topicsClusteringOne.setTargetId(null);
            List<TopicsClustering> list = this.findAll(topicsClusteringOne);

            for (TopicsClustering clus : list) {//复制操作
                if (!clus.getId().equals(topicsClustering.getId())) {//删除操作，要被删除的这行数据不需要被复制
                    TopicsClustering clusUpdate = this.setter(clus.getTopicId(), clus.getTargetId(), currentUserId);
                    TopicsClustering clusAdd = (TopicsClustering) clusUpdate.clone();
                    clusAdd.setCreated(now);
                    clusAdd.setCreator(currentUserId);
                    isTrue(baseDao.saveOrUpdate(clusAdd, clusUpdate) > 0, "Error.save.TopicsClustering");
                }
            }
            TopicsClustering topicsClusteringPeople = this.setter(topicsClustering.getTopicId(), 0L, currentUserId);
            topicsClusteringPeople.setCreator(currentUserId);
            topicsClusteringPeople.setCreated(now);
            isTrue(baseDao.save(topicsClusteringPeople) > 0, "Error.save.TopicsClustering");//复制操作发生时必须添加一条targetId=0的数据，用来标志该topic的数据已经被复制过
        } else {
            if (topicsClustering.getTargetId() == 0) {//targetId=0的数据是为了覆盖机器学习的，所以不能被删除
                return 0;
            }
            topicsClustering.setEnabled(0);
            topicsClustering.setModified(now);
            topicsClustering.setModifier(currentUserId);
            isTrue(baseDao.update(topicsClustering) > 0, "Error.delete.TopicsClustering");
        }
        return 1;
    }

    private TopicsClustering setter(Long topicId, Long targetId, Long currentUserId) {
        TopicsClustering topicsClustering = new TopicsClustering();
        topicsClustering.setTopicId(topicId);
        topicsClustering.setTargetId(targetId);
        topicsClustering.setType(TopicsClustering.ClusteringType.PEOPLE.getIndex());
        topicsClustering.setScore(1d);
        topicsClustering.setEnabled(1);
        topicsClustering.setModifier(currentUserId);
        topicsClustering.setModified(new Date());
        return topicsClustering;
    }
}
