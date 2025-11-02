package com.hoppinzq.model.openai.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * https://beta.openai.com/docs/api-reference/completions/create
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CompletionRequest {
    // 模型名或者微调模型名，必填
    String model;
    String prompt;
    // 插入文本后的后缀
    String suffix;
    // 生成的令牌最大数量
    @JsonProperty("max_tokens")
    Integer maxTokens;
    // 采样温度，介于0到2之间
    Double temperature;
    @JsonProperty("top_p")
    Double topP;
    // 生成文本数量，默认1
    Integer n;
    // 是否流式输出，默认否
    Boolean stream;
    Integer logprobs;
    // 除了完成之外还回显提示符号
    Boolean echo;
    List<String> stop;
    @JsonProperty("presence_penalty")
    Double presencePenalty;
    @JsonProperty("frequency_penalty")
    Double frequencyPenalty;
    @JsonProperty("best_of")
    Integer bestOf;
    @JsonProperty("logit_bias")
    Map<String, Integer> logitBias;
    String user;
}
