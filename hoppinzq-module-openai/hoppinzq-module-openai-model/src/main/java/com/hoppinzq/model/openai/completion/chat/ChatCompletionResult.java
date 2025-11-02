package com.hoppinzq.model.openai.completion.chat;

import com.hoppinzq.model.openai.Usage;
import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionResult {

    String id;
    String object;
    long created;
    String model;
    List<ChatCompletionChoice> choices;
    Usage usage;
}
