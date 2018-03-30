package com.mifan.batch.analyzer.clustering;

import java.util.List;

/**
 * <a href=http://www2007.org/papers/paper215.pdf>http://www2007.org/papers/paper215.pdf</a>
 * <p>Given a set of features extracted from a document and their corresponding weights, we use simhash to generate an f-bit fingerprint as follows.</p>
 * <ol>
 * <li>We maintain an f-dimensional vector V, each of whose dimensions is initialized to zero.</li>
 * <li>A feature is hashed into an f-bit hash value. These f bits (unique to the feature) increment/decrement the f components of the vector by the weight of that feature as follows:
 * <ul>
 * <li>if the i-th bit of the hash value is 1, the i-th component of V is incremented by the weight of that feature;</li>
 * <li>if the i-th bit of the hash value is 0, the i-th component of V is decremented by the weight of that feature.</li>
 * </ul>
 * </li>
 * <li>When all features have been processed, some components of V are positive while others are negative. The signs of components determine the corresponding bits of the final fingerprint.</li>
 * </ol>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/6/7
 */
public class SimHash {

    /**
     * @param features features
     * @return SimHash
     */
    public static long hash(List<Feature> features) {
        int size = 64;
        double[] vector = new double[size];
        for (Feature feature : features) {
            long hash = feature.getHash();
            double weight = feature.getWeight();
            for (int i = 0; i < size; i++) {
                vector[i] += (hash & (1L << i)) == 0 ? -weight : weight;
                // vector[i] += ((hash >> i) & 1L) == 0 ? -weight : weight;
            }
        }

        long result = 0L;
        for (int i = 0; i < size; i++) {
            if (vector[i] > 0D) {
                result |= 1L << i;
            }
        }
        return result;
    }

    public static class Feature implements Comparable<Feature> {

        private String token;
        private long hash;
        private double weight;

        public Feature(String token, long hash, double weight) {
            this.token = token;
            this.hash = hash;
            this.weight = weight;
        }

        /**
         * @param feature
         * @return int
         */
        @Override
        public int compareTo(Feature feature) {
            return Double.compare(weight, feature.weight);
        }

        public String getToken() {
            return token;
        }

        public long getHash() {
            return hash;
        }

        public double getWeight() {
            return weight;
        }
    }

}
