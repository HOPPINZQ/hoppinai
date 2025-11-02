package com.hoppinzq.model.openai.completion.chat;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionChunk {
    String id;
    String object;
    long created;
    String model;
    List<ChatCompletionChoice> choices;
}
