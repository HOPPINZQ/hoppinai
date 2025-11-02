package com.hoppinzq.model.openai.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * https://platform.openai.com/docs/api-reference/messages/modifyMessage
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ModifyMessageRequest {
    Map<String, String> metadata;
}
