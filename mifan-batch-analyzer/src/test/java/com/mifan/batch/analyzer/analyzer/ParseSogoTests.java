package com.mifan.batch.analyzer.analyzer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/22
 */
public class ParseSogoTests {

    public static void main(String[] args) throws Exception {
        File dic = new File("C:\\Quzile\\词库");
        for (File file : dic.listFiles()) {
            sogou(file, "C:\\Quzile\\词库\\goods.txt", true);
        }
    }

    /**
     * 读取scel的词库文件
     * 生成txt格式的文件
     *
     * @param file       输入路径
     * @param outputPath 输出路径
     * @param append     是否拼接追加词库内容 true 代表追加,false代表重建
     **/
    private static void sogou(File file, String outputPath, boolean append) throws IOException {
        if (!append && Files.deleteIfExists(Paths.get(outputPath))) {
            System.out.println("存储此文件已经删除");
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(outputPath, "rw")) {
            int count = 0;
            SouGouModel model = new SouGouReader().read(file);
            Map<String, List<String>> words = model.getWordMap(); //词<拼音,词>
            Set<Entry<String, List<String>>> set = words.entrySet();
            for (Entry<String, List<String>> entry : set) {
                List<String> list = entry.getValue();
                for (String word : list) {
                    //System.out.println(word);
                    randomAccessFile.seek(randomAccessFile.getFilePointer());
                    randomAccessFile.write((word + "\n").getBytes());//写入txt文件
                    count++;
                }
            }
            System.out.println("生成txt成功！,总计写入: " + count + " 条数据！");
        }


    }


}
