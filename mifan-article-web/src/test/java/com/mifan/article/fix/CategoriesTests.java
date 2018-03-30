package com.mifan.article.fix;

import com.mifan.article.AbstractTests;
import com.mifan.article.domain.ForumCategories;
import com.mifan.article.domain.Topics;
import org.junit.Test;
import org.moonframework.core.util.ObjectMapperFactory;
import org.moonframework.model.mybatis.service.Services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/10/31
 */
public class CategoriesTests extends AbstractTests {

    /**
     * 初始化类别数据
     *
     * @throws Exception
     */
    @Test
    public void testFixCategory() {
        read("C:\\Quzile\\我的工作\\数据\\Category.txt", list -> {
            ForumCategories parent = null;
            for (Map<String, String> map : list) {
                int depth = parent == null ? 1 : parent.getDepth() + 1;
                Long parentId = parent == null ? 0 : parent.getId();
                String title = map.get("en");

                for (int i = 1; i < depth; i++) {
                    System.out.print("  ");
                }
                System.out.println(title);

                ForumCategories category = findOne(parentId, title);
                if (category == null) {
                    // save
                    category = new ForumCategories();
                    category.setTitle(title);
                    category.setForumId(Topics.ForumType.PRODUCT.getIndex());
                    category.setParentId(parentId);
                    category.setDepth(depth);
                    category.setLeaf(depth == list.size() ? 1 : 0);
                    Services.save(ForumCategories.class, category);

                    // update
                    category.setRootId(parent == null ? category.getId() : parent.getRootId());
                    category.setPath(parent == null ? String.valueOf(category.getId()) : parent.getPath() + "." + category.getId());
                    Services.update(ForumCategories.class, category);
                }
                parent = category;
            }
        });
    }

    /**
     * @param parentId
     * @param title
     * @return
     */
    private ForumCategories findOne(Long parentId, String title) {
        ForumCategories category = new ForumCategories();
        category.setForumId(Topics.ForumType.PRODUCT.getIndex());
        category.setParentId(parentId);
        category.setTitle(title);
        return Services.findOne(ForumCategories.class, category);
    }

    /**
     * @param pathname
     * @param consumer
     */
    @SuppressWarnings("unchecked")
    public static void read(String pathname, Consumer<List<Map<String, String>>> consumer) {
        File file = new File(pathname);
        try (FileInputStream fileInputStream = new FileInputStream(file);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                List<Map<String, String>> list = ObjectMapperFactory.readValue(line, List.class);
                consumer.accept(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void read1() {
        read("C:\\Quzile\\dictionary.txt", System.out::println);
    }

    public static void read2() {
        Set<String> set = new TreeSet<>();
        String path = "C:\\Quzile\\我的工作\\数据\\Category.txt";
        CategoriesTests.read(path, list -> {
            String en = list.get(0).get("en");
            if (!set.contains(en)) {
                set.add(en);
            }
        });
        set.forEach(System.out::println);
    }

    public static void main(String[] args) {
        read2();
    }

}
