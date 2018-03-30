package com.mifan.batch.analyzer.clustering;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/6/12
 */
public class Similarity implements Comparable<Similarity> {

    private long id;
    private long hash;
    private double score;

    public Similarity(long id, long hash, double score) {
        this.id = id;
        this.hash = hash;
        this.score = score;
    }

    @Override
    public int compareTo(Similarity o) {
        return Double.compare(o.score, this.score);
    }

    @Override
    public String toString() {
        return String.format("{id:%s, hash:%s, score:%s}", id, hash, score);
    }

    public long getId() {
        return id;
    }

    public long getHash() {
        return hash;
    }

    public double getScore() {
        return score;
    }

}
