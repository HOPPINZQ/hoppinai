package com.hoppinzq.model.openai.finetune;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoppinzq.model.openai.file.File;
import lombok.Data;

import java.util.List;

/**
 * https://beta.openai.com/docs/api-reference/fine-tunes
 */
@Deprecated
@Data
public class FineTuneResult {
    String id;
    String object;
    String model;
    @JsonProperty("created_at")
    Long createdAt;
    List<FineTuneEvent> events;
    @JsonProperty("fine_tuned_model")
    String fineTunedModel;
    HyperParameters hyperparams;
    @JsonProperty("organization_id")
    String organizationId;
    @JsonProperty("result_files")
    List<File> resultFiles;
    String status;
    @JsonProperty("training_files")
    List<File> trainingFiles;
    @JsonProperty("updated_at")
    Long updatedAt;
    @JsonProperty("validation_files")
    List<File> validationFiles;
}
