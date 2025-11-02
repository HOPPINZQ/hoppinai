package com.hoppinzq.model.openai.finetune;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * https://beta.openai.com/docs/api-reference/fine-tunes
 */
@Deprecated
@Data
public class HyperParameters {
    @JsonProperty("batch_size")
    Integer batchSize;
    @JsonProperty("learning_rate_multiplier")
    Double learningRateMultiplier;
    @JsonProperty("n_epochs")
    Integer nEpochs;
    @JsonProperty("prompt_loss_weight")
    Double promptLossWeight;
}
