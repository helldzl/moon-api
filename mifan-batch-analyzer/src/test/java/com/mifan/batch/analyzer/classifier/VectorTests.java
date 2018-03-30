package com.mifan.batch.analyzer.classifier;

import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder;
import org.apache.mahout.vectorizer.encoders.TextValueEncoder;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/6/5
 */
public class VectorTests {

    private static final FeatureVectorEncoder encoder = new TextValueEncoder("text");

    public static void a(String text) {
        Vector vector = new RandomAccessSparseVector(10000);
        encoder.addToVector(text, vector);

        System.out.println(vector);

    }

    public static void main(String[] args) {
        String text = "Analog Modeling Desktop Synthesizer and 24-bit/192kHz Audio/MIDI Interface";
        a(text);
    }

}
