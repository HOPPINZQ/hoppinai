package com.hoppinzq.model.openai.fine_tuning;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


/**
 * https://platform.openai.com/docs/api-reference/fine-tuning/create
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FineTuningJobRequest {
    @NonNull
    @JsonProperty("training_file")
    String trainingFile;
    @JsonProperty("validation_file")
    String validationFile;
    @NonNull
    String model;
    Hyperparameters hyperparameters;
    String suffix;
}
