package com.hoppinzq.model.openai.assistants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Tool {
    AssistantToolsEnum type;
    AssistantFunction function;
}
