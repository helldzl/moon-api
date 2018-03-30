package com.mifan.article.service.impl;


import com.mifan.article.dao.TranslateTaskDao;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.PostsText;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.TranslateTask;
import com.mifan.article.domain.support.TranslateTaskAudit;
import com.mifan.article.domain.support.UserAccounts;
import com.mifan.article.domain.support.Users;
import com.mifan.article.feign.user.UserAccountsClient;
import com.mifan.article.feign.user.UsersClient;
import com.mifan.article.service.TranslateTaskService;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.*;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.feign.Feigns;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-11-02
 */
@Service
public class TranslateTaskServiceImpl extends AbstractBaseService<TranslateTask, TranslateTaskDao> implements TranslateTaskService {

    @Autowired
    private UsersClient usersClient;

    @Autowired
    private UserAccountsClient userAccountsClient;

    private static Pattern COUNT_WORD_PATTERN = Pattern.compile("[a-zA-Z0-9\\.]{1,}");

    /**
     * <p>REMOTE CALL</p>
     *
     * @param account user account
     * @return account
     */
    private UserAccounts account(String account) {
        Feigns.asRemote();
        ResponseEntity<Data<List<UserAccounts>>> response = userAccountsClient.doGetPage(Feigns.params()
                .filter(UserAccounts.ACCOUNT, account)
                .build());

        List<UserAccounts> data = response.getBody().getData();
        if (data.isEmpty()) {
            throw new IllegalStateException("翻译人员帐号不存在!");
        } else {
            return data.get(0);
        }
    }

    /**
     * <p>REMOTE CALL</p>
     *
     * @param collection collection of user IDs
     * @return map
     */
    private Map<Long, String> users(Collection<? extends Number> collection) {
        Feigns.asRemote();
        ResponseEntity<Data<List<Users>>> data = usersClient.doGetPage(Feigns.params()
                .filter(Users.ID, collection)
                .fields("users", f -> f.add(Users.ID).add(Users.USERNAME))
                .build());
        return data.getBody().getData().stream().collect(Collectors.toMap(Users::getId, Users::getUsername));
    }

    @Override
    public int update(TranslateTask entity) {
        if (StringUtils.isNotBlank(entity.getTranslatorAccount())) {
            // Users user = usersService.findUserByUsername(entity.getTranslatorAccount());
            UserAccounts user = account(entity.getTranslatorAccount());
            if (user == null) {
                throw new IllegalStateException("翻译人员帐号不存在！");
            }
            entity.setTranslator(user.getUserId());
        }
        return super.update(entity);
    }

    @Override
    public <S extends TranslateTask> int save(S entity) {
        int n = Services.update(Topics.class, new Topics(entity.getTopicId()));
        if (n == 0) {
            throw new IllegalStateException("翻译主题不存在！");
        }
        TranslateTask find = new TranslateTask();
        find.setTopicId(entity.getTopicId());
        if (this.exists(find)) {
            throw new IllegalStateException("该主题已经在翻译任务中了！");
        }
        entity.setState(1);
        if (StringUtils.isNotBlank(entity.getTranslatorAccount())) {
            // Users user = usersService.findUserByUsername(entity.getTranslatorAccount());
            UserAccounts user = account(entity.getTranslatorAccount());
            if (user == null) {
                throw new IllegalStateException("翻译人员帐号不存在！");
            }
            entity.setTranslator(user.getUserId());
            entity.setState(2);
        }
        Posts post = entity.getPost();
        if (post != null && post.getId() != null) {
            setter(post);
            Services.update(Posts.class, post);
        }
        if (entity.getWordsNum() == null) {
            Posts one = new Posts();
            one.setTopicId(entity.getTopicId());
            one.setParentId(0L);
            one.setEnabled(1);
            one = Services.findOne(Posts.class, one);
            int wordsNum = countWord(getWords(one));
            entity.setWordsNum(wordsNum);
        }

        return super.save(entity);
    }

    private String getWords(Posts post) {
        StringBuilder sb = new StringBuilder("");
        if (post.getTitle() != null) {
            sb.append(post.getTitle());
        }
        if (post.getDescription() != null) {
            sb.append(" ").append(post.getDescription());
        }
        if (post.getContent() != null) {
            sb.append(" ").append(post.getContent());
        }
        List<Map<String, String>> features = post.getFeatures();
        if (!Collections.isEmpty(features)) {
            StringBuilder featuresStr = new StringBuilder("");
            for (Map<String, String> map : features) {
                featuresStr.append(map.get("_name")).append(" ").append(map.get("_value")).append(" ");
            }
            sb.append(" ").append(featuresStr);
        }
        return sb.toString();
    }

