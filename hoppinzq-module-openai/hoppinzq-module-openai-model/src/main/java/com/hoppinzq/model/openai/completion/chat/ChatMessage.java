package com.hoppinzq.model.openai.completion.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    /**
     * 只能是system,user,assistant和function
     */
    @NonNull
    private String role;
    @JsonInclude()
    private String content;
    private String name;
    private String tool_call_id;
    @JsonProperty("function_call")
    private ChatFunctionCall function_call;

    @JsonProperty("tool_calls")
    private List<ChatToolCall> tool_calls;

    // Deepseek推理字段
    @JsonInclude()
    private String reasoning_content;

    private Boolean is_end = false;

    private Boolean is_fail = false;

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public ChatMessage(String role, String content, String name) {
        this.role = role;
        this.content = content;
        this.name = name;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatFunctionCall getFunctionCall() {
        return function_call;
    }

    public void setFunctionCall(ChatFunctionCall function_call) {
        this.function_call = function_call;
    }

    public String getReasoning_content() {
        return reasoning_content;
    }

    public void setReasoning_content(String reasoning_content) {
        this.reasoning_content = reasoning_content;
    }

    public List<ChatToolCall> getToolCalls() {
        return tool_calls;
    }

    public void setToolCalls(List<ChatToolCall> tool_calls) {
        this.tool_calls = tool_calls;
    }

    public String getTool_call_id() {
        return tool_call_id;
    }

    public void setTool_call_id(String tool_call_id) {
        this.tool_call_id = tool_call_id;
    }
}
