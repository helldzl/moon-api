package com.mifan.batch.analyzer.vector;

import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/6/17
 */
public class VectorTests {

    public static void main(String[] args) {
        // 使用setQuick方法, 不检查边界
        // set方法会检查边界, 超过边界会出问题
        Vector vector = new RandomAccessSparseVector(2001);
        for (int i = 0; i < 20; i++) {
            vector.setQuick(i, Math.sqrt(i) + 1);
        }
        vector.setQuick(300, 5.9);
        vector.setQuick(400, 5.6);
        vector.setQuick(500, 5.6);
        vector.set(2, 33);
        vector.set(2000, 33);
        System.out.println();
    }

}
