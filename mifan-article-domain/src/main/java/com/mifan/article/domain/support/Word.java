package com.mifan.article.domain.support;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/22
 */
public class Word {

    private int rank;
    private String keyword;
    private int value;
    private double score;

    public Word() {
    }

    public Word(int rank, String keyword) {
        this.rank = rank;
        this.keyword = keyword;
    }

    public Word(int rank, String keyword, int value, double score) {
        this.rank = rank;
        this.keyword = keyword;
        this.value = value;
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
