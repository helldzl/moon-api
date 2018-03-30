package com.mifan.article.api;

import com.mifan.article.AbstractTests;
import com.mifan.article.domain.UsersTopicsHide;
import com.mifan.article.domain.UsersTopicsReport;
import org.junit.Test;
import org.moonframework.model.mybatis.service.Services;

import java.util.HashMap;
import java.util.Map;

//import com.mifan.sku.domain.Comment;
//import com.mifan.sku.domain.Products;
//import com.mifan.sku.domain.Seeds;
//import com.mifan.sku.service.CommentService;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/10
 */
public class ServiceTests extends AbstractTests {

//    @Autowired
//    private UserProfilesDao userProfilesDao;

    //
//    @Autowired
//    private CommentService commentService;
//
//
    @Test
    public void testArticle() {
        UsersTopicsReport insert = new UsersTopicsReport();
        insert.setUserId(1L);
        insert.setTopicId(1L);
        UsersTopicsReport update = new UsersTopicsReport();
        Services.saveOrUpdate(UsersTopicsReport.class, insert, update);

        Map<Long, Map<String, Object>> map = new HashMap<>();
        for (int i = 1; i < 5; i++) {
            Map<String, Object> param = null;
            if (i < 4) {
                param = new HashMap<>();
                param.put("enabled", i * 3);
                param.put("remark", "remark:" + i);

            }
            map.put((long) i, param);
        }
        Services.doToManyPatch(UsersTopicsHide.class, 5L, "topics:hide", map, false);

        System.out.println();

    }
//
//    @Test
//    public void name() throws Exception {
//        Seeds seed = new Seeds();
//        seed.setUpdateRate(100);
//        int update = Services.update(Seeds.class, seed, Restrictions.eq(Seeds.UPDATE_RATE, 10));
//        System.out.println(update);
//        update = Services.update(Seeds.class, seed, Restrictions.eq(Seeds.UPDATE_RATE, 10));
//        System.out.println(update);
//        System.out.println();
//    }
//
//    @Test
//    public void testSaveComment() throws Exception {
//        Comment comment = new Comment();
//        comment.setThemeId(1L);
//        comment.setConfId(1L);
//        comment.setTopId(0L);
//        comment.setContent("hello world444444");
//        comment.setCreator(1031L);
//        comment.setEnabled(1);
//        comment.setIsAnonymous(0);
//        comment.setState(0);
//        comment.setCreated(new Date());
//        int save = commentService.save(comment);
//        System.out.println(save);
//
//    }
//
//    @Test
//    public void testStream() throws Exception {
//        List<Products> list = new ArrayList<>();
//
//        Products p1 = new Products(1L);
//        p1.setFeatureVector("hello");
//        list.add(p1);
//
//        Products p2 = new Products(1L);
//        list.add(p2);
//
//        list.stream().collect(Collectors.toMap(o -> o.getId(), o -> o.getFeatureVector()));
//    }
//
//    @Test
//    public void testUpdateInfo() throws Exception {
////        Products product = new Products(1L);
////        product.setName("Access Virus TI2 Desktop");
////        Services.save(Products.class, product);
//        TimeUnit.SECONDS.sleep(100);
//    }
//
//    @Test
//    public void testService2() throws Exception {
//        PageRequest pageRequest = Pages.builder().page(1).size(100).sort(Pages.sortBuilder().add(BaseEntity.ID, true).build()).build();
//        Long id = 0L;
//        Page<Products> page;
//        while ((page = Services.findAll(Products.class, Restrictions.gt(BaseEntity.ID, id), pageRequest, false)).hasContent()) {
//            for (Products entity : page)
//                System.out.println(String.format("ID:%s, name:%s", entity.getId(), entity.getName()));
//            id = page.getContent().get(page.getNumberOfElements() - 1).getId();
//        }
//    }
//
//    @Test
//    public void testProducts() throws Exception {
//        Criterion between = Restrictions.between(Products.MODIFIED, "2015-01-09 13:40:47", "2017-05-09 13:40:47");
//        Pageable pageable = Pages.builder().page(1).size(50).build();
//        List<Field> fields = Fields.builder().add(Products.ID).build();
//
//        Page<Products> page;
//        // 索引覆盖扫描查询主键, 这里不能查询非索引字段.
//        while ((page = Services.findAll(Products.class, between, pageable, fields, false)).hasContent()) {
//            List<Long> ids = page.getContent().stream().map(BaseEntity::getId).collect(Collectors.toList());
//            // Using primary key with condition
//            Services.findAll(Products.class, Restrictions.and(
//                    Restrictions.in(BaseEntity.ID, ids.toArray()),
//                    Restrictions.eq(BaseEntity.ENABLED, 1))).forEach(product -> {
//                System.out.println(String.format("ID:%s, name:%s", product.getId(), product.getName()));
//            });
//            pageable = pageable.next();
//        }
//
//    }
//
//    @Test
//    public void testUserProfiles() throws Exception {
//        int update = userProfilesDao.update(12L, Pair.builder().add(UserProfiles.USER_KARMA, 1).build(), Collections.emptyList());
//        System.out.println(update);
//    }
//
//    @Test
//    public void testAddress() throws Exception {
//        UserAddresses resource = new UserAddresses();
//        resource.setId(165L);
//        resource.setPriority(0);
//        resource.setCode(99);
//        int update = Services.update(UserAddresses.class, resource, Restrictions.and(
//                Restrictions.eq(UserAddresses.USER_ID, 12L),
//                Restrictions.ne(UserAddresses.ID, 165L)));
//
//        System.out.println(update);
//        System.out.println();
//    }

    @Test
    public void testService() {
//        Seeds seed = Services.findOne(Seeds.class, 1L);
//        assertNotNull(seed);

//        Users user = Services.findOne(Users.class, 2L);
//        assertNotNull(user);
//
//        System.out.println();
    }

}
