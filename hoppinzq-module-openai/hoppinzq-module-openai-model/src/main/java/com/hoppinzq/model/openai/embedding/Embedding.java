package com.hoppinzq.model.openai.embedding;

import lombok.Data;

import java.util.List;

/**
 * https://beta.openai.com/docs/api-reference/classifications/create
 */
@Data
public class Embedding {
    String object;
    List<Double> embedding;
    Integer index;
}
