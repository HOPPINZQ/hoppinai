package com.hoppinzq.model.openai.runs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolCallCodeInterpreterOutput {

    private String type;

    private String logs;

    private RunImage image;
}
