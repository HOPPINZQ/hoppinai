package com.hoppinzq.model.openai.assistants;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssistantRequest {
    @NonNull
    String model;
    String name;
    String description;
    String instructions;
    List<Tool> tools;
    @JsonProperty("file_ids")
    List<String> fileIds;
    Map<String, String> metadata;
}
