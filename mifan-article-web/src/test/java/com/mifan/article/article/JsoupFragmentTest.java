package com.mifan.article.article;

import com.mifan.article.AbstractTests;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.PostsText;
import com.mifan.article.service.PostsService;
import com.mifan.article.service.PostsTextService;
import com.mifan.article.service.TopicsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Fields;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by LiuKai on 2017/9/8.
 */
public class JsoupFragmentTest extends AbstractTests {
    @Autowired
    private PostsService postsService;
    @Autowired
    private PostsTextService postsTextService;
    @Autowired
    private TopicsService topicsService;

    @Test
    public void formatting() {
        PostsText postsText = postsTextService.findOne(400822L);
        System.out.println(postsText.getContent());
        Document document  =Jsoup.parseBodyFragment(postsText.getContent());
        System.out.println(document);
        System.out.println(document.select("body>*"));
    }

    @Test
    public  void formating001() {
        List<Posts> postsList=postsService.findAll(
                Restrictions.and(
                        Restrictions.ne(Posts.PARENT_ID,0),
                        Restrictions.lt(Posts.MODIFIED,"2017-09-08")
                ),
                Fields.builder()
                        .add(Posts.ID)
                        .add(Posts.TOPIC_ID)
                        .build());
        Date date = new Date();
        for(Posts posts:postsList){
            PostsText postsText = postsTextService.findOne(posts.getId());
            Elements elements =Jsoup.parseBodyFragment(postsText.getContent()).select("body>*");
            postsText.setContent(elements.toString());
            posts.setModified(date);
            //Topics topics = topicsService.findOne(posts.getTopicId());
            //topics.setModified(date);
            //topicsService.update(topics);
            postsService.update(posts);
            postsTextService.update(postsText);
            System.out.println(posts.getId()+"++++"+posts.getTopicId());

        }

    }




}
