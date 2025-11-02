package com.hoppinzq.model.openai.threads;

import com.hoppinzq.model.openai.messages.MessageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 创建一个线程
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThreadRequest {
    private List<MessageRequest> messages;
    private Map<String, String> metadata;
}
