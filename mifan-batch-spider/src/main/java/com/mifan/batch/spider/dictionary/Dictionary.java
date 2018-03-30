package com.mifan.batch.spider.dictionary;



import com.mifan.batch.spider.dictionary.util.ChangeToLower;
import com.mifan.batch.spider.dictionary.util.RemoveChar;
import com.mifan.batch.spider.dictionary.util.RemoveTag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by LiuKai on 2017/5/11.
 *写一个简单的字典，主要对品牌进行分类
 *本字典只筛选存在本字典存在的单词 ，刚刚和其他字典相反。
 * 1、再加上一个单词计数，给未来做准备
 *
 */

public class Dictionary {

    public static void main(String[] args) {

        String s="<h1>TestDrive: Algoriddim djay 3</h1> \n" +
                "<p>Posted on Apr 29, 2011</p> \n" +
                "<p>FutureMusic Power Rating: 80% — “The most DJ software you can buy for $50…”</p> \n" +
                "<p><img src=\"http://static.budee.com/yyren/image/199/21/1427280.jpg\" alt=\"djay 3 Looping\"><br> <img src=\"http://static.budee.com/yyren/image/199/21/1427281.jpg\" width=\"500\" height=\"35\"></p> \n" +
                "<p><strong>djay 3</strong> is the latest incarnation of Algoriddim’s <em>living room</em> DJ software and it hits a new high water mark for ease-of-use, simplicity and depth. djay’s core attribute is that it doesn’t try to be the end-all, be-all of DJ software, it simply allows anyone, from first time DJs to true professionals, to mix tracks from their iTunes library. <em>More!</em></p> \n" +
                "<p><strong>Read the FutureMusic <a href=\"http://www.futuremusic.com/news/testdrive/djay-review.html\">djay 3 review</a>.</strong></p> \n" +
                "<div></div> \n" +
                "<span></span>";
        /*List<Map.Entry<String, Integer>>  word;
        word=Dictionary.Dict(s," ");
        for(int i=0;i<word.size();i++){
            System.out.println(word.get(i).getKey()+" "+word.get(i).getValue());
        }*/
        System.out.println(Dictionary.result(s));
    }



    //定义一个自动增长的对象数组
    public static List<String> dic =new ArrayList<String>();
    //定义一个停用词词典的对象数组
    public static List<String> dicStop =new ArrayList<String>();
    //第一个参数出入文章的内容或者标题的字符串，第二个参数是分隔符。

    public static  String  result(String s){
        List<Map.Entry<String, Integer>>  word;
        word=Dictionary.Dict(s," ");
        if (word.size()>=2){
            return word.get(0).getKey()+','+word.get(1).getKey();
        }
        if (word.size()==1){
            return  word.get(0).getKey();
        }
        return  null;
    }

    public static List<String> divide(String s)
    {
        //默认按照空格进行分词就行
        String delim=" ";
        //去除标点符号
        s= RemoveChar.removeChar(s,',');
        s= ChangeToLower.changeToLower(s);
        s= RemoveTag.removeTag(s);
        List<String> word=new ArrayList<>();
        StringTokenizer st;
        if(delim.isEmpty())
        {
            st= new StringTokenizer(s);
        }
        else
        {
            st= new StringTokenizer(s,delim);
        }
        DictStop();//加载停用词典
        while(st.hasMoreElements())
        {
            //获取下一个单词
            String token=st.nextToken();
            if(!dicStop.contains(token)){
                word.add(token);
            }

        }


        //由于要进行分词，所以需要进行再次组合，再次分词。第一步 先进行，相邻单词合成一个词，进行分词。
        int size1=word.size();
        //进行第二次分词，主要防止有两个单词为一个词组
        for(int i=0;i<size1-1;i++){
            String strWord=word.get(i)+" "+word.get(i+1);
            if(dicStop.contains(strWord)){
                word.remove(word.get(i));
                word.remove(word.get(i));
                i--;
            }else{
                word.add(word.get(i)+" "+word.get(i+1));
            }

        }

        //进行第三次分词，主要防止有三个单词为一个词组
        for(int i=0;i<size1-2;i++){
            String strWord=word.get(i)+" "+word.get(i+1)+" "+word.get(i+2);
            if(dicStop.contains(strWord)){
                word.remove(word.get(i));
                word.remove(word.get(i));
                word.remove(word.get(i));
                i--;
            }else{
                word.add(word.get(i)+" "+word.get(i+1)+" "+word.get(i+2));
            }

        }
        //进行第四次分词，主要防止有四个单词为一个词组
        for(int i=0;i<size1-3;i++){
            String strWord=word.get(i)+" "+word.get(i+1)+" "+word.get(i+2)+word.get(i+3);
            if(dicStop.contains(strWord)){
                word.remove(word.get(i));
                word.remove(word.get(i));
                word.remove(word.get(i));
                word.remove(word.get(i));
                i--;
            }else{
                word.add(word.get(i)+" "+word.get(i+1)+" "+word.get(i+2)+word.get(i+3));
            }
        }

        return word;

    }

    //使用自定义的词典进行分词
    public static List<Map.Entry<String, Integer>> Dict(String s, String delim)
    {
        //没有加载字典时初始化它
        if(dic.size()==0)
        {
            //加载字典文件
            loadDict("brandDic.txt",dic);
            loadDict(Dictionary.class.getResource("/brandDic.txt").getPath(),dic);
        }
        //这是形成一个list的一个词数组
        List<String> word=divide(s);

        //List<String> nword=new ArrayList<>();
        Map<String,Integer> nword= new HashMap<>(16);
        for(String str:word){
            if (dic.contains(str)){
                if(nword.keySet().contains(str)){
                    nword.put(str,nword.get(str)+1);
                }else {
                    nword.put(str,1);
                }
            }
        }
        List<Map.Entry<String, Integer>> nwordSort =
                new ArrayList<Map.Entry<String, Integer>>(nword.entrySet());
        //排序
        Collections.sort(nwordSort, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue() - o1.getValue());
            }
        });

        return nwordSort;
    }


    //使用停用字典进行一些词的停用
    public static void DictStop()
    {
        //没有加载字典时初始化它
        if(dicStop.size()==0)
        {
            //加载字典文件
            //loadDict("dicStop.txt",dicStop);
            loadDict(Dictionary.class.getResource("/dicStop.txt").getPath(),dicStop);
        }
    }


    /*
    *加载词典
    * 一、需要对比的品牌词典，
    * 二、需要停用词的词典
    * 三、日常词汇停用（或者放到第二个字典中）
    * */
    public static void loadDict(String path,List<String> dic)
    {
        try {
            File file=new File(path);
            if(file.isFile() && file.exists())
            {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    //字典中非小写的，都转成小写
                    dic.add(ChangeToLower.changeToLower(lineTxt.trim()));
                }
                read.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
