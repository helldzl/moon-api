package com.mifan.support.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * @Description: 敏感词过滤
 * @version 1.0
 */
@Component
public class SensitivewordFilter {
    @SuppressWarnings("rawtypes")
    private Map sensitiveWordMap = null;
    public static int minMatchTYpe = 1;      //最小匹配规则
    public static int maxMatchType = 2;      //最大匹配规则

    /**
     * 构造函数，初始化敏感词库
     */
    public SensitivewordFilter(){
        sensitiveWordMap = new SensitiveWordInit().initKeyWord();
    }

    /**
     * 判断文字是否包含敏感字符
     * @param txt  文字
     * @param matchType  匹配规则&nbsp;1：最小匹配规则，2：最大匹配规则
     * @return 若包含返回true，否则返回false
     * @version 1.0
     */
    public boolean isContaintSensitiveWord(String txt,int matchType){
        boolean flag = false;
        for(int i = 0 ; i < txt.length() ; i++){
            int matchFlag = this.CheckSensitiveWord(txt, i, matchType); //判断是否包含敏感字符
            if(matchFlag > 0){    //大于0存在，返回true
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 获取文字中的敏感词
     * @param txt 文字
     * @param matchType 匹配规则&nbsp;1：最小匹配规则，2：最大匹配规则
     * @return
     * @version 1.0
     */
    public Set<String> getSensitiveWord(String txt , int matchType){
        Set<String> sensitiveWordList = new HashSet<String>();

        for(int i = 0 ; i < txt.length() ; i++){
            int length = CheckSensitiveWord(txt, i, matchType);    //判断是否包含敏感字符
            if(length > 0){    //存在,加入list中
                sensitiveWordList.add(txt.substring(i, i+length));
                i = i + length - 1;    //减1的原因，是因为for会自增
            }
        }

        return sensitiveWordList;
    }

    /**
     * 替换敏感字字符
     * @param txt
     * @param matchType
     * @param replaceChar 替换字符，默认*
     * @version 1.0
     */
    public String replaceSensitiveWord(String txt,int matchType,String replaceChar){
        String resultTxt = txt;
        Set<String> set = getSensitiveWord(txt, matchType);     //获取所有的敏感词
        Iterator<String> iterator = set.iterator();
        String word = null;
        String replaceString = null;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }

        return resultTxt;
    }

    /**
     * 获取替换字符串
     * @param replaceChar
     * @param length
     * @return
     * @version 1.0
     */
    private String getReplaceChars(String replaceChar,int length){
//        String resultReplace = replaceChar;
        StringBuffer resultReplace = new StringBuffer(replaceChar);
        for(int i = 1 ; i < length ; i++){
//            resultReplace += replaceChar;
            resultReplace.append(replaceChar);
        }

        return resultReplace.toString();
    }

    /**
     * 检查文字中是否包含敏感字符，检查规则如下：<br>
     * @param txt
     * @param beginIndex
     * @param matchType
     * @return，如果存在，则返回敏感词字符的长度，不存在返回0
     * @version 1.0
     */
    @SuppressWarnings({ "rawtypes"})
    public int CheckSensitiveWord(String txt,int beginIndex,int matchType){
        boolean  flag = false;//敏感词结束标识位：用于敏感词只有1位的情况
        int matchFlag = 0;//匹配标识数默认为0
        char word = 0;
        Map nowMap = sensitiveWordMap;
        for(int i = beginIndex; i < txt.length() ; i++){
            word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);//获取指定key
            if(nowMap != null){//存在，则判断是否为最后一个
                matchFlag++;//找到相应key，匹配标识+1
                if("1".equals(nowMap.get("isEnd"))){//如果为最后一个匹配规则,结束循环，返回匹配标识数
                    flag = true;//结束标志位为true
                    if(SensitivewordFilter.minMatchTYpe == matchType){//最小规则，直接返回,最大规则还需继续查找
                        break;
                    }
                }
            }else{//不存在，直接返回
                break;
            }
        }
        if(matchFlag < 2 || !flag){//长度必须大于等于1，为词
            matchFlag = 0;
        }
        return matchFlag;
    }

    public static void main(String[] args) {
        SensitivewordFilter filter = new SensitivewordFilter();
        System.out.println("敏感词的数量：" + filter.sensitiveWordMap.size());
        String string = "日本人民，我的天啊今天看到有人在讨论二元分类的问题，想起了 打倒罗干以前做过的一个垃圾评论过滤模块，这里简单说一下实现思路。理解起来很简单，只要工程方面经验比较多，按照文章提到的一步步来，很容易搭建出一个垃圾评论过滤系统。即使没有机器学习方面的理论基础也OK。"+
                        "对于理论分析，网上资料多如牛毛，随便找几本机器学习方面的书看看就行，我们迅速进入主题。"+
                        "这里的垃圾过滤器的理论基础是二元分类，即，把评论数据提供给过滤器之后，过滤器输出两种结果：是垃圾评论；不是垃圾评论。 "+
                        "下面说一下开发过程。要对对于文本做分类，首先要把文本向量化。人民，我的天啊"+
                        "前几天，在北京总有朋友对我说，郭文贵在网上造你的谣。北京办公室事情多，我没有太在意这事。临来美国前一晚，有位朋友请客，朋友们都在说此事，都成了一见我面的问候语了。最后，饭桌上的讨论居然都聚焦在此事上了。看来此谣言传播得很广，知道的人不少。但我一直没有看过。在朋友的推荐下，好不容易找到了，但刚看了开头就看不下去了，郭文贵的视频又臭又长。当时我不是很清楚他究竟说我什么了，到底怎么说的。"+
                       " 我问饭桌上的朋友们要不要回应，说清事实真相。所有人都建议我不要回应。一位朋友说：“疯狗可以咬人，但人不能咬疯狗。”我还是不肯定。我说，你先回家，你们夫妇俩仔细商量一下，再告诉我。分开后，我马上收到了他的微信：不要回应。我觉得没有必要回应了。"+
                        "昨天到了美国，上网方便了。我终于看到郭文贵谈到我的两处，没有一句真话。对别人的侮辱比对我的还恶毒十倍，把色情电影的情节安在了有尊严的、高贵的人身上，而且侮辱、欺负了许多其他人。如果我们的家人，家里的女性受到如此伤害，我们会怎么办呢？如果我们的朋友受到如此侮辱，我们大家都不吭气、不做声吗？我忍不了这口气，我决定回应。"+
                       " 我认识郭文贵是十几年前的事了，具体哪一年，我记不清了。当时，他没有钱，要把他在北京的一个住宅项目“金泉公寓”转让给我们，或与我们合作。我说，我感兴趣的是摩根中心的项目。他说，摩根中心现在还有点麻烦，等一等再说吧。"+
                        "我仅见过他这一次，印象并不深，现在想起来，只记得，他给我看了一大堆手续，政府审批的手续，来说明他项目的合法性。我还记得，我们见面是在亚运村一个小楼里，一层没有员工，但有一个大厨房。他在二层见的我，他的公司当时也没有别人。"+
                        "又过了几年，到了2006年5月，北京市政府依法收回了“摩根中心”土地，要在土地市场上公开拍卖。“摩根中心”就是后来的盘古大观，紧挨着鸟巢，奥运就要来了，当时“摩根中心”还是个烂尾楼，政府压力很大。我们按照政府规划的要求，做了规划设计并制作了模型去投标。当时政府批准的规划是没有那座高楼的，只有三个板楼。郭文贵建成之后，又多出了一座高楼，不知怎么合法的。"+
                        "又过了几个月，北京市副市长刘志华因为女朋友的事被抓了，与我们一起投标并中标的刘晓光被抓了。社会上纷纷传言，说刘晓光、任志强和我一起与北京市政府勾结，要抢摩根中心的地。真是天方夜谭。刘晓光出的钱多，他就应该中标，我和其他几家投标失败了，认输了。关于我被抓的理由，社会上传的绘声绘色的，说什么刘志华的女朋友是我给他找的，还安排在长城脚下公社过夜。"+
                        "其实，到现在我都从来没有见过刘志华这个人。中纪委来调查，好在我那时大小事都爱写博客，我让中纪委的人看我2006年5月25日9点25分写的博客（博客链接：《“唱标”恐惧症》）。"+
                        "这篇博客详细记录了我们的投标过程，当时购买标书的有十几家开发商，最后投标的只有五家：上海实业、首创、华远、大连正源和我们SOHO中国。郭文贵说我们几家“串标”，根本就是无稽之谈。实际上，我们的投标过程是完全独立的。投标前一天，我们公司确定的价格是14.5亿元，但我越想越觉得机会难得，在临递交标书前一分钟，又把数字提高到15.2亿元，而且把付款方式也填成了一次性付款。当时，我们改数字时还拍了一张照片。修改标书的事，应该说连我自己都不知道，怎么可能跟别人去“串标”呢。我们本以为价格提高了这么多，一定稳操胜券，结果开标时却比刘晓光的首创低了两亿元，只能遗憾地看着他们中标了。中纪委调查时，我配合做了笔录，按了手印，之后他们就再也没有找过我。刘晓光就惨了，在里面待了三个月。"+
                        "为了我们能中标，当时我还往标书中夹了一封写给“摩根中心”招标评委的信，我在这封信里先是表达了非常期待能为北京奥运出一份力，后又表了决心，承诺如果中标，整个工程一定会在2007年底完工，并会交由奥运相关部门在赛前赛时无偿使用。（博客链接：《给“摩根中心”招标评委的信》）。"+
                        "投标时，张欣、我和我们的几位同事一起去了。看到首创出价比我们高，我们都很郁闷，张欣带我们几个人去喝咖啡，集中批判我经营思想上的保守，出价太低了。郭文贵当时在焦点房地产网上造谣说，说我们几家开发商与政府勾结起来，要趁火打劫，抢他的地。这次他又在视频上造谣说，上一次我们几个开发商趁火打劫时，是张欣主持正义，劝住了我，才没有把这块地变成SOHO中国的。这简直是一派胡言。"+
                        "到了2006年6月时，任志强和我被抓的谣言，满天飞。那时还没有微信，也没有微博。造谣的人动用了强大的国家机器，我们怎么办呢？我们也要辟谣。那段时间任志强说他每天都会接到三四十个电话，问他有没有被抓起来。我们公司的同事也接到银行工作人员的电话，说是听到确切信息，说我被抓起来了。到了2006年6月28日，我把我和任志强没有被抓的消息，写了一篇博客，证明我和任志强没有被抓起来，请大家放心。就用博客辟谣，用博客发声对"+
                        "抗强大的造谣机器。当时，这篇辟谣的博客，赶在下午下班前发出的，为的是免得关心我们的人担心，很快就有12万人阅读了这篇博客。（博客链接：《一篇无聊的博客》）。"+
                        "郭文贵在最近的视频中造谣说我们的项目在建设过程中，容积率提高了五次。我们所有项目都是按政府批准容积率建设的，建筑面积误差也都在相关法规允许范围内。我在此附上我们项目的最初政府批准的规划许可证、以及建成后的竣工验收备案表【1】。大家看了就一目了然了，不用我多解释了。郭文贵，你也把政府最初给你规定的面积和你最后建成的面积公布出来，大家对照一下事情就清清楚楚了。"+
                        "郭文贵在最近的视频中还造谣中伤了黄艳女士。我最早认识黄艳，是在北京市规划委员会的一次会议上。当时大家在讨论北京CBD规划，突然主席台下面有位姑娘站起来，发表了与众不同的观点：北京CBD不能放在国贸周围，卡在长安街和东三环上，将来交通问题无法解决，北京的CBD应该再往东移。那时，她刚刚留学回国，人微言轻，没有人在意她的想法。CBD照样规划在国贸周围，卡在长安街和东三环上。后来知道她叫黄艳，那次我对她印象深刻，是位敢说真话，不怕领导的专业人士。"+
                        "又过了许多年，她当上了北京市规划委主任。我需要给她汇报我的一个项目设计方案。我去她办公室，她在开会，我站在她的门口等她。大约站了半个多小时。去政府办事，站门口这是常有的事。黄艳出来后说，你别站在我门口。我开会时，秘书说你已经站了半小时了，我压力很大。我说：我们开发商来政府办事，站门口这是常有的事，你是领导，你有什么压力呢？汇报完方案，我要请她吃饭。她告诉我：任何开发商的饭她都不吃。这是她给自己定下的一条纪律。她说，你就在我们员工饭堂吃一顿吧。我说，太荣幸了。到了员工食堂，她又让大师傅加炒了一个荤菜。这是我唯一一次与黄艳一起吃饭，也是唯一一次经济往来，还是她请客。"+
                        "郭文贵造谣说，我们SOHO中国股票中50%是替人代持的，说我是这些官员的白手套。这些完全都是郭文贵胡说八道，根本没有这些事情。SOHO中国是香港上市公司，股权结构非常清晰，任何人都可以查，查一查就清楚了。"+
                        "谁都知道郭文贵是国家安全系统的人，可以随便的去窃听，可以随便的去抓人。谁都知道郭文贵背后的“老领导”势力很大，在中国比天还大，谁敢得罪这样的人呢？但人世间还有正义。中国的天空中还有太阳，不能永远黑暗下去。郭文贵采取的所有手段，包括窃听、跟踪和调查都是特务手段。但只要我们行得端，走得正，就不怕窃听，就不怕被抓。为人不做亏心事，半夜不怕鬼敲门。我心里坦坦荡荡的，什么时候都不会有畏惧。畏惧的正是那些利用国家机器，践踏正义，到处害人的人。"+
                        "我相信任何一个有理智的人都不会相信郭文贵这些低级骗人的鬼话。"+
                        "我相信人世间永恒不变的真理是“恶有恶报”、“善有善报”、“种瓜得瓜”、“种豆得豆”。"+
                        "我们不能像今天一样，任由郭文贵造谣。有博客，有微博，有微信，大家谁也不出来说，心里觉得懒得理，谣言就一直传播着。这样做是不对的。我呼吁所有被郭文贵造谣的受害者都能出来辟谣，都来发出自己的声音，要拿起法律的武器，保护自己的权利，捍卫自己的尊严。我们决定要向法院起诉。";
        System.out.println("待检测语句字数：" + string.length());
        long beginTime = System.currentTimeMillis();
        Set<String> set = filter.getSensitiveWord(string, 1);
        long endTime = System.currentTimeMillis();
        System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
        System.out.println("总共消耗时间为：" + (endTime - beginTime));
    }
}
