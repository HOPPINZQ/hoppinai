package com.hoppinzq.search.embedding;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmbeddingUtils {

    private EmbeddingUtils() {
    }

    /**
     * 余弦相似度算法
     *
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 相似度值，范围在[-1,1]之间，值越大表示越相似
     */
    public static double cosineSimilarity(double[] vector1, double[] vector2) {
        RealVector realVector1 = new ArrayRealVector(vector1);
        RealVector realVector2 = new ArrayRealVector(vector2);
        double dotProduct = realVector1.dotProduct(realVector2);
        double norm = realVector1.getNorm() * realVector2.getNorm();
        double similarity = dotProduct / norm;
        return similarity;
    }

    /**
     * jsonarray转double数组
     *
     * @param jsonArray
     * @return
     */
    public static double[] getDoubleArray(JSONArray jsonArray) {
        double[] array = new double[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            try {
                array[i] = jsonArray.getDouble(i);
            } catch (JSONException e) {
                e.printStackTrace();
                array[i] = 0.0;
            }
        }
        return array;
    }

    public static double[] getDoubleArray(List<Double> list) {
        double[] array = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            try {
                array[i] = list.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
                array[i] = 0.0;
            }
        }
        return array;
    }

    public static <T> T[] addAll(T[] array1, T[] array2) {
        T[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    public static <T> List<List<T>> divideList(List<T> list, int n) {
        int size = list.size();
        int quotient = size / n;
        int remainder = size % n;
        List<List<T>> result = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < n; i++) {
            int count = quotient;
            if (i < remainder) {
                count++;
            }
            List<T> sublist = list.subList(index, index + count);
            result.add(sublist);
            index += count;
        }
        return result;
    }
}
