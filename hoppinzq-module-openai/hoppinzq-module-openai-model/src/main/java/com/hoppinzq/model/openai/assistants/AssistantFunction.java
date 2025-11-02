package com.hoppinzq.model.openai.assistants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssistantFunction {
    private String description;
    private String name;
    private Map<String, Object> parameters;
}
