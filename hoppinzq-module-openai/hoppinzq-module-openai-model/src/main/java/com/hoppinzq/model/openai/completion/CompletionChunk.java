package com.hoppinzq.model.openai.completion;

import lombok.Data;

import java.util.List;

/**
 * https://beta.openai.com/docs/api-reference/completions/create
 */
@Data
public class CompletionChunk {
    String id;
    String object;
    long created;
    String model;
    List<CompletionChoice> choices;
}
