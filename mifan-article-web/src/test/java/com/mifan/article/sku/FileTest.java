package com.mifan.article.sku;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用来处理文件
 */
public class FileTest {
    //写文件，支持中文字符，在linux redhad下测试过
    public static void writeLog(String str) {
        try {
            String path = "D:\\敏感词\\words_new.txt";
            File file = new File(path);
            if (!file.exists())
                file.createNewFile();
            FileOutputStream out = new FileOutputStream(file, false); //如果追加方式用true
            StringBuffer sb = new StringBuffer();
            sb.append(str + "\n");
            out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
            out.close();
        } catch (IOException ex) {
            System.out.println(ex.getStackTrace());
        }
    }

    public static String readLog() {
        StringBuffer sb = new StringBuffer();
        String tempstr = null;
        try {
            String path = "D:\\敏感词\\words.txt";
            File file = new File(path);
            if (!file.exists())
                throw new FileNotFoundException();
//            BufferedReader br=new BufferedReader(new FileReader(file));            
//            while((tempstr=br.readLine())!=null)
//                sb.append(tempstr);    
            //另一种读取方式
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            List<String> list = new ArrayList<String>();
            while ((tempstr = br.readLine()) != null) {
                if (!list.contains(tempstr)) {
                    list.add(tempstr);
                } else {
                    System.out.println(tempstr);
                }
            }
            for (String _str : list) {
                sb.append(_str).append("\n");
            }
            writeLog(sb.toString());
//                
        } catch (IOException ex) {
            System.out.println(ex.getStackTrace());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        FileTest.readLog();
    }
}
