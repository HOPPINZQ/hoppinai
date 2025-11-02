package com.hoppinzq.model.openai.completion;

import lombok.Data;

/**
 * https://beta.openai.com/docs/api-reference/completions/create
 */
@Data
public class CompletionChoice {
    String text;
    Integer index;
    LogProbResult logprobs;
    String finish_reason;
}
