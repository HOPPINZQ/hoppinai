package com.hoppinzq.search.embedding;

import cn.hutool.core.collection.CollUtil;
import com.hoppinzq.function.zq.constants.FunctionCallCommon;
import com.hoppinzq.model.openai.embedding.Embedding;
import com.hoppinzq.model.openai.embedding.EmbeddingRequest;
import com.hoppinzq.model.openai.embedding.EmbeddingResult;
import com.hoppinzq.openai.service.OpenAiService;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface MyEmbedding {

    /**
     * 获取多个文本的向量表示
     *
     * @param inputs 文本内容列表
     * @return
     */
    default List<List<Double>> getEmbeddings(List<String> inputs) {
        if (CollUtil.isEmpty(inputs)) {
            return Collections.emptyList();
        }
        OpenAiService service = new OpenAiService(FunctionCallCommon.apiKey,
                Duration.ofSeconds(60),
                FunctionCallCommon.openaiProxy);
        EmbeddingResult embeddings = service.createEmbeddings(EmbeddingRequest.builder()
                .model(FunctionCallCommon.embedding_model)
                .input(inputs)
                .build());
        List<Embedding> data = embeddings.getData();
        List<List<Double>> embeddingList = data.stream()
                .map(Embedding::getEmbedding)
                .collect(Collectors.toList());
        return embeddingList;
    }

    /**
     * 计算两个向量的余弦相似度
     *
     * @param vector1
     * @param vector2
     * @return
     */
    default double getSimilarity(double[] vector1, double[] vector2) {
        if (CollUtil.isEmpty(Collections.singleton(vector1))
                || CollUtil.isEmpty(Collections.singleton(vector2))
                || vector1.length != vector2.length) {
            return 0.0;
        }
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            normA += Math.pow(vector1[i], 2);
            normB += Math.pow(vector2[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * 批量插入向量数据
     *
     * @param inputs 文本内容列表
     */
    void insertBatch(List<String> inputs);

    /**
     * 查询向量数据
     *
     * @param input     文本内容
     * @param topK      返回结果数量
     * @param threshold 相似度阈值，低于此值的向量不会被返回
     * @return
     */
    List<String> search(String input, int topK, float threshold);
}

