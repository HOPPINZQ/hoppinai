package com.hoppinzq.model.openai.runs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoppinzq.model.openai.assistants.Tool;
import com.hoppinzq.model.openai.threads.ThreadRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateThreadAndRunRequest {

    @JsonProperty("assistant_id")
    private String assistantId;

    private ThreadRequest thread;

    private String model;

    private String instructions;

    private List<Tool> tools;

    private Map<String, String> metadata;
}
