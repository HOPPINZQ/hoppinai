package com.hoppinzq.model.openai.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


/**
 * https://platform.openai.com/docs/api-reference/messages/object
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {
    String id;
    String object;
    @JsonProperty("created_at")
    int createdAt;
    @JsonProperty("thread_id")
    String threadId;
    String role;
    List<MessageContent> content;
    @JsonProperty("assistant_id")
    String assistantId;
    @JsonProperty("run_id")
    String runId;
    @JsonProperty("file_ids")
    List<String> fileIds;
    Map<String, String> metadata;
}
