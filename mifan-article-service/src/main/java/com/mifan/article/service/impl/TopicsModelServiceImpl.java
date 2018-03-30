package com.mifan.article.service.impl;

import com.mifan.article.dao.TopicsModelDao;
import com.mifan.article.domain.*;
import com.mifan.article.domain.support.Multilingual;
import com.mifan.article.service.TopicsModelService;
import com.mifan.article.service.amqp.MessageEntityPublisher;
import io.jsonwebtoken.lang.Collections;
import org.moonframework.amqp.Resource;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static com.mifan.article.domain.TopicsModel.ModelStatus;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-11-08
 */
@Service
public class TopicsModelServiceImpl extends AbstractBaseService<TopicsModel, TopicsModelDao> implements TopicsModelService {

    @Autowired
    private MessageEntityPublisher messageEntityPublisher;

    @Value("${moon.amqp.rabbit.analyzer-exchange}")
    private String analyzerExchange;
    @Value("${moon.amqp.rabbit.analyzer-queue}")
    private String analyzerQueue;

    /**
     * <p>保存一个模型, 此时模型处于NEW状态, 还没有进行训练, 需要人工修改状态到RUNNABLE, 进行模型训练</p>
     *
     * @param entity entity
     * @param <S>    S
     * @return int
     */
    @Override
    public <S extends TopicsModel> int save(S entity) {
        Topics.ForumType forumType = Topics.ForumType.from(entity.getForumId());
        if (forumType == null) {
            throw new IllegalStateException(String.format("非法的Forum ID:%s, 请填写正确的参数", entity.getForumId()));
        }

        // 检查命名空间
        entity.setModelName(entity.getModelName().trim());
        if (super.count(Restrictions.eq(TopicsModel.MODEL_NAME, entity.getModelName())) > 0) {
            throw new IllegalStateException("模型命名空间不唯一, 请换一个模型名称");
        }

        entity.setModelStatus(ModelStatus.NEW);
        return super.save(entity);
    }

    /**
     * <p>删除模型时也要同时删除模型文件</p>
     *
     * @param id ID
     * @return int
     */
    @Override
    public int remove(Long id) {
        TopicsModel update = new TopicsModel(id);
        update.setEnabled(0);
        update.setModelStatus(ModelStatus.DELETE);
        int n = super.update(update, criterion());
        if (n > 0) {
            // 删除hadoop文件
            remove(super.findOne(id, Fields.builder().add(BaseEntity.ID).add(TopicsModel.MODEL_NAME).build()));
        }
        return n;
    }

    /**
     * <p></p>
     * <ol>
     * <li>模型名称一旦创建不能修改</li>
     * <li>模型Forum ID不能修改</li>
     * </ol>
     *
     * @param entity entity
     * @param <S>    S
     * @return int
     */
    @Override
    public <S extends TopicsModel> int update(S entity) {
        ModelStatus modelStatus = entity.getModelStatus() == null ? null : ModelStatus.valueOf(entity.getModelStatus());

        int n;
        if (ModelStatus.DONE == modelStatus) {
            n = super.update(entity);
        } else if (ModelStatus.CLASSIFICATION == modelStatus) {
            n = super.update(entity, Restrictions.and(
                    Restrictions.eq(TopicsModel.ENABLED, 1),
                    Restrictions.eq(TopicsModel.MODEL_STATUS, ModelStatus.DONE.name())));

            if (n == 0) {
                throw new IllegalStateException("训练模型不可用或正在分类中, 不能执行该操作");
            } else {
                classification(super.findOne(entity.getId()));
            }
        } else {
            n = super.update(entity, criterion());
            if (ModelStatus.RUNNABLE == modelStatus) {
                if (n == 0) {
                    throw new IllegalStateException("模型正在训练中, 不能执行该操作");
                } else {
                    train(super.findOne(entity.getId()));
                }
            }
        }

        return n;
    }

    @Override
    public void classification(Long forumId, Long topicId) {
        Page<TopicsModel> page = super.findAll(
                Restrictions.and(
                        Restrictions.eq(TopicsModel.FORUM_ID, forumId),
                        Restrictions.eq(TopicsModel.MODEL_STATUS, ModelStatus.DONE.name())),
                Pages.builder().page(1).size(1).sort(Pages.sortBuilder().add(TopicsModel.PRIORITY, true).build()).build(),
                false);

        if (!page.hasContent()) {
            return;
        }

        TopicsModel model = page.getContent().get(0);

        Criterion criterion = Restrictions.and(
                Restrictions.eq(Topics.FORUM_ID, forumId),
                Restrictions.eq(Topics.ID, topicId));

        // 该接口专门用于查询待分类数据
        Services.doList(Topics.class, criterion, topics -> {
            for (Topics topic : topics) {
                classification(model, topic);
            }
            return Boolean.FALSE;
        });
    }

    // private methods

    private Criterion criterion() {
        return Restrictions.and(
                Restrictions.eq(TopicsModel.ENABLED, 1),
                Restrictions.ne(TopicsModel.MODEL_STATUS, ModelStatus.RUNNABLE.name()),
                Restrictions.ne(TopicsModel.MODEL_STATUS, ModelStatus.CLASSIFICATION.name()),
                Restrictions.ne(TopicsModel.MODEL_STATUS, ModelStatus.WAITING.name()));
    }

