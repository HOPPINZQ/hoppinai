package com.hoppinzq.function;

import com.alibaba.fastjson.JSONArray;
import com.hoppinzq.model.openai.model.Model;
import com.hoppinzq.openai.service.OpenAiService;

import java.time.Duration;
import java.util.List;

public class OpenAiApiModule {

    public static void main(String... args) {
        //String token = System.getenv("OPENAI_TOKEN");
        String token = "sk-";
        OpenAiService service = new OpenAiService("sk-",
                Duration.ofSeconds(60), "https://api.uchat.site");
        List<Model> models = service.listModels();
        final String s = JSONArray.toJSONString(models);
        System.err.println(s);
        service.shutdownExecutor();
    }
}

