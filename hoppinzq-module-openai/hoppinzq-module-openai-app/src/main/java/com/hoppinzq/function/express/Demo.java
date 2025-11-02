package com.hoppinzq.function.express;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Demo {

    public static void main(String[] args) throws Exception {
        String express = OpenAiApiFunctionsExpressExample.getExpress("315029360436187", "YD");
        JSONObject expressJSON = JSON.parseObject(express);
        System.out.println(expressJSON.toJavaObject(JSONObject.class));
    }
}

