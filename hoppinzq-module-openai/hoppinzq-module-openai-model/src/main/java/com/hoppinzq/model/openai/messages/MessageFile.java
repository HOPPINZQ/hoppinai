package com.hoppinzq.model.openai.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * https://platform.openai.com/docs/api-reference/messages/file-object
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageFile {
    String id;
    String object;
    @JsonProperty("created_at")
    int createdAt;
    @JsonProperty("message_id")
    String messageId;
}
