package com.mifan.batch.analyzer.prop;

import java.io.*;
import java.util.Properties;

/**
 * <p>初始化翻译字典</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/5/27
 */
public class InitDictionaryTests {

    public static void read(String filePath, Properties prop) {
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    String[] split = lineTxt.split("=");
                    prop.put(split[0], split[1]);
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

    }

    public static void main(String argv[]) {
        try {
            Properties prop = new Properties();
            try (OutputStream fos = new FileOutputStream("C:\\Users\\quzile\\IdeaProjects\\2.0\\mifan-api\\mifan-batch-analyzer\\src\\main\\resources\\classification\\dictionary.properties")) {
                String filePath = "C:\\Users\\quzile\\Desktop\\翻译.txt";
                read(filePath, prop);
                prop.store(fos, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
