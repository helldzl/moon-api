package com.mifan.article.article;

import com.mifan.article.AbstractTests;
import com.mifan.article.domain.*;
import com.mifan.article.service.TopicsService;
import org.junit.Test;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LiuKai on 2017/7/21.
 */
public class TopicServiceImplTest extends AbstractTests {
    @Autowired
    private TopicsService topicsService;

    @Test
    public void findOne() {
        Topics one = Services.queryForObject(Topics.class, 1L, null);
        Map<String, Object> objectMap = TopicServiceImplTest.toMap(one, true);
        System.out.println();

    }

    @Test
    public void TestToMap001() {
        List<Long> listLong = new ArrayList<>();
        listLong.add(1l);
        listLong.add(2l);
        listLong.add(3l);
        List<Topics> listTopics = Services.findAll(Topics.class, listLong);

        Map<String, Object> objectMap = TopicServiceImplTest.toMap(listTopics, true);

        System.out.println();

    }

    @Test
    public void TestToMap002() {
        List<Long> listLong = new ArrayList<>();
        listLong.add(2l);
        listLong.add(3l);
        List<Topics> listTopics = Services.findAll(Topics.class, listLong);
        listTopics.add(Services.queryForObject(Topics.class, 1L, null));
        TestDomain testDomain = new TestDomain();
        testDomain.setId(1l);
        testDomain.setTopicsList(listTopics);
        Map<String, Object> objectMap = TopicServiceImplTest.toMap(testDomain, true);

        System.out.println();

    }

    @Test
    public void TestToMap003() {
        Posts posts = Services.queryForObject(Posts.class, 1l, null);
        Map<String, Object> objectMap = toMap01(posts, true);
        System.out.println();

    }


    @Test
    public void testFindAll() {
        Page<TopicsFetch> page = Services.findAll(TopicsFetch.class,
                Restrictions.and(
                        Restrictions.eq(TopicsFetch.SEED_ID, 11)),

                Pages.builder().page(1).size(100).build(), false);
        //Object[] topicsIds = page.getContent().stream().map(TopicsFetch::getTopicId).collect(Collectors.toSet()).toArray();

        topicsService.findAll(
                Restrictions.and(
                        Restrictions.in(Topics.ID, page.getContent().stream().map(TopicsFetch::getTopicId).collect(Collectors.toSet()).toArray())

                ), Pages.builder().page(1).size(100).build(), false);
    }

    public static Map<String, Object> toMap(Object obj) {
        if (obj == null)
            return Collections.emptyMap();
        if (obj instanceof List) {
            Map<String, Object> map0001 = new HashMap<>();
            for (int i = 0; i < ((List) obj).size(); i++) {
                map0001.put(i + "", ((List) obj).get(i));
            }
        }

        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                String key = descriptor.getName();
                if (!"class".equals(key)) {
                    Object value = descriptor.getReadMethod().invoke(obj);
                    if (value != null && !"".equals(value))
                        map.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    //是否嵌套转成map
    public static Map<String, Object> toMap01(Object obj, Boolean bool) {
        if (obj == null)
            return Collections.emptyMap();
        if (bool == false) {
            return BeanUtils.toMap(obj);
        }
        Map<String, Object> map = BeanUtils.toMap(obj);
        for (String key : map.keySet()) {
            if (map.get(key) instanceof BaseEntity) {
                map.put(key, BeanUtils.toMap(map.get(key)));
            }
        }
        return map;
    }


    //是否嵌套转成map
    public static Map<String, Object> toMap(Object obj, Boolean bool) {
        if (obj == null)
            return Collections.emptyMap();
        if (bool == false) {
            return BeanUtils.toMap(obj);
        }
        Map<String, Object> map = BeanUtils.toMap(obj);
        objectToMap(map);
        return map;
    }

    public static Map<String, Object> objectToMap(Map<String, Object> map) {
        for (String key : map.keySet()) {
            Object keyObj = map.get(key);
            if (keyObj == null || keyObj == "") {
                continue;
            } else if (keyObj instanceof String) {
                continue;
            } else if (keyObj instanceof Number) {
                continue;
            } else if (keyObj instanceof Date) {
                continue;
            } else if (keyObj instanceof Boolean) {
                continue;
            } else if (keyObj instanceof Enum) {
                map.put(key, ((Enum) keyObj).name());
                continue;
            } else if (keyObj instanceof List) {
                // TODO: 2017/8/3  等会需要再进行优化一下才行
                if (((List) keyObj).size() > 0) {
                    if (((List) keyObj).get(0) instanceof String || ((List) keyObj).get(0) instanceof Number || ((List) keyObj).get(0) instanceof Date || ((List) keyObj).get(0) instanceof Boolean) {
                        Map map001 = new HashMap<>();
                        for (int i = 0; i < ((List) keyObj).size(); i++) {
                            map001.put(i, ((List) keyObj).get(i));
                        }
                        map.put(key, map001);
                    }
                }
            } else {
                map.put(key, TopicServiceImplTest.objectToMap(BeanUtils.toMap(keyObj)));
            }
        }

        return map;
    }

    @Test
    public void saveTest() {
        Topics topics = new Topics();
        TopicsFetch topicsFetch = new TopicsFetch();
        topicsFetch.setOrigin("d88888888888");
        topicsFetch.setSeedId(11222L);
        topics.setFrom(topicsFetch);
        topics.setTitle("wwwww刘凯测试11221");
        topics.setForumId(3l);
        List<Attachments> attachmentsList = new ArrayList<>();
        Attachments attachment1 = new Attachments();
        attachment1.setFilename("wwww111wwww1.com1");
        attachment1.setTitle("dfdfdfwwwwdfdfdwwww1f");
        attachment1.setExtension("jwpg");
        attachment1.setDescription("dfdwfdfdf11");
        attachment1.setGroupId(4);
        attachment1.setId(1866685L);
        attachment1.setMime("imagwe/jpeg1");
        attachmentsList.add(attachment1);
        topics.setImages(attachmentsList);
        TopicsDocument topicsDocument = new TopicsDocument();
        topicsDocument.setAuthor("liukwai1");
        topicsDocument.setPostDate(new Date());
        topicsDocument.setBrand("ddwd1");
        PostsText postsText = new PostsText();
        postsText.setCategory("dddw1");
        postsText.setTitle("wdd1");
        postsText.setTag("dwdd1");
        postsText.setContent("dddddwdd1");
        postsText.setDescription("ddddwddddd1");
        Posts posts = new Posts();
        posts.setPostsText(postsText);
        posts.setTitle("ddwdd");
        posts.setParentId(0L);
        posts.setPriority(1);
        posts.setCreated(new Date());
        posts.setModified(new Date());
        topics.setPost(posts);
        topics.setDocument(topicsDocument);

        topicsService.save(topics);
    }

}