    private int countWord(String str) {
        //定义script的正则表达式
        String regScript = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义style的正则表达式
        String regStyle = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        // 定义HTML标签的正则表达式
        String regHtml = "<[^>]+>";
        // 过滤script标签
        Pattern pScript = Pattern.compile(regScript, Pattern.CASE_INSENSITIVE);
        Matcher mScript = pScript.matcher(str);
        str = mScript.replaceAll("");
        // 过滤style标签
        Pattern pStyle = Pattern.compile(regStyle, Pattern.CASE_INSENSITIVE);
        Matcher mStyle = pStyle.matcher(str);
        str = mStyle.replaceAll("");
        // 过滤html标签
        Pattern pHtml = Pattern.compile(regHtml, Pattern.CASE_INSENSITIVE);
        Matcher mHtml = pHtml.matcher(str);
        str = mHtml.replaceAll("");
        //很多小数比如6.5，如果不加上\.则会匹配为两个字数
        Matcher matcher = COUNT_WORD_PATTERN.matcher(str);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /*public static void main(String[] args) {
        String str = "“脏”64位混音引擎，每个音轨16个插件，automation（自动操作）曲线等新加入的功能4.4 。. ,";
        System.out.println(countWordCn(str));
    }*/
    @Override
    public TranslateTask queryForObject(Long id, Iterable<? extends Field> fields) {
        TranslateTask task = this.findOne(id);
        Topics topics = Services.findOne(Topics.class, task.getTopicId(), Fields.builder().add(Topics.ID).add(Topics.FORUM_ID).add(Topics.TITLE).build());
        setTitle(task, topics);

        Set<Long> userIds = new HashSet<Long>();
        userIds.add(task.getAuditor());
        userIds.add(task.getTranslator());
        userIds.add(task.getCreator());
        userIds.add(task.getModifier());
//        Map<Long, String> userMap = Services.findAll(Users.class, userIds,
//                Fields.builder().add(Users.ID).add(Users.USERNAME).build()).
//                stream().collect(Collectors.toMap(BaseEntity::getId, user -> user.getUsername()));

        Map<Long, String> userMap = users(userIds);

        Include include = Restrictions.get(Include.class);
        if (include != null && include.contains(TranslateTask.ROLE_TRANSLATOR)) {
            task.setTranslatorAccount(userMap.get(task.getTranslator()));
        } else if (include != null && include.contains(TranslateTask.ROLE_AUDITOR)) {
            task.setTranslatorAccount(userMap.get(task.getTranslator()));
            task.setAuditorAccount(userMap.get(task.getAuditor()));
        } else {
            task.setTranslatorAccount(userMap.get(task.getTranslator()));
            task.setModifierAccount(userMap.get(task.getModifier()));
            task.setCreatorAccount(userMap.get(task.getCreator()));
            task.setAuditorAccount(userMap.get(task.getAuditor()));
        }

        Posts parent = new Posts();
        parent.setTopicId(task.getTopicId());
        parent.setParentId(0L);
        parent = Services.findOne(Posts.class, parent);
        if (parent == null) {
            return null;
        } else {
            String content = parent.getContent();
            if (content != null) {
                String[] contentSegment = content.split("(<img class='content_segment' style='display:none'/>)");
                parent.setContentSegment(contentSegment);
            }
        }
        Posts posts = null;
        if (task.getPostId() != 0) {
            posts = Services.findOne(Posts.class, task.getPostId());
            String content = posts.getContent();
            if (content != null) {
                String[] contentSegment = content.split("(<img class='content_segment' style='display:none'/>)");
                posts.setContentSegment(contentSegment);
            }
        } else {
            posts = new Posts();
        }
        posts.setParent(parent);
        task.setPost(posts);
        return task;
    }

    @Override
    public int updateOfAuditor(TranslateTaskAudit task) {//1开始审核，2暂存、提交审核结果
        TranslateTask entity = BeanUtils.copyProperties(task, TranslateTask.class);
        entity.setModifier(task.getAuditor());
        if (entity.getState() == 6 && entity.getWordsNumCn() == null && task.getPosts() != null) {
            int wordsNumCn = countWordCn(getWords(task.getPosts()));
            entity.setWordsNumCn(wordsNumCn);
        }
        int n = Services.update(TranslateTask.class, entity, Restrictions.and(Restrictions.in(TranslateTask.STATE, new Integer[]{3, 4}),
                Restrictions.in(TranslateTask.AUDITOR, new Long[]{entity.getAuditor(), 0L})));
        if (n == 0) {
            throw new IllegalStateException("系统繁忙,请稍后再提交");
        }
        TranslateTask one = this.findOne(entity.getId());
        Posts post = task.getPosts();
        if (entity.getState() != 4) {//修改bug，当通过审核时，post的enabled应该设置为1
            if (post == null) {
                throw new IllegalStateException("系统异常，请刷新重试！");
            }
            if (entity.getState() == 6) {
                post.setEnabled(1);
            }
        }
        if (post != null) {
            if (one.getPostId() == null) {
                throw new IllegalStateException("系统异常，请联系管理员！");
            }
            post.setId(one.getPostId());
            this.setter(post);
            post.setModifier(task.getAuditor());
            n += Services.update(Posts.class, post);
        }
        return n;
    }

    private int countWordCn(String str) {//统计汉字个数
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(str.charAt(i));
            if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
                    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                    || ub == Character.UnicodeBlock.VERTICAL_FORMS) {
                count++;
            }
        }
        count += countWord(str);
        return count;
    }

    @Override
    public Page<TranslateTask> doGetPageOfAuditor(TranslateTask entity, int page, int size) {
        entity.setEnabled(1);
        if (entity.getState() == 3) {
            entity.setAuditor(null);
        }
        PageRequest pageRequest = Pages.builder().page(page).size(size).sort(Pages.sortBuilder().add(TranslateTask.MODIFIED, false).build()).build();
        Page<TranslateTask> pages = this.findAll(entity, pageRequest,
                Fields.builder().add(TranslateTask.ID).add(TranslateTask.TOPIC_ID)
                        .add(TranslateTask.STATE).add(TranslateTask.TRANSLATOR).add(TranslateTask.WORDS_NUM).add(TranslateTask.WORDS_NUM_CN).build());
        List<TranslateTask> tasks = pages.getContent();
        if (tasks.size() > 0) {
            Set<Long> topicIds = new HashSet<Long>();
            Set<Long> userIds = new HashSet<Long>();
            tasks.forEach(t -> {
                topicIds.add(t.getTopicId());
                userIds.add(t.getTranslator());
            });
            List<Topics> topics = Services.findAll(Topics.class, topicIds, Fields.builder().add(Topics.ID).add(Topics.FORUM_ID).add(Topics.TITLE).build());
            Map<Long, Topics> topicMap = topics.stream().collect(Collectors.toMap(BaseEntity::getId, topic -> topic));
            tasks.forEach(t -> setTitle(t, topicMap.get(t.getTopicId())));

//            Map<Long, String> userMap = Services.findAll(Users.class, userIds,
//                    Fields.builder().add(Users.ID).add(Users.USERNAME).build()).
//                    stream().collect(Collectors.toMap(BaseEntity::getId, user -> user.getUsername()));

            Map<Long, String> userMap = users(userIds);
            tasks.forEach(t -> {
                t.setTranslatorAccount(userMap.get(t.getTranslator()));
            });
        }
        return pages;
    }

    @Override
    public Page<TranslateTask> doGetPageOfTranslator(TranslateTask entity, int page, int size) {
        entity.setEnabled(1);
        if (entity.getState() == 1) {
            entity.setTranslator(null);
        }
        PageRequest pageRequest = Pages.builder().page(page).size(size).sort(Pages.sortBuilder().add(TranslateTask.MODIFIED, false).build()).build();
        Page<TranslateTask> pages = this.findAll(entity, pageRequest,
                Fields.builder()
                        .add(TranslateTask.ID)
                        .add(TranslateTask.TOPIC_ID)
                        .add(TranslateTask.STATE)
                        .add(TranslateTask.TRANSLATOR)
                        .add(TranslateTask.WORDS_NUM)
                        .add(TranslateTask.WORDS_NUM_CN)
                        .add(BaseEntity.CREATED)
                        .add(BaseEntity.MODIFIED)
                        .build());
        List<TranslateTask> tasks = pages.getContent();
        if (tasks.size() > 0) {
            Set<Long> topicIds = new HashSet<Long>();
            tasks.forEach(t -> topicIds.add(t.getTopicId()));
            List<Topics> topics = Services.findAll(Topics.class, topicIds);
            Map<Long, Topics> topicMap = topics.stream().collect(Collectors.toMap(BaseEntity::getId, topic -> topic));
            tasks.forEach(t -> setTitle(t, topicMap.get(t.getTopicId())));
        }
        return pages;
    }

    @Override
    public Page<TranslateTask> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Page<TranslateTask> pages = super.findAll(criterion, pageable, fields);
        if (pages.hasContent()) {
            List<TranslateTask> list = pages.getContent();
            Set<Long> topicIds = new HashSet<Long>();
            Set<Long> userIds = new HashSet<Long>();
            list.forEach(t -> {
                topicIds.add(t.getTopicId());
                userIds.add(t.getAuditor());
                userIds.add(t.getTranslator());
                userIds.add(t.getModifier());
                userIds.add(t.getCreator());
            });
            Map<Long, Topics> topicMap = Services.findAll(
                    Topics.class, topicIds).stream().collect(Collectors.toMap(BaseEntity::getId, topic -> topic));
            list.forEach(t -> setTitle(t, topicMap.get(t.getTopicId())));

//            Map<Long, String> userMap = Services.findAll(Users.class, userIds,
//                    Fields.builder().add(Users.ID).add(Users.USERNAME).build()).
//                    stream().collect(Collectors.toMap(BaseEntity::getId, user -> user.getUsername()));

            Map<Long, String> userMap = users(userIds);
            list.forEach(t -> {
                t.setTranslatorAccount(userMap.get(t.getTranslator()));
                t.setAuditorAccount(userMap.get(t.getAuditor()));
                t.setModifierAccount(userMap.get(t.getModifier()));
                t.setCreatorAccount(userMap.get(t.getCreator()));
            });

        }
        return pages;
    }

    private void setTitle(TranslateTask task, Topics topics) {
        task.setTitle(topics.getTitle());
        task.setTopicType(Topics.ForumType.getName(topics.getForumId()));
    }

    @Override
    public int updateOfTranslator(TranslateTask entity) {//场景：1，领取任务2，第一次添加翻译3，修改翻译 4，审核失败后继续翻译
        if (entity.getState() == 3 && entity.getPost() == null) {
            throw new IllegalStateException("请填写翻译内容！");
        }
        entity.setTranslator(entity.getModifier());
        TranslateTask one = this.findOne(entity.getId());
        int n = Services.update(TranslateTask.class, entity, Restrictions.and(Restrictions.in(TranslateTask.STATE, new Integer[]{1, 2, 5}),
                Restrictions.in(TranslateTask.TRANSLATOR, new Long[]{entity.getTranslator(), 0L})));
        if (n == 0) {
            throw new IllegalStateException("系统繁忙,请稍后再提交");
        }
        Posts post = entity.getPost();
        if (post != null) {
            if (one.getState() == 1 || one.getState() == 5) {//待领取的任务不能添加翻译
                throw new UnauthorizedException("您暂无权限，请先领取任务！");
            }
            this.setter(post);
            if (one.getPostId() == 0L) {
                Posts find = new Posts();
                find.setTopicId(one.getTopicId());
                find.setParentId(0L);
                Posts parent = Services.findOne(Posts.class, find);
                if (parent == null) {
                    throw new IllegalStateException("系统繁忙,请稍后再提交");
                }
                post.setTopicId(parent.getTopicId());
                post.setParentId(parent.getId());

                post.setCreator(entity.getTranslator());
                post.setModifier(entity.getTranslator());
                post.setLanguage(1);
                n += Services.save(Posts.class, post);
                entity.setPostId(post.getId());
                n += Services.update(TranslateTask.class, entity);
            } else {
                post.setId(one.getPostId());
                post.setModifier(entity.getTranslator());
                n += Services.update(Posts.class, post);
            }
        }
        return n;
    }

    private void setter(Posts posts) {
        PostsText text = new PostsText();
        text.setTitle(posts.getTitle());
        text.setDescription(posts.getDescription());
        text.setContent(posts.getContent());
        if (posts.getContentSegment() != null && posts.getContentSegment().length > 0) {
            String content = StringUtils.join(posts.getContentSegment(), "<img class='content_segment' style='display:none'/>");
            text.setContent(content);
        }
        if (!CollectionUtils.isEmpty(posts.getTags())) {
            StringBuffer tags = new StringBuffer("");
            posts.getTags().forEach(t -> tags.append(t).append(","));
            tags.deleteCharAt(tags.length() - 1);
            text.setTag(tags.toString());
        }
        text.setNoUpdateCategoryAndTagOfNull(1);
        posts.setPostsText(text);
    }
}
