package com.hoppinzq.mcp;

import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.annotation.Inject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WeatherService {
    @Inject
    ChatModel chatModel;
    
    public String askWeather(String question) throws IOException {
        ChatResponse response = chatModel.prompt(question).call();
        return response.getChoices().get(0).getMessage().getContent();
    }
}