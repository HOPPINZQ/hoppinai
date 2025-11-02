package com.hoppinzq.openai.service;

import com.hoppinzq.model.openai.completion.chat.ChatFunctionCall;
import com.hoppinzq.model.openai.completion.chat.ChatMessage;

public class ChatMessageAccumulator {

    private final ChatMessage messageChunk;
    private final ChatMessage accumulatedMessage;

    public ChatMessageAccumulator(ChatMessage messageChunk, ChatMessage accumulatedMessage) {
        this.messageChunk = messageChunk;
        this.accumulatedMessage = accumulatedMessage;
    }

    public boolean isFunctionCall() {
        return getAccumulatedMessage().getFunctionCall() != null && getAccumulatedMessage().getFunctionCall().getName() != null;
    }

    public boolean isChatMessage() {
        return !isFunctionCall();
    }

    public ChatMessage getMessageChunk() {
        return messageChunk;
    }

    public ChatMessage getAccumulatedMessage() {
        return accumulatedMessage;
    }

    public ChatFunctionCall getChatFunctionCallChunk() {
        return getMessageChunk().getFunctionCall();
    }

    public ChatFunctionCall getAccumulatedChatFunctionCall() {
        return getAccumulatedMessage().getFunctionCall();
    }
}
