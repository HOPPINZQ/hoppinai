package com.hoppinzq.model.openai.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * https://beta.openai.com/docs/api-reference/create-completion
 */
@Data
public class LogProbResult {
    List<String> tokens;
    @JsonProperty("token_logprobs")
    List<Double> tokenLogprobs;
    @JsonProperty("top_logprobs")
    List<Map<String, Double>> topLogprobs;
    List<Integer> textOffset;
}
