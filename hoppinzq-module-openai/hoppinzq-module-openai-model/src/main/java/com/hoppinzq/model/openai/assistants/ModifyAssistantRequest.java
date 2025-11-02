package com.hoppinzq.model.openai.assistants;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ModifyAssistantRequest {
    String model;
    String name;
    String description;
    String instructions;
    List<Tool> tools;
    @JsonProperty("file_ids")
    List<String> fileIds;
    Map<String, String> metadata;
}
