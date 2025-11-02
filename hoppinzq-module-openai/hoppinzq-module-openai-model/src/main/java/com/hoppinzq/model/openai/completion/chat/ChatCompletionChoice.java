package com.hoppinzq.model.openai.completion.chat;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatCompletionChoice {
    Integer index;
    @JsonAlias("delta")
    ChatMessage message;
    @JsonProperty("finish_reason")
    String finishReason;
}
