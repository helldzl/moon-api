package com.mifan.user.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class TenpayUtil {
    public static void main(String[] args) {
        /*for(int i = 0;i < 1;i ++) {
            TenpayUtil.randomPackage();
        }*/
        String s = "1,2,3";
        String[] sr = s.split(",");
        List<String> strs = Arrays.asList(sr);
        List<Double> ds = strs.stream().map(Double::valueOf).collect(Collectors.toList());
        for(Double d : ds) {
            System.out.println(d);
        }
    }

    public static List<Double> randomPackage() {
        DecimalFormat df = new DecimalFormat("#.00");
        double total_money = 15;
        int total_people = 10;
        double min_money = 100; // 每个人最少能收到0.99元
        double max_money = 280; // 每个人最多能收到2.10元
//        double lingjiezhi=(total_money/total_people)*100;
        double allresult = 0;
        List<Double> resultList = new ArrayList<Double>();
        for (int i = 0; i <total_people; i++) {
            //保护值
            double procte = (total_people-i-1)*min_money/100;
            //可支配金额
            double zpje = total_money-procte;
            if(zpje*100 < max_money){
                max_money = zpje*100;
            }
            double result = restRound(min_money, max_money, i, zpje, total_people-1);
            total_money=total_money-result;
            allresult+=result;
            result = Double.parseDouble(df.format(result));
            if(result < 3 && result >= 1) {
                resultList.add(result);
            }
//            if(result > 2.1 || result < 1) {
                System.out.format("红包  %.2f,余额  %.2f \n",result,total_money);
//            }
        }
        allresult = Double.parseDouble(df.format(allresult));
//        if(allresult > 12) {
            System.out.println(allresult);
//        }
        return resultList;

    }
    public static double restRound(double min,double max,int i,double money,int count){
        double redpac = 0;
        if(i == count){
            redpac = money;
        }else{
            redpac = (Math.random()*(max-min)+min)/100;
        }
        return redpac;
    }

    /**
     * 获取随机字符串
     * @return
     */
    public static String getNonceStr() {
        return String.valueOf(System.currentTimeMillis()) + TenpayUtil.buildRandom(4);
    }
    /**
     * 生成商户订单号的结尾
     * @return
     */
    public static String getMchBillnoEnding() {
        String time = String.valueOf(System.currentTimeMillis());
        String timeEnd = time.substring(time.length()-6);
        return TenpayUtil.getCurrTime() + timeEnd + TenpayUtil.buildRandom(4);
    }
    /**
     * 获取当前时间 yyyyMMddHHmmss
     * @return String
     */
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMdd");
        String s = outFormat.format(now);
        return s;
    }

    /**
     * 获取当前日期 yyyyMMdd
     * @param date
     * @return String
     */
    public static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String strDate = formatter.format(date);
        return strDate;
    }

    /**
     * 取出一个指定长度大小的随机正整数.
     *
     * @param length
     *            int 设定所取出随机数的长度。length小于10
     * @return int 返回生成的随机数。
     */
    public static int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }

    /**
     * 获取编码字符集
     * @param request
     * @param response
     * @return String
     */

    public static String getCharacterEncoding(HttpServletRequest request,
            HttpServletResponse response) {

        if(null == request || null == response) {
            return "utf-8";
        }

        String enc = request.getCharacterEncoding();
        if(null == enc || "".equals(enc)) {
            enc = response.getCharacterEncoding();
        }

        if(null == enc || "".equals(enc)) {
            enc = "utf-8";
        }

        return enc;
    }

    /**
     * 获取unix时间，从1970-01-01 00:00:00开始的秒数
     * @param date
     * @return long
     */
    public static long getUnixTime(Date date) {
        if( null == date ) {
            return 0;
        }

        return date.getTime()/1000;
    }

    /**
     * 时间转换成字符串
     * @param date 时间
     * @param formatType 格式化类型
     * @return String
     */
    public static String date2String(Date date, String formatType) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatType);
        return sdf.format(date);
    }

}












