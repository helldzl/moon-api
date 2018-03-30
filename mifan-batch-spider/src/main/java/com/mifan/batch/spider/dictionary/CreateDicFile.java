package com.mifan.batch.spider.dictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by LiuKai on 2017/5/17.
 * 根据不同品牌生成不同的字典词库文件，在文章进行分词的时候，直接取词库文件中有的词。
 * 通过这样的分词，和 产品的名称进行对应比较，如果一个文章中有这个 产品的所有单词，
 * 那么这个评测文章应该就关联这个 产品。
 * 一个产品可以关联多个文章。
 * 多个文章也可以关联一个产品
 * 4000个品牌形成4000个字典库是不是太多了？
 * 生成扩展词使用
 */
public class CreateDicFile {

    public static void main(String[] args) throws IOException {
        CreateDicFile createDicFile = new CreateDicFile();
        createDicFile.createDicFile("jbl","xx400 33");
        createDicFile.createDicFile("jbl","xx3");
        createDicFile.createDicFile("jbl","xx3");
        createDicFile.createDicFile("jbl","xx311");
    }


    //传入dic文件的名字 和  产品名字 和一些特征
    public void createDicFile(String dicName,String str) throws IOException {
        File file = new File(dicName+".txt");
        if(!file.exists()){
            file.createNewFile();
        }
        FileWriter fw= new FileWriter(file,true);
        BufferedWriter bw= new BufferedWriter(fw);
        //通过空格分词
        StringTokenizer st=new StringTokenizer(str," ");
        while(st.hasMoreElements()){
            //获取下一个单词
            String token=st.nextToken();
            List<String> dic = new ArrayList<>();
            Dictionary.loadDict(dicName+".txt",dic);
            if(!dic.contains(token)) {
                bw.write(token + "\n");
            }
        }
        bw.flush();
        bw.close();
        fw.close();
    }

}
