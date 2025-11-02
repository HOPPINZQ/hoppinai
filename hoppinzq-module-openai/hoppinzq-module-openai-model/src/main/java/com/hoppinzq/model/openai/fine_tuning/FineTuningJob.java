package com.hoppinzq.model.openai.fine_tuning;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * https://platform.openai.com/docs/api-reference/fine-tuning/object
 */
@Data
public class FineTuningJob {
    String id;
    String object;
    @JsonProperty("created_at")
    Long createdAt;
    @JsonProperty("finished_at")
    Long finishedAt;
    String model;
    @JsonProperty("fine_tuned_model")
    String fineTunedModel;
    @JsonProperty("organization_id")
    String organizationId;
    String status;
    Hyperparameters hyperparameters;
    @JsonProperty("training_file")
    String trainingFile;
    @JsonProperty("validation_file")
    String validationFile;
    @JsonProperty("result_files")
    List<String> resultFiles;
    @JsonProperty("trained_tokens")
    Integer trainedTokens;
}
