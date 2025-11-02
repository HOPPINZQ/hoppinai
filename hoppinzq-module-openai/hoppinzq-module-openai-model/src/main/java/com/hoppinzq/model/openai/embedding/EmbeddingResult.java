package com.hoppinzq.model.openai.embedding;

import com.hoppinzq.model.openai.Usage;
import lombok.Data;

import java.util.List;

/**
 * https://beta.openai.com/docs/api-reference/embeddings/create
 */
@Data
public class EmbeddingResult {
    String model;
    String object;
    List<Embedding> data;
    Usage usage;
}
