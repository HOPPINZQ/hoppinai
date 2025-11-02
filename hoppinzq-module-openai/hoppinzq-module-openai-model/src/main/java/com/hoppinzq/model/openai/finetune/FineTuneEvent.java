package com.hoppinzq.model.openai.finetune;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * https://beta.openai.com/docs/api-reference/fine-tunes
 */
@Deprecated
@Data
public class FineTuneEvent {
    String object;
    @JsonProperty("created_at")
    Long createdAt;
    String level;
    String message;
}
