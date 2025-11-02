package com.hoppinzq.model.openai.completion;

import com.hoppinzq.model.openai.Usage;
import lombok.Data;

import java.util.List;

/**
 * https://beta.openai.com/docs/api-reference/completions/create
 */
@Data
public class CompletionResult {
    String id;
    String object;
    long created;
    String model;

    /**
     * 生成的补全列表
     */
    List<CompletionChoice> choices;

    /**
     * token费用
     */
    Usage usage;
}