    /**
     * <p>删除模型</p>
     *
     * @param model model
     */
    private void remove(TopicsModel model) {
        messageEntityPublisher.send(
                analyzerExchange,
                analyzerQueue,
                model.model(),
                model.getId().toString(),
                TopicsModel.class.getSimpleName(),
                meta -> meta.put(Resource.META_METHOD, Resource.Method.DELETE));
    }

    /**
     * <p>基于forumID的全量模型训练</p>
     * <p>模型训练的格式大致为: Key: /{label}/{id}: Value:{value} </p>
     * <ul>
     * <li>label : 目标变量</li>
     * <li>id : 唯一标识符</li>
     * <li>value : 格式化后的文本</li>
     * </ul>
     *
     * @param model model
     * @see com.mifan.article.service.TopicsService#findAll(Criterion, Pageable, boolean)
     */
    private void train(TopicsModel model) {
        model(model, true, topic -> {
            Posts post = topic.getPost();
            String label;
            String value;
            if (post == null
                    || Collections.isEmpty(post.getCategories())
                    || StringUtils.isEmpty((label = post.getCategories().get(0)))
                    || StringUtils.isEmpty((value = topic.toString()))) {
                return Boolean.FALSE;
            }

            // [训练]
            String id = topic.getId().toString();
            messageEntityPublisher.send(
                    analyzerExchange,
                    analyzerQueue,
                    model.data(id, label, value, topic.getEnabled()),
                    id,
                    Topics.class.getSimpleName(),
                    meta -> meta.put(Resource.META_METHOD, Resource.Method.POST));

            return Boolean.TRUE;
        }, true);
    }

    /**
     * <p>基于forumID的全量分类</p>
     */
    private void classification(TopicsModel model) {
        model(model, false, topic -> classification(model, topic), false);
    }

    private boolean classification(TopicsModel model, Topics topic) {
        Posts post = topic.getPost();
        String value;
        if (post == null || StringUtils.isEmpty((value = topic.toString()))) {
            return Boolean.FALSE;
        }

        // [分类]
        TopicsFetch from = topic.getFrom();
        int source = from == null ? 0 : from.getSeedId().intValue();
        String id = topic.getId().toString();
        messageEntityPublisher.send(
                analyzerExchange,
                analyzerQueue,
                model.data(id, null, value, topic.getEnabled()),
                id,
                Topics.class.getSimpleName(),
                meta -> {
                    meta.put(Resource.META_METHOD, Resource.Method.PATCH);
                    meta.put("target_classification", TopicsClassification.class.getName());
                    meta.put("target_cluster", TopicsClustering.class.getName());
                    // 类别和语言
                    meta.put("categories", post.classification());
                    meta.put("language", Multilingual.Language.from(post.getLanguage()).getName());
                    meta.put("title", topic.getTitle());
                    meta.put("source", source);
                });
        return Boolean.TRUE;
    }

    /**
     * @param model    model 模型
     * @param train    是否是训练样本 true:是, false:否
     * @param function function
     * @param callback callback 是否等待回调, true:设置为WAITING状态等待回调, false:设置为DONE, 此时模型可用
     */
    private void model(TopicsModel model, boolean train, Function<Topics, Boolean> function, boolean callback) {
        CompletableFuture.runAsync(() -> {
            TopicsModel entity = new TopicsModel(model.getId());

            // 查询数据
            Criterion criterion = Restrictions.and(
                    Restrictions.eq(Topics.FORUM_ID, model.getForumId()),
                    Restrictions.eq(Topics.TRAIN_SAMPLE, train ? 1 : 0));

            long count = Services.count(Topics.class, criterion);
            AtomicInteger finished = new AtomicInteger(0);
            AtomicInteger canceled = new AtomicInteger(0);
            AtomicLong time = new AtomicLong(System.currentTimeMillis());
            try {
                // 循环获取[样本]集合
                Services.doList(Topics.class, criterion, page -> {
                    for (Topics topic : page) {
                        if (function.apply(topic).equals(Boolean.TRUE)) {
                            finished.incrementAndGet();
                            long current = System.currentTimeMillis();
                            if (current - time.get() > 8000) {
                                time.set(current);
                                TopicsModel update = new TopicsModel(model.getId());
                                result(update, count, finished, canceled);
                                super.update(update);
                                logger.info(String.format("模型ID: %s : %s", model.getId(), update.getResultText()));
                            }
                        } else {
                            canceled.incrementAndGet();
                        }
                    }
                    return Boolean.TRUE;
                });
                entity.setModelStatus(callback ? ModelStatus.WAITING : ModelStatus.DONE);
                result(entity, count, finished, canceled);
            } catch (Exception e) {
                entity.setModelStatus(ModelStatus.TERMINATED);
                entity.setResultText("Message:" + e.getMessage());
            } finally {
                entity.setResultSamples((int) count);
                super.update(entity);
            }
        });
    }

    private void result(TopicsModel entity, long total, AtomicInteger finished, AtomicInteger canceled) {
        int x = finished.get();
        int y = canceled.get();
        double percentage = ((double) (x + y) / (double) total) * 100D;
        entity.setResultText(String.format("发送消息: %s, 丢弃消息: %s, 进度: (%.2f%%)", x, y, percentage));
    }

}
