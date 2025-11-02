package com.hoppinzq.model.openai.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * https://platform.openai.com/docs/api-reference/messages/createMessage
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageRequest {

    @NonNull
    @Builder.Default
    String role = "user";
    @NonNull
    String content;
    @JsonProperty("file_ids")
    List<String> fileIds;
    Map<String, String> metadata;
}
