package com.hoppinzq.model.openai.runs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolCallCodeInterpreter {
    private String input;
    private List<ToolCallCodeInterpreterOutput> outputs;
}
