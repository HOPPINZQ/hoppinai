package com.hoppinzq.dto;

import com.hoppinzq.model.openai.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenRequestDTO {
    private List<ChatMessage> messages;
    private String message;
    private String reason_message;
    private String model;
}

