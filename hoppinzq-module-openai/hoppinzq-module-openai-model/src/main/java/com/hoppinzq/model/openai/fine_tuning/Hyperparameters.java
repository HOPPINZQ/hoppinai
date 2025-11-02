package com.hoppinzq.model.openai.fine_tuning;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * https://platform.openai.com/docs/api-reference/fine-tuning/object#hyperparameters
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Hyperparameters {
    @JsonProperty("n_epochs")
    Integer nEpochs;
}
