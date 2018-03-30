package com.mifan.article.article;

//import com.mifan.AbstractTests;
import com.mifan.article.AbstractTests;
import com.mifan.article.domain.Seeds;
import com.mifan.article.service.SeedsService;
//import com.sun.mail.util.MailSSLSocketFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

//import javax.mail.Message;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by LiuKai on 2017/11/17.
 */
public class MailTest extends AbstractTests {
    @Autowired
    private SeedsService seedsService;

//    public static void sendMail(String fromMail, String user, String password,
//                                String toMail,
//                                String mailTitle,
//                                String mailContent) throws Exception {
//        Properties props = System.getProperties();
//        props.setProperty("mail.smtp.host", "smtp.ym.163.com");
//        props.setProperty("mail.smtp.port", "994");
//        props.put("mail.smtp.auth", "true"); // 允许smtp校验
//        MailSSLSocketFactory sf = null;
//        try {
//            sf = new MailSSLSocketFactory();
//        } catch (GeneralSecurityException e) {
//            System.out.println(e);
//        }
//        sf.setTrustAllHosts(true);
//        props.put("mail.smtp.ssl.enable", "true");
//        props.put("mail.smtp.ssl.socketFactory", sf);
//        props.put("mail.smtp.socketFactory.fallback", "false");
//        props.put("mail.smtp.socketFactory.port",  "994");
//
//        Session session = Session.getInstance(props);//根据属性新建一个邮件会话
//        //session.setDebug(true); //有他会打印一些调试信息。
//
//        MimeMessage message = new MimeMessage(session);//由邮件会话新建一个消息对象
//        message.setFrom(new InternetAddress(fromMail));//设置发件人的地址
//        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toMail));//设置收件人,并设置其接收类型为TO
//        message.setSubject(mailTitle);//设置标题
//        //设置信件内容
////        message.setText(mailContent); //发送 纯文本 邮件
//        message.setContent(mailContent, "text/html;charset=gbk"); //发送HTML邮件，内容样式比较丰富
//        message.setSentDate(new Date());//设置发信时间
//        message.saveChanges();//存储邮件信息
//
//        //发送邮件
////        Transport transport = session.getTransport("smtp");
//        Transport transport = session.getTransport();
//        transport.connect(user, password);
//        transport.sendMessage(message, message.getAllRecipients());//发送邮件,其中第二个参数是所有已设好的收件人地址
//        transport.close();
//    }

    @Test
    public void test() {
        MailTest mailTest = new MailTest();
        List<Seeds> seedsPage=seedsService.getQuestionTemplate(1,1).getContent();
        StringBuilder seedsQuestionTemplate= new StringBuilder("<p> 可能出现问题的模板："+ seedsPage.size()+"<br />");
        for (Seeds seed:seedsPage) {
            seedsQuestionTemplate.append(seed.getId());
            seedsQuestionTemplate.append("  ");
            seedsQuestionTemplate.append(seed.getUrl());
            seedsQuestionTemplate.append("  ");
            seedsQuestionTemplate.append(seed.getName());
            seedsQuestionTemplate.append("  ");
            seedsQuestionTemplate.append(seed.getDescription());
            seedsQuestionTemplate.append("<br />");
        }
        seedsQuestionTemplate.append("</p>");
        List<Seeds> seedsesNotContent = seedsService.getNotWholeTemplate(1,1).getContent();
        seedsQuestionTemplate.append("<p>内容不全模板："+ seedsesNotContent .size()+"个<br />");
        for (Seeds seed:seedsesNotContent) {
            seedsQuestionTemplate.append(seed.getId());
            seedsQuestionTemplate.append("  ");
            seedsQuestionTemplate.append(seed.getUrl());
            seedsQuestionTemplate.append("  ");
            seedsQuestionTemplate.append(seed.getName());
            seedsQuestionTemplate.append("  ");
            seedsQuestionTemplate.append(seed.getDescription());
            seedsQuestionTemplate.append("<br />");
        }
        seedsQuestionTemplate.append("</p>");
//
//        mailTest.sendMail("liukai@mifanxing.com", "liukai@mifanxing.com", "budee123!@#",
//                "850770722@qq.com",
//                "模板需要更新提示",
//                seedsQuestionTemplate.toString());
    }
}
