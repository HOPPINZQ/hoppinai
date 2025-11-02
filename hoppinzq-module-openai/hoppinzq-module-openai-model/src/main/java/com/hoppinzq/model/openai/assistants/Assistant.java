package com.hoppinzq.model.openai.assistants;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Assistant {
    String id;
    String object;
    @JsonProperty("created_at")
    Integer createdAt;
    String name;
    String description;
    @NonNull
    String model;
    String instructions;
    List<Tool> tools;
    @JsonProperty("file_ids")
    List<String> fileIds;
    Map<String, String> metadata;
}
