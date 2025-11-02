package com.hoppinzq.function.zq.constants;

import com.hoppinzq.openai.service.OpenAiService;

public class FunctionCallCommon {

    public static final String HAMIBOT_TOKEN = "";
    public static final String HAMIBOT_MOBILE_TITLE = "张祺的小AI";
    public static final String openaiProxy = "https://api.mixrai.com";
    public static final String apiKey = "sk-";
    public static final String model = "gpt-4o-mini";
    public static final String embedding_model = "text-embedding-ada-002";

    public static final String music_path = "D:\\CloudMusic";

    public static ThreadLocal<String> cookie = new ThreadLocal<String>();
    public static ThreadLocal<OpenAiService> serviceThreadLocal = new ThreadLocal<OpenAiService>();

}

