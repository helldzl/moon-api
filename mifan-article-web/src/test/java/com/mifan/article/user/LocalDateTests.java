package com.mifan.article.user;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/7
 */
public class LocalDateTests {

    @Test
    public void testLocalDate() {
        Date time = new Date();

        LocalDate now = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
        LocalDate localDate = localDateTime.toLocalDate();
        boolean eq1 = localDate.equals(now);
        boolean eq2 = localDate.isEqual(now);
        System.out.println(eq1);
        System.out.println(eq2);

        ZoneId zone = ZoneId.systemDefault();
        Date date = Date.from(localDateTime.atZone(zone).toInstant());
        System.out.println(date);
        System.out.println(localDateTime);
    }
}
