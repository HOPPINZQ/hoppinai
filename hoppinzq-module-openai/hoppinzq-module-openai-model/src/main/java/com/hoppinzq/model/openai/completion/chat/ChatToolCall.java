package com.hoppinzq.model.openai.completion.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatToolCall {
    String id;
    String type;
    ChatFunctionCall function;
}
