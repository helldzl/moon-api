package com.mifan.article.es;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class BoostTests {

    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(1960, 1, 1, 23, 1, 30);
        LocalDateTime now = LocalDateTime.now();

        Date date = new Date();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());


        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime));

        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        Duration between = Duration.between(start, now);
        System.out.println("days:" + between.toDays());
        System.out.println("hours:" + between.toHours());
        System.out.println("days.hours:" + between.toDays() + "." + between.toHours());
        System.out.println("days:" + decimalFormat.format(between.toHours() / 24D));
        System.out.println("days:" + new BigDecimal(between.toHours() / 24D).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue());

        System.out.println("a1 " + Math.log1p(between.toHours() / 24D));
        System.out.println("a2 " + Math.log(between.toHours() / 24D));
        System.out.println("a3 " + Math.sqrt(between.toHours() / 24D));
    }

}
