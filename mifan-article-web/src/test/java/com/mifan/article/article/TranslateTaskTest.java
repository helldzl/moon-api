package com.mifan.article.article;

import com.mifan.article.AbstractTests;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.TranslateTask;
import com.mifan.article.domain.support.TranslateTaskAudit;
import com.mifan.article.service.TranslateTaskService;
import io.jsonwebtoken.lang.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TranslateTaskTest extends AbstractTests {

    @Autowired
    private TranslateTaskService translateTaskService;

    @Test
    public void saveTest() {
        TranslateTask task = new TranslateTask();
        task.setTopicId(100l);
        task.setCreator(1031l);
        task.setModifier(1031l);
        int result = translateTaskService.save(task);
        Assert.isTrue(result > 0);
    }
    @Test
    public void updateOfTranslatorTest() {
        TranslateTask task = new TranslateTask(34l);
        task.setState(2);
        task.setModifier(1031l);
        Assert.isTrue(translateTaskService.updateOfTranslator(task) > 0);
    }
    @Test
    public void updateOfTranslatorForPostTest() {
        TranslateTask task = new TranslateTask(34l);
        task.setState(3);
        task.setModifier(1031l);
        Posts post = new Posts();
        post.setTitle("R24翻译3333");
        task.setPost(post);
        Assert.isTrue(translateTaskService.updateOfTranslator(task) > 0);
    }
    @Test
    public void updateOfAuditorTest() {
        TranslateTaskAudit taskAudit = new TranslateTaskAudit(34l);
        taskAudit.setState(4);
        taskAudit.setAuditor(1031l);
        Assert.isTrue(translateTaskService.updateOfAuditor(taskAudit) > 0);
    }
    @Test
    public void updateOfAuditorForPostTest() {
        TranslateTaskAudit taskAudit = new TranslateTaskAudit(34l);
        taskAudit.setState(6);
        taskAudit.setAuditor(1031l);
        Posts post = new Posts();
        post.setTitle("R24翻译审核");
        taskAudit.setPosts(post);
        Assert.isTrue(translateTaskService.updateOfAuditor(taskAudit) > 0);
    }
}
