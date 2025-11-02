package com.hoppinzq.model.openai.fine_tuning;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * https://platform.openai.com/docs/api-reference/fine-tuning/list-events
 */
@Data
public class FineTuningEvent {
    String object;
    String id;
    @JsonProperty("created_at")
    Long createdAt;
    String level;
    String message;
    String type;
}
