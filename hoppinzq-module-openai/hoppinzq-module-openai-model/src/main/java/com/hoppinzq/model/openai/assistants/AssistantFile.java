package com.hoppinzq.model.openai.assistants;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AssistantFile {
    String id;
    String object;
    @JsonProperty("created_at")
    String createdAt;
    @JsonProperty("assistant_id")
    String assistantId;
}
