package com.mifan.quiz.service.impl;

import com.mifan.quiz.dao.QuizSessionDao;
import com.mifan.quiz.domain.QuizCount;
import com.mifan.quiz.domain.QuizSession;
import com.mifan.quiz.domain.Quizs;
import com.mifan.quiz.service.QuizSessionService;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.UUID;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-12-12
 */
@Service
public class QuizSessionServiceImpl extends AbstractBaseService<QuizSession, QuizSessionDao> implements QuizSessionService {

    @Override
    public int save(QuizSession entity) {
        Quizs quiz = Services.findOne(Quizs.class, entity.getQuizId(), Fields.builder().add(Quizs.STATE).build());
        if (quiz == null || quiz.getState() != 1) {
            throw new IllegalStateException("问卷不存在！");
        }

        String sessionCode = UUID.randomUUID().toString();
        entity.setSessionCode(sessionCode);
        return super.save(entity);
    }

    @Override
    public QuizSession getResult(String sessionCode) {
    	//查询当前会话
		QuizSession quizSession = Services.findOne(QuizSession.class, Restrictions.eq(QuizSession.SESSION_CODE, sessionCode));
		if(quizSession == null) {
            throw new IllegalStateException("您没有权限！");
        }
		if(quizSession.getAllDone() != 1) {
            throw new IllegalStateException("请先答完试题！");
        }
		Integer answerNum = quizSession.getAnswerNum();
		Integer rightNum = quizSession.getRightNum();
		//转化小数
        float ratio = (float)rightNum/answerNum ;
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数   
        String score = df.format(ratio);
        float scoref = Float.parseFloat(score);
        
        QuizCount count =  Services.findOne(QuizCount.class,Restrictions.eq(QuizCount.QUIZ_ID, quizSession.getQuizId()));
        long exceedPeoples = super.count(Restrictions.and(//分数大于他的人数
                    Restrictions.eq(QuizSession.QUIZ_ID, quizSession.getQuizId()),
                    Restrictions.eq(QuizSession.ALL_DONE, 1),
                    Restrictions.gt(QuizSession.RIGHT_NUM,quizSession.getRightNum())
                ));
        long samePeoples = super.count(Restrictions.and(//分数等于他的人数
                Restrictions.eq(QuizSession.QUIZ_ID, quizSession.getQuizId()),
                Restrictions.eq(QuizSession.ALL_DONE, 1),
                Restrictions.eq(QuizSession.RIGHT_NUM,quizSession.getRightNum())
            ));
        long allPeoples = super.count(Restrictions.and(//总共多少人答完，因为QuizCount中的数据会人工干预，所以不用那个里面统计的peoples
                Restrictions.eq(QuizSession.QUIZ_ID, quizSession.getQuizId()),
                Restrictions.eq(QuizSession.ALL_DONE, 1)
            ));
        String exceedPeoplesRatio = "0";
        if(allPeoples != 0 && exceedPeoples != 0) {
            exceedPeoplesRatio = df.format((float)(allPeoples - exceedPeoples - samePeoples)/allPeoples);
        }
        quizSession.setRanking(exceedPeoples + 1);
        quizSession.setExceedPeoplesRatio(exceedPeoplesRatio);
        quizSession.setScore(score);
		if(quizSession.getEnabled() == 0) {
	        quizSession.setCount(count);
	        return quizSession;
		}
		
		QuizCount save = new QuizCount();
		save.setQuizId(quizSession.getQuizId());
		save.setPeoples(1);
		save.setEnabled(1);
		Services.saveOrUpdate(QuizCount.class, save);//防止高并发造成的人数不准确，直接修改加锁
		save = Services.findOne(QuizCount.class, save.getId());
		
		if (scoref>=0 && scoref<0.1) {
		    save.setFirst(save.getFirst()+1);
		}else if (scoref>=0.1 && scoref <=0.19) {
		    save.setSecond(save.getSecond()+1);
		}else if(scoref>=0.20 && scoref <=0.29) {
		    save.setThird(save.getThird()+1);
		}else if (scoref>=0.30 && scoref <=0.39) {
		    save.setFourth(save.getFourth()+1);
		}else if (scoref>=0.40 && scoref <=0.49) {
		    save.setFifth(save.getFifth()+1);
		}else if (scoref>=0.50 && scoref <=0.59) {
		    save.setSixth(save.getSixth()+1);
		}else if (scoref>=0.60 && scoref <=0.69) {
		    save.setSeventh(save.getSeventh()+1);
		}else if (scoref>=0.70 && scoref <=0.79) {
		    save.setEighth(save.getEighth()+1);
		}else if (scoref>=0.80 && scoref <=0.89) {
		    save.setNinth(save.getNinth()+1);
		}else if (scoref>=0.90 && scoref <=1) {
		    save.setTenth(save.getTenth()+1);
		}
		Services.update(QuizCount.class, save);
		
		QuizSession update = new QuizSession(quizSession.getId());
		update.setEnabled(0);
		Services.update(QuizSession.class, update);
		
		quizSession.setCount(save);
		return quizSession;
	}


}
