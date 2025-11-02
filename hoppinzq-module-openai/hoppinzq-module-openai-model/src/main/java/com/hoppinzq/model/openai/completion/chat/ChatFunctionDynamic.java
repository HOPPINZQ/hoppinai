package com.hoppinzq.model.openai.completion.chat;

import lombok.Data;
import lombok.NonNull;


@Data
public class ChatFunctionDynamic {
    @NonNull
    private String name;
    private String description;
    private ChatFunctionParameters parameters;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String description;
        private ChatFunctionParameters parameters = new ChatFunctionParameters();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder parameters(ChatFunctionParameters parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder addProperty(ChatFunctionProperty property) {
            this.parameters.addProperty(property);
            return this;
        }

        public ChatFunctionDynamic build() {
            ChatFunctionDynamic chatFunction = new ChatFunctionDynamic(name);
            chatFunction.setDescription(description);
            chatFunction.setParameters(parameters);
            return chatFunction;
        }
    }
}
