package com.hoppinzq.model.openai.threads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * https://platform.openai.com/docs/api-reference/threads/object
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Thread {
    private String id;
    private String object;
    @JsonProperty("created_at")
    private int createdAt;
    private Map<String, String> metadata;
}
